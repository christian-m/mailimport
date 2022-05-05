package cloud.matzat.aws.mailimport.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import io.awspring.cloud.messaging.listener.Acknowledgment;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import cloud.matzat.aws.mailimport.dynamodb.DynamoDBTestConfig;
import cloud.matzat.aws.mailimport.dynamodb.domain.SqsMessageLock;
import cloud.matzat.aws.mailimport.dynamodb.domain.SqsMessageLockRepository;
import cloud.matzat.aws.mailimport.persistence.domain.EmailRepository;
import cloud.matzat.aws.mailimport.service.LockService;
import cloud.matzat.aws.mailimport.service.MimeMessageService;
import cloud.matzat.aws.mailimport.service.ObjectMapperService;
import cloud.matzat.aws.mailimport.service.S3BucketService;
import cloud.matzat.aws.mailimport.test.configuration.MeterRegistryTestConfig;
import cloud.matzat.aws.mailimport.test.configuration.S3TestConfig;
import cloud.matzat.aws.mailimport.test.utils.TestUtils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unittest for {@link QueueListener}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@SpringBootTest(classes = {
    S3TestConfig.class, DynamoDBTestConfig.class, MeterRegistryTestConfig.class,
    QueueListener.class, LockService.class, ObjectMapperService.class, S3BucketService.class,
    MimeMessageService.class, EmailRepository.class
})
@ActiveProfiles("test")
public class QueueListenerTest {

    private static final String MESSAGE_ID_HEADER = "905c2cd1-956a-5963-8654-7476a5075e41";
    private static final String OBJECT_KEY = "9g5sj2g198oc8iq642eaof3nponm8mvckce8pcg1";

    @MockBean
    public EmailRepository emailRepository;

    @Value("${configuration.s3.path-prefix.success}")
    private String successPrefix;

    @Value("${configuration.s3.bucket-name}")
    private String bucketName;

    @Autowired
    private QueueListener queueListener;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private SqsMessageLockRepository sqsMessageLockRepository;

    private String payload;
    private TestAcknowledgment acknowledgment;

    @BeforeEach
    public void setup() throws IOException {
        final List<S3ObjectSummary> summaryList = amazonS3.listObjects(bucketName).getObjectSummaries();
        String[] keys = summaryList.stream().map(S3ObjectSummary::getKey).toArray(String[]::new);
        final DeleteObjectsRequest deleteObjectRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);
        amazonS3.deleteObjects(deleteObjectRequest);
        String objectContent = TestUtils.readFile("cloud/matzat/aws/mailimport/service/mimeMessage.txt");
        amazonS3.putObject(bucketName, OBJECT_KEY, objectContent);

        acknowledgment = new TestAcknowledgment();
    }

    @Test
    public void validMessageIncommingTest() throws IOException {
        payload = TestUtils.readFile("cloud/matzat/aws/mailimport/messaging/sqsmessage.txt");
        queueListener.onMailIncomingEvent(payload, MESSAGE_ID_HEADER, acknowledgment);
        assertThat(acknowledgment.isAcknowledged()).isTrue();
        Optional<S3ObjectSummary> objectSummary = resolveS3Object(OBJECT_KEY);
        assertThat(objectSummary).isEmpty();
        Optional<S3ObjectSummary> objectSummaryMoved = resolveS3Object(successPrefix + OBJECT_KEY);
        assertThat(objectSummaryMoved).isPresent();
        assertThat(objectSummaryMoved.get().getSize()).isGreaterThan(0L);
        Optional<SqsMessageLock> messageLock = sqsMessageLockRepository.findById(MESSAGE_ID_HEADER);
        assertThat(messageLock).isEmpty();
    }

    @Test
    public void emptyMessageIncommingTest() throws IOException {
        payload = TestUtils.readFile("cloud/matzat/aws/mailimport/messaging/sqsmessage_empty.txt");
        queueListener.onMailIncomingEvent(payload, MESSAGE_ID_HEADER, acknowledgment);
        assertThat(acknowledgment.isAcknowledged()).isFalse();
        Optional<S3ObjectSummary> objectSummary = resolveS3Object(successPrefix + OBJECT_KEY);
        assertThat(objectSummary).isEmpty();
        Optional<SqsMessageLock> messageLock = sqsMessageLockRepository.findById(MESSAGE_ID_HEADER);
        assertThat(messageLock).isEmpty();
    }

    @Test
    public void noMailMessageIncommingTest() throws IOException {
        payload = TestUtils.readFile("cloud/matzat/aws/mailimport/messaging/sqsmessage_nomailcontent.txt");
        queueListener.onMailIncomingEvent(payload, MESSAGE_ID_HEADER, acknowledgment);
        assertThat(acknowledgment.isAcknowledged()).isTrue();
        Optional<S3ObjectSummary> objectSummary = resolveS3Object(successPrefix + OBJECT_KEY);
        assertThat(objectSummary).isEmpty();
        Optional<SqsMessageLock> messageLock = sqsMessageLockRepository.findById(MESSAGE_ID_HEADER);
        assertThat(messageLock).isEmpty();
    }

    private Optional<S3ObjectSummary> resolveS3Object(String objectKey) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        List<S3ObjectSummary> objectSummary = objectListing.getObjectSummaries();
        return objectSummary.stream().filter(s3ObjectSummary -> s3ObjectSummary.getKey().equals(objectKey)).findFirst();
    }
}

class TestAcknowledgment implements Acknowledgment {

    boolean acknowledged;

    @Override
    public Future<?> acknowledge() {
        acknowledged = true;
        return null;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }
}
