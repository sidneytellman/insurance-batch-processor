package exception;

/**
 * Thrown when no job exists with the requested ID.
 */
public class JobNotFoundException extends RuntimeException {

    /**
     * @param message description including the ID that was searched for
     */
    public JobNotFoundException(String message) {
        super(message);
    }
}