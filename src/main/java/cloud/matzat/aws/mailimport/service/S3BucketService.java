package cloud.matzat.aws.mailimport.service;

import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;
import java.util.Optional;

/**
 * CRUD-Service to process objects with S3.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Service
public class S3BucketService {

    private final AmazonS3 amazonS3Client;

    public S3BucketService(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String readS3Resource(String bucketName, String objectKey) {
        Optional<S3ObjectSummary> objectSummary = findObject(bucketName, objectKey);
        if (objectSummary.isEmpty()) {
            throw new S3ObjectNotFoundException(String.format("Object with key %s not found", objectKey));
        }
        return amazonS3Client.getObjectAsString(bucketName, objectKey);
    }

    public void writeS3Resource(String bucketName, String objectKey, String content) {
        amazonS3Client.putObject(bucketName, objectKey, content);
    }

    public void deleteS3Resource(String bucketName, String objectKey) {
        amazonS3Client.deleteObject(bucketName, objectKey);
    }

    public String getS3ResourceUrl(String bucketName, String objectKey) {
        return String.format("s3://%s/%s", bucketName, objectKey);
    }

    public String getS3BucketLocation(String bucketName) {
        return String.format("https://%s.s3.%s.amazonaws.com",
                             bucketName, amazonS3Client.getBucketLocation(bucketName)
        );
    }

    public ObjectListing listObjects(String bucketName) {
        return amazonS3Client.listObjects(bucketName);
    }

    public Optional<S3ObjectSummary> findObject(String bucketName, String objectKey) {
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName, objectKey);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        return objectSummaries.stream()
                              .filter(s3ObjectSummary -> s3ObjectSummary.getKey().equals(objectKey))
                              .findFirst();
    }
}
