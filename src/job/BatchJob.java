package job;

import model.JobStatus;
import service.PolicyRepository;

/**
 * Base class for all batch jobs in the system.
 * <p>
 * Holds what every job has in common: an ID, a name, a status and a retry count.
 * Each subclass decides what the job actually does by implementing
 * {@link #execute(PolicyRepository)} and {@link #estimateRuntimeMinutes(PolicyRepository)}.
 * <p>
 * A new job always starts as {@link JobStatus#PENDING}. Only the job itself can
 * change its status, through the protected {@code markCompleted()} and
 * {@code markFailed()} methods.
 */
public abstract class BatchJob {

    private final String jobId;
    private final String name;
    private JobStatus status;
    private int retryCount;

    /**
     * Creates a new job with status PENDING and zero retries.
     *
     * @param jobId unique identifier for the job, e.g. "J-001"
     * @param name  descriptive name of the job
     * @throws IllegalArgumentException if the ID or name is empty
     */
    protected BatchJob(String jobId, String name) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be empty.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Job name cannot be empty.");
        }

        this.jobId = jobId;
        this.name = name;
        this.status = JobStatus.PENDING;
        this.retryCount = 0;
    }

    public String getJobId() {
        return jobId;
    }

    public String getName() {
        return name;
    }

    public JobStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Marks the job as successfully completed. Called by the subclass at the end of {@code execute}.
     */
    protected void markCompleted() {
        status = JobStatus.COMPLETED;
    }

    /**
     * Marks the job as failed. Called by the subclass before throwing a {@code JobExecutionException}.
     */
    protected void markFailed() {
        status = JobStatus.FAILED;
    }

    /**
     * Increases the retry count by one. Used by jobs that implement {@link Retryable}.
     */
    protected void incrementRetryCount() {
        retryCount++;
    }

    /**
     * Returns a one-line summary of the job. Subclasses extend this with their own details.
     *
     * @return the job's ID, name and status, separated by " | "
     */
    public String getDetails() {
        return jobId + " | " + name + " | " + status;
    }

    /**
     * Runs the job against the policies and claims in the repository.
     * Implementations must call {@code markCompleted()} on success, or
     * {@code markFailed()} and throw a {@code JobExecutionException} on failure.
     *
     * @param repository the data the job works on
     */
    public abstract void execute(PolicyRepository repository);

    /**
     * Estimates how long the job would take to run, based on the amount of data.
     *
     * @param repository the data the job would work on
     * @return estimated runtime in minutes
     */
    public abstract int estimateRuntimeMinutes(PolicyRepository repository);
}