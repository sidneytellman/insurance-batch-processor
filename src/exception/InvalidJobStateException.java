package exception;

/**
 * Thrown when an action is not allowed for a job's current status,
 * for example running a job that has already completed.
 */
public class InvalidJobStateException extends RuntimeException {

    /**
     * @param message description of why the action is not allowed
     */
    public InvalidJobStateException(String message) {
        super(message);
    }
}