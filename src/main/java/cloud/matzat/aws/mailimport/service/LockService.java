package cloud.matzat.aws.mailimport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import cloud.matzat.aws.mailimport.dynamodb.domain.SqsMessageLock;
import cloud.matzat.aws.mailimport.dynamodb.domain.SqsMessageLockRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to lock parallel message processing.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@Service
public class LockService {

    private final SqsMessageLockRepository sqsMessageLockRepository;

    public LockService(SqsMessageLockRepository sqsMessageLockRepository) {
        this.sqsMessageLockRepository = sqsMessageLockRepository;
    }

    @Transactional(isolation = Isolation.DEFAULT)
    public void aquireLock(String messageId) {
        if (!sqsMessageLockRepository.findById(messageId).isEmpty()) {
            log.warn("Lock for {} already taken!", messageId);
            throw new LockAlreadyTakenException(String.format("Lock for %s already taken!", messageId));
        }

        final SqsMessageLock messageLock = new SqsMessageLock(messageId);
        sqsMessageLockRepository.save(messageLock);
        log.debug("Take Lock for {}", messageId);
    }

    @Transactional(isolation = Isolation.DEFAULT)
    public void releaseLock(String messageId) {
        sqsMessageLockRepository.deleteById(messageId);
        log.debug("Lock for {} released", messageId);
    }
}

