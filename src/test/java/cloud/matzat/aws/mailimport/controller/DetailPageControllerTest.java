package cloud.matzat.aws.mailimport.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.ModelAndView;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import cloud.matzat.aws.mailimport.service.S3BucketService;
import cloud.matzat.aws.mailimport.test.configuration.S3TestConfig;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unittest for {@link DetailPageController}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SpringBootTest(classes = { S3BucketService.class, S3TestConfig.class, DetailPageController.class })
@ActiveProfiles("test")
public class DetailPageControllerTest {

    private static final String OBJECT_KEY = "test-object";
    private static final String OBJECT_KEY_NOT_FOUND = "test-object-not-found";
    private static final String OBJECT_CONTENT = "test-content";

    @Value("${configuration.s3.bucket-name}")
    private String bucketName = "test-bucket";

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private DetailPageController detailPageController;

    @BeforeEach
    public void setup() {
        final List<S3ObjectSummary> summaryList = amazonS3.listObjects(bucketName).getObjectSummaries();
        String[] keys = summaryList.stream()
                                   .map(S3ObjectSummary::getKey)
                                   .collect(Collectors.toList())
                                   .toArray(new String[0]);
        final DeleteObjectsRequest deleteObjectRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);
        amazonS3.deleteObjects(deleteObjectRequest);
        amazonS3.listObjects(bucketName);
        amazonS3.putObject(bucketName, OBJECT_KEY, OBJECT_CONTENT);
    }

    @Test
    public void contextLoads() {
        assertThat(amazonS3).isNotNull();
        assertThat(detailPageController).isNotNull();
    }

    @Test
    public void detailControllerTest() throws Exception {
        ModelAndView detailPageView = detailPageController.getDetailPageView(OBJECT_KEY);

        assertThat(detailPageView.getModelMap())
            .isNotEmpty()
            .containsKey("s3Url")
            .containsKey("s3MessageContent")
            .containsKey("errorMessage");
    }

    @Test
    public void detailControllerObjectFoundTest() throws Exception {
        ModelAndView detailPageView = detailPageController.getDetailPageView(OBJECT_KEY);

        assertThat(detailPageView.getModelMap().get("s3Url"))
            .isNotNull()
            .isEqualTo(String.format("s3://%s/%s", bucketName, OBJECT_KEY));
        assertThat(detailPageView.getModelMap().get("s3MessageContent"))
            .isNotNull()
            .isEqualTo(OBJECT_CONTENT);
        assertThat(detailPageView.getModelMap().get("errorMessage")).isNotNull().isEqualTo("");
    }

    @Test
    public void detailControllerObjectNotFoundTest() throws Exception {
        ModelAndView detailPageView = detailPageController.getDetailPageView(OBJECT_KEY_NOT_FOUND);

        assertThat(detailPageView.getModelMap().get("s3Url"))
            .isNotNull()
            .isEqualTo(String.format("s3://%s/%s", bucketName, OBJECT_KEY_NOT_FOUND));
        assertThat(detailPageView.getModelMap().get("s3MessageContent")).isNull();
        assertThat(detailPageView.getModelMap().get("errorMessage"))
            .isNotNull()
            .isEqualTo(String.format("S3 Object %s not found.", OBJECT_KEY_NOT_FOUND));
    }
}
