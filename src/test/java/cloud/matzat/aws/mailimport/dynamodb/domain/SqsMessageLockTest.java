package cloud.matzat.aws.mailimport.dynamodb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cloud.matzat.aws.mailimport.dynamodb.DynamoDBTestConfig;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unittest for {@link SqsMessageLockRepository} and {@link SqsMessageLock}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@SpringBootTest(classes = { PropertyPlaceholderAutoConfiguration.class, DynamoDBTestConfig.class })
@ActiveProfiles("test")
public class SqsMessageLockTest {

    private static final String LOCK_ID = "LOCK_ID";

    @Autowired
    private SqsMessageLockRepository sqsMessageLockRepository;

    @BeforeEach
    public void init() throws Exception {
        sqsMessageLockRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
        assertThat(sqsMessageLockRepository).isNotNull();
    }

    @Test
    public void saveSqsMessageLockTestCase() {
        final SqsMessageLock sqsMessageLock = new SqsMessageLock(LOCK_ID);
        sqsMessageLockRepository.save(sqsMessageLock);

        final Optional<SqsMessageLock> messsageLock = sqsMessageLockRepository.findById(LOCK_ID);
        assertThat(messsageLock).isPresent();
        assertThat(messsageLock.get().getLockId()).isEqualTo(LOCK_ID);
        assertThat(messsageLock.get().getLockedAt()).isNotNull();
        assertThat(messsageLock.get().getLockedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    public void deleteSqsMessageLockTestCase() {
        final SqsMessageLock sqsMessageLock = new SqsMessageLock(LOCK_ID);
        sqsMessageLockRepository.save(sqsMessageLock);

        final Optional<SqsMessageLock> sqsMessageLockToDelete = sqsMessageLockRepository.findById(LOCK_ID);
        assertThat(sqsMessageLockToDelete).isPresent();
        sqsMessageLockRepository.deleteById(LOCK_ID);

        final Optional<SqsMessageLock> deletedMessageLock = sqsMessageLockRepository.findById(LOCK_ID);
        assertThat(deletedMessageLock).isEmpty();
    }
}
