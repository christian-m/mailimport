package cloud.matzat.aws.mailimport.management;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.context.annotation.Bean;

import cloud.matzat.aws.mailimport.management.endpoints.SQSErrorDlQueueStatusEndpoint;
import cloud.matzat.aws.mailimport.management.endpoints.SQSErrorQueueStatusEndpoint;
import cloud.matzat.aws.mailimport.management.endpoints.SQSSuccessDlQueueStatusEndpoint;
import cloud.matzat.aws.mailimport.management.endpoints.SQSSuccessQueueStatusEndpoint;
import cloud.matzat.aws.mailimport.service.SQSQueueService;

/**
 * Configuration of Custom Endpoints.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@ManagementContextConfiguration
public class CustomEndpointConfig {

    private final SQSQueueService sqsQueueService;

    @Value("${configuration.sqs.success.queue-name}")
    private String successQueueName;
    @Value("${configuration.sqs.success.dl-queue-name}")
    private String successDlQueueName;
    @Value("${configuration.sqs.error.queue-name}")
    private String errorQueueName;
    @Value("${configuration.sqs.error.dl-queue-name}")
    private String errorDlQueueName;

    public CustomEndpointConfig(SQSQueueService sqsQueueService) {
        this.sqsQueueService = sqsQueueService;
    }

    @Bean
    public SQSSuccessQueueStatusEndpoint successQueueStatusEndpoint() {
        return new SQSSuccessQueueStatusEndpoint(sqsQueueService, successQueueName);
    }

    @Bean
    public SQSSuccessDlQueueStatusEndpoint successDlQueueStatusEndpoint() {
        return new SQSSuccessDlQueueStatusEndpoint(sqsQueueService, successDlQueueName);
    }

    @Bean
    public SQSErrorQueueStatusEndpoint errorQueueStatusEndpoint() {
        return new SQSErrorQueueStatusEndpoint(sqsQueueService, errorQueueName);
    }

    @Bean
    public SQSErrorDlQueueStatusEndpoint errorDlQueueStatusEndpoint() {
        return new SQSErrorDlQueueStatusEndpoint(sqsQueueService, errorDlQueueName);
    }
}
