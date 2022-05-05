package cloud.matzat.aws.mailimport.messaging;

import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import io.awspring.cloud.messaging.listener.Acknowledgment;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import cloud.matzat.aws.mailimport.messaging.model.Message;
import cloud.matzat.aws.mailimport.messaging.model.Receipt;
import cloud.matzat.aws.mailimport.persistence.domain.Email;
import cloud.matzat.aws.mailimport.persistence.domain.EmailRepository;
import cloud.matzat.aws.mailimport.service.LockAlreadyTakenException;
import cloud.matzat.aws.mailimport.service.LockService;
import cloud.matzat.aws.mailimport.service.MimeMessageService;
import cloud.matzat.aws.mailimport.service.S3BucketService;
import cloud.matzat.aws.mailimport.service.S3ObjectNotFoundException;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;

/**
 * SQS-queuelistener to process incomming SQS-messages, read matching S3-data, parse Mail
 * and store it on the database.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@Component
public class QueueListener {

    private static final String MESSAGE = "Message";
    private final Timer timer;
    private final Counter counterSuccess;
    private final Counter counterTotal;
    private final LockService lockService;
    private final ObjectMapper objectMapper;
    private final S3BucketService s3BucketService;
    private final MimeMessageService mimeMessageService;
    private final EmailRepository emailRepository;
    @Value("${configuration.s3.path-prefix.success}")
    private String successPrefix;
    @Value("${configuration.s3.path-prefix.error}")
    private String errorPrefix;

    public QueueListener(
        LockService lockService,
        ObjectMapper objectMapper,
        S3BucketService s3BucketService,
        MimeMessageService mimeMessageService,
        MeterRegistry meterRegistry,
        EmailRepository emailRepository
    ) {
        this.lockService = lockService;
        this.objectMapper = objectMapper;
        this.s3BucketService = s3BucketService;
        this.mimeMessageService = mimeMessageService;
        this.emailRepository = emailRepository;

        this.counterSuccess = meterRegistry.counter("message.count.success");
        this.counterTotal = meterRegistry.counter("message.count.total");
        this.timer = meterRegistry.timer("message.time");
    }

    @SqsListener(value = "${configuration.sqs.success.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void onMailIncomingEvent(
        @Payload String payload,
        @Header("MessageId") String messageId,
        Acknowledgment acknowledgment
    ) {
        final long start = Instant.now().toEpochMilli();
        log.info("Incoming Message with ID: {}", messageId);
        try {
            lockService.aquireLock(messageId);

            @SuppressWarnings("unchecked")
            Map<String, String> map = objectMapper.readValue(payload, Map.class);
            if (map.containsKey(MESSAGE)) {
                handleMessage(messageId, map);
            } else {
                log.info("Ommiting message with ID {} - No Mail-Content", messageId);
                lockService.releaseLock(messageId);
            }
            acknowledgment.acknowledge();
        } catch (MismatchedInputException e) {
            log.info("Input Message with ID {} could not parsed - skip", messageId);
            lockService.releaseLock(messageId);
        } catch (LockAlreadyTakenException e) {
            log.warn("Message with ID {} already in process - redrive", messageId);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON", e);
            lockService.releaseLock(messageId);
        } finally {
            timer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
            counterTotal.increment();
        }
    }

    private void handleMessage(String messageId, Map<String, String> map) throws JsonProcessingException {
        final String rawMessage = map.get(MESSAGE);
        final Message message = objectMapper.readValue(rawMessage, Message.class);

        log.info("Incoming Mail: \n{}", message);

        if (message.getReceipt() != null && message.getReceipt().getAction() != null) {

            Receipt.Action action = message.getReceipt().getAction();
            String s3MessageContent = null;
            String objectKey = action.getObjectKey();
            String bucketName = action.getBucketName();
            try {
                s3MessageContent = s3BucketService.readS3Resource(
                    bucketName,
                    objectKey
                );

                MimeMessage mimeMessage = mimeMessageService.parseMimeMessage(s3MessageContent);
                MimeMessageParser parser = new MimeMessageParser(mimeMessage).parse();
                Email email = Email.builder()
                                   .guid(UUID.randomUUID().toString())
                                   .dateReceived(message.getMail()
                                                        .getTimestamp()
                                                        .atZone(ZoneId.of("Europe/Paris"))
                                                        .toLocalDateTime())
                                   .fromEMail(parser.getFrom())
                                   .fromName(extractName(mimeMessage.getFrom()))
                                   .mailTo(parser.getTo()
                                                 .stream()
                                                 .map(Address::toString)
                                                 .collect(Collectors.joining(",")))
                                   .cc(parser.getCc()
                                             .stream()
                                             .map(Address::toString)
                                             .collect(Collectors.joining(",")))
                                   .bcc(parser.getBcc()
                                              .stream()
                                              .map(Address::toString)
                                              .collect(Collectors.joining(",")))
                                   .subject(parser.getSubject())
                                   .normalizedSubject(parser.getSubject().toLowerCase())
                                   .htmlMail(parser.hasHtmlContent())
                                   .htmlContent(parser.getHtmlContent())
                                   .textContent(parser.getPlainContent())
                                   .contentMediaType(mimeMessage.getContentType())
                                   .messageId(parser.getMimeMessage().getMessageID())
                                   .build();

                emailRepository.save(email);
                moveMessage(bucketName, successPrefix, objectKey, s3MessageContent);
                counterSuccess.increment();
            } catch (S3ObjectNotFoundException e) {
                log.warn("S3 Object with key {} not found", objectKey);
            } catch (Exception e) {
                log.error("Error parsing MimeMessage");
                moveMessage(bucketName, errorPrefix, objectKey, s3MessageContent);
            } finally {
                lockService.releaseLock(messageId);
            }
        } else {
            log.info("Ommiting message with ID {} - No Mail-Content", messageId);
            lockService.releaseLock(messageId);
        }
    }

    private void moveMessage(String bucketName, String prefix, String objectKey, String objectContent) {
        s3BucketService.writeS3Resource(
            bucketName,
            prefix + objectKey,
            objectContent
        );
        s3BucketService.deleteS3Resource(bucketName, objectKey);
    }

    private String extractName(final Address[] fromAdresses) {

        if (fromAdresses == null || fromAdresses.length == 0) {
            return "";
        }
        final String from = fromAdresses[0].toString();
        if (from.contains("<")) {
            return from.substring(0, from.indexOf("<") - 1).replace("\"", "");
        }
        return "";
    }

}
