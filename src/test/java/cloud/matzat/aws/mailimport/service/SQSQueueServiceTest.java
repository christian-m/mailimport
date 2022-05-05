package cloud.matzat.aws.mailimport.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.amazonaws.services.sqs.AmazonSQSAsync;

import cloud.matzat.aws.mailimport.service.model.SQSQueueStatus;
import cloud.matzat.aws.mailimport.test.utils.AWSTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unittest for {@link SQSQueueService}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SpringBootTest(classes = { SQSQueueService.class })
@ActiveProfiles("test")
public class SQSQueueServiceTest {

    private static final String QUEUE_NAME = "test-queue";

    @MockBean
    public AmazonSQSAsync amazonSQS;

    @Autowired
    private SQSQueueService sqsQueueService;

    @BeforeEach
    public void setup() {
        given(amazonSQS.getQueueAttributes(QUEUE_NAME, AWSTestUtils.getQueueAttributeNames()))
            .willReturn(AWSTestUtils.getQueueAttributesResult());
    }

    @Test
    public void contextLoads() {
        assertThat(sqsQueueService).isNotNull();
    }

    @Test
    public void getSQSQuewueStatusTest() {
        SQSQueueStatus sqsQueueStatus = sqsQueueService.getSqsQueueStatus(QUEUE_NAME);
        assertThat(sqsQueueStatus.getMessagesQueued()).isEqualTo("15");
        assertThat(sqsQueueStatus.getMessagesDelayed()).isEqualTo("20");
        assertThat(sqsQueueStatus.getMessagesInDelivery()).isEqualTo("30");
    }
}
