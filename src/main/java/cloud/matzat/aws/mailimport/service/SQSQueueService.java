package cloud.matzat.aws.mailimport.service;

import org.springframework.stereotype.Service;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;

import cloud.matzat.aws.mailimport.service.model.SQSQueueStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service to get queue attributes of a SQS queue.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Service
public class SQSQueueService {

    private final AmazonSQSAsync amazonSQSClient;

    public SQSQueueService(AmazonSQSAsync amazonSQSClient) {
        this.amazonSQSClient = amazonSQSClient;
    }

    public SQSQueueStatus getSqsQueueStatus(String queueName) {
        final List<String> queueAttributeNames = new ArrayList<>();
        Collections.addAll(
            queueAttributeNames,
            QueueAttributeName.ApproximateNumberOfMessages.name(),
            QueueAttributeName.ApproximateNumberOfMessagesDelayed.name(),
            QueueAttributeName.ApproximateNumberOfMessagesNotVisible.name()
        );
        final GetQueueAttributesResult queueAttributes =
            amazonSQSClient.getQueueAttributes(queueName, queueAttributeNames);
        final Map<String, String> queueAttributeMap = queueAttributes.getAttributes();
        return new SQSQueueStatus(queueAttributeMap);
    }
}
