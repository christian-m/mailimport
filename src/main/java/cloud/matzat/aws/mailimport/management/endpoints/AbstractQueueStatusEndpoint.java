package cloud.matzat.aws.mailimport.management.endpoints;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import cloud.matzat.aws.mailimport.service.SQSQueueService;
import cloud.matzat.aws.mailimport.service.model.SQSQueueStatus;

/**
 * Common Class Implementation for QueueStatusEndpoints.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
public class AbstractQueueStatusEndpoint {

    protected String queueName;
    protected SQSQueueService sqsQueueService;

    @ReadOperation
    public SQSQueueStatus getQueueStatus() {
        return sqsQueueService.getSqsQueueStatus(queueName);
    }
}
