package cloud.matzat.aws.mailimport.service;

/**
 * Exceptions thrown, if a lock was already taken.
 *
 * @author Christian Matzat
 * @see LockService (christian@matzat.net)
 */
public class LockAlreadyTakenException extends RuntimeException {

    public LockAlreadyTakenException(String message) {
        super(message);
    }
}
