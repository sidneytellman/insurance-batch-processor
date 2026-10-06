package exception;

/**
 * Thrown when a job fails while running, for example when a claim
 * exceeds its policy's coverage. The job is marked FAILED before this is thrown.
 */
public class JobExecutionException extends RuntimeException {

    /**
     * @param message description of what went wrong during the run
     */
    public JobExecutionException(String message) {
        super(message);
    }
}