package cloud.matzat.aws.mailimport.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import cloud.matzat.aws.mailimport.test.configuration.S3TestConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unittest for {@link S3BucketService}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SpringBootTest(classes = { S3BucketService.class, S3TestConfig.class })
@ActiveProfiles("test")
public class S3BucketServiceTest {

    private static final String OBJECT_KEY = "test-object";
    private static final String OBJECT_KEY_NOT_FOUND = "test-object-not-found";
    private static final String OBJECT_CONTENT = "test-content";

    @Value("${configuration.s3.bucket-name}")
    private String bucketName;

    // Different bucketLocation due to restrictions of s3mock
    private String bucketLocation = "US";

    @Autowired
    private S3BucketService s3BucketService;

    @Autowired
    private AmazonS3 amazonS3;

    @BeforeEach
    public void setup() {
        final List<S3ObjectSummary> summaryList = amazonS3.listObjects(bucketName).getObjectSummaries();
        String[] keys = summaryList.stream().map(S3ObjectSummary::getKey).toArray(String[]::new);
        final DeleteObjectsRequest deleteObjectRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);
        amazonS3.deleteObjects(deleteObjectRequest);
        amazonS3.putObject(bucketName, OBJECT_KEY, OBJECT_CONTENT);
    }

    @Test
    public void contextLoads() {
        assertThat(s3BucketService).isNotNull();
    }

    @Test
    public void readS3ResourceFoundTest() {
        assertDoesNotThrow(() -> {
            String s3Content = s3BucketService.readS3Resource(bucketName, OBJECT_KEY);
            assertThat(s3Content).isNotEmpty().isEqualTo(OBJECT_CONTENT);
        }, "Writen Object not readable");
    }

    @Test
    public void readS3ResourceNotFoundTest() {
        assertThrows(S3ObjectNotFoundException.class, () -> {
            s3BucketService.readS3Resource(bucketName, OBJECT_KEY_NOT_FOUND);
        }, "Unexpected Object was found");
    }

    @Test
    public void writeS3ResourceTest() {
        final String newObjectKey = "new_object_key";
        assertDoesNotThrow(() -> {
            s3BucketService.writeS3Resource(bucketName, newObjectKey, OBJECT_CONTENT);
        }, "Write S3 Object not successfull");

        assertDoesNotThrow(() -> {
            String s3Content = s3BucketService.readS3Resource(bucketName, newObjectKey);
            assertThat(s3Content).isNotEmpty().isEqualTo(OBJECT_CONTENT);
        }, "Writen Object not readable");
    }

    @Test
    public void deleteS3ResourceTest() {
        assertDoesNotThrow(() -> {
            s3BucketService.deleteS3Resource(bucketName, OBJECT_KEY);
        }, "Delete S3 Object not successfull");

        assertThrows(S3ObjectNotFoundException.class, () -> {
            String s3Content = s3BucketService.readS3Resource(bucketName, OBJECT_KEY);
        }, "Deleted Object still readable");
    }

    @Test
    public void getS3ResourceUrlTest() {
        String s3ResourceUrl = s3BucketService.getS3ResourceUrl(bucketName, OBJECT_KEY);
        assertThat(s3ResourceUrl).isEqualTo(String.format("s3://%s/%s", bucketName, OBJECT_KEY));
    }

    @Test
    public void getS3BucketLocationTest() {
        String bucketLocation = s3BucketService.getS3BucketLocation(bucketName);
        assertThat(bucketLocation).isEqualTo(String.format(
            "https://%s.s3.%s.amazonaws.com",
            bucketName,
            this.bucketLocation
        ));
    }

    @Test
    public void listObjectsTest() {
        ObjectListing objectListingToCheck = s3BucketService.listObjects(bucketName);
        assertThat(objectListingToCheck).isNotNull();
        assertThat(objectListingToCheck.getObjectSummaries()).isNotNull().hasSize(1);
    }

    @Test
    public void findObjectFoundTest() {
        Optional<S3ObjectSummary> objectSummary = s3BucketService.findObject(bucketName, OBJECT_KEY);
        assertThat(objectSummary).isPresent();
        assertThat(objectSummary.get().getKey()).isEqualTo(OBJECT_KEY);
    }

    @Test
    public void findObjectNotFoundTest() {
        Optional<S3ObjectSummary> objectSummary = s3BucketService.findObject(bucketName, OBJECT_KEY_NOT_FOUND);
        assertThat(objectSummary).isEmpty();
    }
}
