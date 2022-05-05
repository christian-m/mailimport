package cloud.matzat.aws.mailimport.service;

/**
 * Exception thrown if a S3 object was not found.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
public class S3ObjectNotFoundException extends RuntimeException {

    public S3ObjectNotFoundException(String exception) {
        super(exception);
    }
}
