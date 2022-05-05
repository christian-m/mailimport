package cloud.matzat.aws.mailimport.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ObjectUtils;
import io.awspring.cloud.autoconfigure.context.properties.AwsRegionProperties;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverterFactory;

import lombok.extern.slf4j.Slf4j;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement;

/**
 * Configuration Class for DynamoDB Repositories.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@Configuration
@EnableDynamoDBRepositories(basePackages = "cloud.matzat.aws.mailimport.dynamodb.domain")
public class DynamoDBConfig {

    @Value("${configuration.dynamodb.table-name}")
    private String tableName;

    @Bean
    public AmazonDynamoDB amazonDynamoDB(
        @Value("${configuration.dynamodb.endpoint}") String amazonDynamoDBEndpoint,
        AwsRegionProperties regionProperties
    ) {
        AmazonDynamoDBClientBuilder dynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard();
        if (!ObjectUtils.isEmpty(amazonDynamoDBEndpoint)) {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, regionProperties.getStatic());
            dynamoDBClientBuilder.setEndpointConfiguration(endpointConfiguration);
        }
        log.info("DynamoDB Connection: initialization completed");
        return dynamoDBClientBuilder.build();
    }

    @Bean
    @Primary
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        return new DynamoDBMapperConfig.Builder()
            .withTableNameOverride(withTableNameReplacement(tableName))
            .withTypeConverterFactory(DynamoDBTypeConverterFactory.standard())
            .build();
    }
}
