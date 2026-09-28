package job;

import model.JobStatus;
import service.PolicyRepository;
import service.PolicyRepository;

public abstract class BatchJob {

    private final String jobId;
    private final String name;
    private JobStatus status;
    private int retryCount;

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

    protected void markCompleted() {
        status = JobStatus.COMPLETED;
    }

    protected void markFailed() {
        status = JobStatus.FAILED;
    }

    protected void incrementRetryCount() {
        retryCount++;
    }

    public String getDetails() {
        return jobId + " | " + name + " | " + status;
    }
}
public abstract void execute(PolicyRepository repository);
public abstract int estimateRuntimeMinutes(PolicyRepository repository);
