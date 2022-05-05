package cloud.matzat.aws.mailimport.controller;

import org.assertj.core.api.InstanceOfAssertFactories;
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
 * Unittest for {@link DashboardController}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SpringBootTest(classes = { S3BucketService.class, S3TestConfig.class, DashboardController.class })
@ActiveProfiles("test")
public class DashboardControllerTest {

    private static final String OBJECT_KEY = "test-object";
    private static final String OBJECT_CONTENT = "test-content";

    @Value("${configuration.s3.bucket-name}")
    private String bucketName = "test-bucket";

    // Different bucketLocation due to restrictions of s3mock
    private String bucketLocation = String.format("https://%s.s3.%s.amazonaws.com",
                                                  bucketName, "US"
    );
    ;

    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private DashboardController dashboardController;

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
        assertThat(dashboardController).isNotNull();
    }

    @Test
    public void dashboardControllerTest() throws Exception {
        ModelAndView dashboardView = dashboardController.getDashboardView();

        assertThat(dashboardView.getViewName()).isEqualTo("dashboard");
        assertThat(dashboardView.getModelMap())
            .isNotEmpty()
            .containsKey("bucketName")
            .containsKey("bucketLocation")
            .containsKey("availableFiles");

        assertThat(dashboardView.getModelMap().get("bucketName")).isNotNull().isEqualTo(bucketName);
        assertThat(dashboardView.getModelMap().get("bucketLocation")).isNotNull().isEqualTo(bucketLocation);
        assertThat(dashboardView.getModelMap().get("availableFiles"))
            .isNotNull()
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .hasSize(1);
        List<S3ObjectSummary> objectSummaries = (List<S3ObjectSummary>) dashboardView.getModelMap()
                                                                                     .get("availableFiles");
        assertThat(objectSummaries.get(0).getKey()).isNotNull().isEqualTo(OBJECT_KEY);
    }
}
