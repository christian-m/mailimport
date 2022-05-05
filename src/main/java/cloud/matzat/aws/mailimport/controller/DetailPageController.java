package cloud.matzat.aws.mailimport.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cloud.matzat.aws.mailimport.service.S3BucketService;
import cloud.matzat.aws.mailimport.service.S3ObjectNotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for one single S3-storageentry.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Slf4j
@Controller
public class DetailPageController {

    private final S3BucketService s3BucketService;

    @Value("${configuration.s3.bucket-name}")
    private String bucketName;

    public DetailPageController(S3BucketService s3BucketService) {
        this.s3BucketService = s3BucketService;
    }

    @GetMapping("/detail")
    public ModelAndView getDetailPageView(@RequestParam("objectkey") String objectKey) {
        String s3MessageContent = null;
        String errorMessage = "";
        try {
            s3MessageContent = s3BucketService.readS3Resource(bucketName, objectKey);
        } catch (S3ObjectNotFoundException e) {
            log.info("S3 Object {} not found.", objectKey);
            errorMessage = String.format("S3 Object %s not found.", objectKey);
        }

        final String s3Url = s3BucketService.getS3ResourceUrl(bucketName, objectKey);
        ModelAndView modelAndView = new ModelAndView("detailpage");
        modelAndView.addObject("s3Url", s3Url);
        modelAndView.addObject("s3MessageContent", s3MessageContent);
        modelAndView.addObject("errorMessage", errorMessage);
        return modelAndView;
    }

}
