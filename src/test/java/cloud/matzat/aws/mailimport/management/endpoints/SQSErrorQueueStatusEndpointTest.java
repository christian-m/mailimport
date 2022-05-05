package cloud.matzat.aws.mailimport.management.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.amazonaws.services.sqs.AmazonSQSAsync;

import cloud.matzat.aws.mailimport.management.CustomEndpointConfig;
import cloud.matzat.aws.mailimport.service.SQSQueueService;
import cloud.matzat.aws.mailimport.service.model.SQSQueueStatus;
import cloud.matzat.aws.mailimport.test.utils.AWSTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unittest for {@link SQSErrorQueueStatusEndpoint}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SpringBootTest(classes = { SQSQueueService.class, CustomEndpointConfig.class })
@ActiveProfiles("test")
public class SQSErrorQueueStatusEndpointTest {

    @MockBean
    public AmazonSQSAsync amazonSQS;

    @Value("${configuration.sqs.error.queue-name}")
    private String queueName;

    @Autowired
    private SQSErrorQueueStatusEndpoint sqsErrorQueueStatusEndpoint;

    @BeforeEach
    public void setup() {
        given(amazonSQS.getQueueAttributes(queueName, AWSTestUtils.getQueueAttributeNames()))
            .willReturn(AWSTestUtils.getQueueAttributesResult());
    }

    @Test
    public void contextLoads() {
        assertThat(sqsErrorQueueStatusEndpoint).isNotNull();
    }

    @Test
    public void setSqsErrorDlQueueStatusEndpointTest() {
        SQSQueueStatus queueStatus = sqsErrorQueueStatusEndpoint.getQueueStatus();
        assertThat(queueStatus.getMessagesQueued()).isEqualTo("15");
        assertThat(queueStatus.getMessagesDelayed()).isEqualTo("20");
        assertThat(queueStatus.getMessagesInDelivery()).isEqualTo("30");
    }

}
