package cloud.matzat.aws.mailimport.test.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.SocketUtils;
import io.findify.s3mock.S3Mock;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Test configuration for S3.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@TestConfiguration
public class S3TestConfig {

    private final int port;
    @Value("${configuration.s3.endpoint}")
    private String s3Endpoint;
    @Value("${cloud.aws.region.static}")
    private String s3Region;
    @Value("${configuration.s3.bucket-name}")
    private String s3Bucket;

    public S3TestConfig() {
        port = SocketUtils.findAvailableTcpPort();
        S3Mock api = new S3Mock.Builder().withPort(port).withInMemoryBackend().build();
        api.start();
    }

    @Bean(destroyMethod = "shutdown")
    @Primary
    public AmazonS3 amazonS3Client() {
        final AwsClientBuilder.EndpointConfiguration endpoint =
            new AwsClientBuilder.EndpointConfiguration(s3Endpoint + ":" + port, s3Region);
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder
            .standard()
            .withPathStyleAccessEnabled(true)
            .withEndpointConfiguration(endpoint)
            .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
            .build();
        amazonS3.createBucket(s3Bucket);
        return amazonS3;
    }
}
