package cloud.matzat.aws.mailimport.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverterFactory;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement;

/**
 * Testconfiguration for DynamoDB.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@TestConfiguration
@EnableDynamoDBRepositories(basePackages = "cloud.matzat.aws.mailimport.dynamodb.domain")
public class DynamoDBTestConfig {

    @Value("${configuration.dynamodb.table-name}")
    private String tableName;

    @Bean(destroyMethod = "shutdown")
    @Primary
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDBLocal dynamoDBLocal = DynamoDBEmbedded.create();
        return dynamoDBLocal.amazonDynamoDB();
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
