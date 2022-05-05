package cloud.matzat.aws.mailimport.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import cloud.matzat.aws.mailimport.service.S3BucketService;

import javax.annotation.PostConstruct;

/**
 * Controller for S3-storageentries overview.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Controller
public class DashboardController {

    private final S3BucketService s3BucketService;

    @Value("${configuration.s3.bucket-name}")
    private String bucketName;

    private String bucketLocation;

    public DashboardController(S3BucketService s3BucketService) {
        this.s3BucketService = s3BucketService;
    }

    @PostConstruct
    public void postConstruct() {
        this.bucketLocation = s3BucketService.getS3BucketLocation(bucketName);
    }

    @GetMapping("/")
    public ModelAndView getDashboardView() {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("bucketName", bucketName);
        modelAndView.addObject("bucketLocation", bucketLocation);
        modelAndView.addObject("availableFiles", s3BucketService.listObjects(bucketName).getObjectSummaries());
        return modelAndView;
    }
}
