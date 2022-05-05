package cloud.matzat.aws.mailimport;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cloud.matzat.aws.mailimport.dynamodb.DynamoDBTestConfig;
import cloud.matzat.aws.mailimport.test.configuration.AwsTestConfig;
import cloud.matzat.aws.mailimport.test.configuration.S3TestConfig;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unittest for {@link MailimportApplicationTests}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@SpringBootTest(classes = { AwsTestConfig.class, S3TestConfig.class, DynamoDBTestConfig.class })
@ActiveProfiles("test")
class MailimportApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
        }, "Context not loaded.");
        log.info("Context loaded.");
    }

}
