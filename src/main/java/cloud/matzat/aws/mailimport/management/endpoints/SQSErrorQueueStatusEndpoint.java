package cloud.matzat.aws.mailimport.management.endpoints;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;

import cloud.matzat.aws.mailimport.service.SQSQueueService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of QueueStatusEndpoint for the Errorqueue.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@Endpoint(id = "queue.status.error")
public class SQSErrorQueueStatusEndpoint extends AbstractQueueStatusEndpoint {

    public SQSErrorQueueStatusEndpoint(SQSQueueService sqsQueueService, String queueName) {
        this.sqsQueueService = sqsQueueService;
        this.queueName = queueName;
    }
}
