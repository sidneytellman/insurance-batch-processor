package job;

import service.PolicyRepository;

/**
 * Implemented by jobs that can be run again after a failure.
 * <p>
 * Not every job needs this. Jobs that only read data, like renewal notices,
 * never fail, so only jobs that change data implement it. {@code JobRegister}
 * finds retryable jobs with {@code instanceof}, without knowing their classes.
 */
public interface Retryable {

    /**
     * The maximum number of times a failed job may be retried.
     */
    int MAX_RETRIES = 3;

    /**
     * Checks whether the job may be retried right now.
     *
     * @return {@code true} if the job has failed and has retries left
     */
    boolean canRetry();

    /**
     * Runs the job again and counts the attempt.
     *
     * @param repository the data the job works on
     */
    void retry(PolicyRepository repository);
}