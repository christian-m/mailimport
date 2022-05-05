package cloud.matzat.aws.mailimport.test.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;

/**
 * Test configuration for AWS components.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@TestConfiguration
public class AwsTestConfig {

    @MockBean
    public SimpleMessageListenerContainer simpleMessageListenerContainer;
}
