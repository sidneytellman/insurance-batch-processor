package service;

import exception.InvalidJobStateException;
import exception.JobExecutionException;
import exception.JobNotFoundException;
import job.BatchJob;
import job.Retryable;
import model.JobStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps track of all batch jobs and runs them against the policy repository.
 * <p>
 * This is the only class the menu talks to for job actions. It enforces the
 * rules: job IDs must be unique (case-insensitive), only PENDING jobs can be run,
 * and only failed jobs that implement {@link Retryable} can be retried.
 */
public class JobRegister {

    private final List<BatchJob> jobs = new ArrayList<>();
    private final PolicyRepository repository;

    /**
     * Creates an empty register that runs its jobs against the given repository.
     *
     * @param repository the policies and claims the jobs work on
     * @throws IllegalArgumentException if the repository is null
     */
    public JobRegister(PolicyRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Policy repository cannot be null.");
        }
        this.repository = repository;
    }

    /**
     * Adds a job to the register.
     *
     * @param job the job to add
     * @throws IllegalArgumentException if the job is null or its ID is already in use
     */
    public void addJob(BatchJob job) {
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null.");
        }
        for (BatchJob existing : jobs) {
            if (existing.getJobId().equalsIgnoreCase(job.getJobId())) {
                throw new IllegalArgumentException("A job with ID " + job.getJobId() + " already exists.");
            }
        }
        jobs.add(job);
    }

    /**
     * Finds a job by its ID, ignoring upper and lower case.
     *
     * @param jobId the ID to look for
     * @return the matching job
     * @throws JobNotFoundException if no job has that ID
     */
    public BatchJob findById(String jobId) {
        for (BatchJob job : jobs) {
            if (job.getJobId().equalsIgnoreCase(jobId)) {
                return job;
            }
        }
        throw new JobNotFoundException("No job with ID " + jobId + " was found.");
    }

    /**
     * Removes a job from the register.
     *
     * @param jobId the ID of the job to remove
     * @throws JobNotFoundException if no job has that ID
     */
    public void removeJob(String jobId) {
        BatchJob job = findById(jobId);
        jobs.remove(job);
    }

    /**
     * Runs a single job.
     *
     * @param jobId the ID of the job to run
     * @throws JobNotFoundException     if no job has that ID
     * @throws InvalidJobStateException if the job is not PENDING
     * @throws JobExecutionException    if the job fails while running
     */
    public void runJob(String jobId) {
        BatchJob job = findById(jobId);
        if (job.getStatus() != JobStatus.PENDING) {
            throw new InvalidJobStateException("Job " + jobId + " cannot run: status is " + job.getStatus() + ".");
        }
        job.execute(repository);
    }

    /**
     * Runs every PENDING job. A failing job does not stop the others.
     *
     * @return the error messages of the jobs that failed, empty if all succeeded
     */
    public List<String> runAllPendingJobs() {
        List<String> failures = new ArrayList<>();
        for (BatchJob job : findByStatus(JobStatus.PENDING)) {
            try {
                job.execute(repository);
            } catch (JobExecutionException e) {
                failures.add(e.getMessage());
            }
        }
        return failures;
    }

    /**
     * Finds all jobs with the given status.
     *
     * @param status the status to filter on
     * @return the matching jobs, empty if there are none
     */
    public List<BatchJob> findByStatus(JobStatus status) {
        List<BatchJob> result = new ArrayList<>();
        for (BatchJob job : jobs) {
            if (job.getStatus() == status) {
                result.add(job);
            }
        }
        return result;
    }

    /**
     * Finds all jobs that implement {@link Retryable}, whatever their status.
     *
     * @return the retryable jobs
     */
    public List<Retryable> getRetryableJobs() {
        List<Retryable> result = new ArrayList<>();
        for (BatchJob job : jobs) {
            if (job instanceof Retryable retryable) {
                result.add(retryable);
            }
        }
        return result;
    }

    /**
     * Retries every job that can currently be retried. A failing retry does not stop the others.
     *
     * @return the error messages of the retries that failed, empty if all succeeded
     */
    public List<String> retryFailedJobs() {
        List<String> failures = new ArrayList<>();
        for (Retryable job : getRetryableJobs()) {
            if (job.canRetry()) {
                try {
                    job.retry(repository);
                } catch (JobExecutionException e) {
                    failures.add(e.getMessage());
                }
            }
        }
        return failures;
    }

    /**
     * Adds up the estimated runtime of all PENDING jobs.
     *
     * @return total estimated runtime in minutes
     */
    public int getTotalEstimatedRuntime() {
        int total = 0;
        for (BatchJob job : findByStatus(JobStatus.PENDING)) {
            total += job.estimateRuntimeMinutes(repository);
        }
        return total;
    }

    /**
     * Calculates the share of finished jobs that completed successfully.
     * PENDING jobs are not counted.
     *
     * @return success rate in percent, or 0 if no job has finished yet
     */
    public double getSuccessRate() {
        int completed = findByStatus(JobStatus.COMPLETED).size();
        int failed = findByStatus(JobStatus.FAILED).size();
        if (completed + failed == 0) {
            return 0;
        }
        return completed * 100.0 / (completed + failed);
    }

    /**
     * Returns all jobs. The list is read-only, so jobs can only be added or removed through this class.
     *
     * @return an unmodifiable list of all jobs
     */
    public List<BatchJob> getJobs() {
        return Collections.unmodifiableList(jobs);
    }
}