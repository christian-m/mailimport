package cloud.matzat.aws.mailimport.service.model;

import com.amazonaws.services.sqs.model.QueueAttributeName;

import lombok.Data;

import java.util.Map;

/**
 * DTO for the queue status of a SQS-Queue.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Data
public class SQSQueueStatus {

    private final String messagesQueued;
    private final String messagesDelayed;
    private final String messagesInDelivery;

    public SQSQueueStatus(Map<String, String> queueAttributes) {
        messagesQueued = queueAttributes.get(QueueAttributeName.ApproximateNumberOfMessages.name());
        messagesDelayed = queueAttributes.get(QueueAttributeName.ApproximateNumberOfMessagesDelayed.name());
        messagesInDelivery = queueAttributes.get(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.name());
    }
}
