package cloud.matzat.aws.mailimport.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cloud.matzat.aws.mailimport.dynamodb.DynamoDBTestConfig;
import cloud.matzat.aws.mailimport.dynamodb.domain.SqsMessageLock;
import cloud.matzat.aws.mailimport.dynamodb.domain.SqsMessageLockRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unittest for {@link LockService}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SpringBootTest(classes = { PropertyPlaceholderAutoConfiguration.class, DynamoDBTestConfig.class, LockService.class })
@ActiveProfiles("test")
public class LockServiceTest {

    private static final String LOCK_ID = "LOCK_ID";

    @Autowired
    private SqsMessageLockRepository sqsMessageLockRepository;
    @Autowired
    private LockService lockService;

    @BeforeEach
    public void init() throws Exception {
        sqsMessageLockRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
        assertThat(sqsMessageLockRepository).isNotNull();
        assertThat(lockService).isNotNull();
    }

    @Test
    public void aquireLockTest() {
        assertDoesNotThrow(() -> {
            lockService.aquireLock(LOCK_ID);
        }, "Lock already taken.");
        final Optional<SqsMessageLock> messageLock = sqsMessageLockRepository.findById(LOCK_ID);
        assertThat(messageLock).isPresent();
        assertThat(messageLock.get().getLockId()).isEqualTo(LOCK_ID);
    }

    @Test
    public void lockAlreadyTakenTest() {
        lockService.aquireLock(LOCK_ID);
        assertThrows(LockAlreadyTakenException.class, () -> {
            lockService.aquireLock(LOCK_ID);
        }, "Lock was not already taken.");
    }

    @Test
    public void releaseLockTest() {
        lockService.aquireLock(LOCK_ID);
        final Optional<SqsMessageLock> messageLock = sqsMessageLockRepository.findById(LOCK_ID);
        assertThat(messageLock).isPresent();
        assertThat(messageLock.get().getLockId()).isEqualTo(LOCK_ID);
        lockService.releaseLock(LOCK_ID);
        final Optional<SqsMessageLock> releasedMessageLock = sqsMessageLockRepository.findById(LOCK_ID);
        assertThat(releasedMessageLock).isEmpty();
    }
}
