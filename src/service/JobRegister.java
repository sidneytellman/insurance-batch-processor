package service;

import job.Retryable;
import exception.InvalidJobStateException;
import exception.JobExecutionException;
import model.JobStatus;
import exception.JobNotFoundException;
import job.BatchJob;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobRegister {

    private final List<BatchJob> jobs = new ArrayList<>();
    private final PolicyRepository repository;

    public JobRegister(PolicyRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Policy repository cannot be null.");
        }
        this.repository = repository;
    }

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

    public BatchJob findById(String jobId) {
        for (BatchJob job : jobs) {
            if (job.getJobId().equalsIgnoreCase(jobId)) {
                return job;
            }
        }
        throw new JobNotFoundException("No job with ID " + jobId + " was found.");
    }

    public void removeJob(String jobId) {
        BatchJob job = findById(jobId);
        jobs.remove(job);
    }

    public void runJob(String jobId) {
        BatchJob job = findById(jobId);
        if (job.getStatus() != JobStatus.PENDING) {
            throw new InvalidJobStateException("Job " + jobId + " cannot run: status is " + job.getStatus() + ".");
        }
        job.execute(repository);
    }

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

    public List<BatchJob> findByStatus(JobStatus status) {
        List<BatchJob> result = new ArrayList<>();
        for (BatchJob job : jobs) {
            if (job.getStatus() == status) {
                result.add(job);
            }
        }
        return result;
    }

    public List<Retryable> getRetryableJobs() {
        List<Retryable> result = new ArrayList<>();
        for (BatchJob job : jobs) {
            if (job instanceof Retryable retryable) {
                result.add(retryable);
            }
        }
        return result;
    }

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

    public int getTotalEstimatedRuntime() {
        int total = 0;
        for (BatchJob job : findByStatus(JobStatus.PENDING)) {
            total += job.estimateRuntimeMinutes(repository);
        }
        return total;
    }

    public double getSuccessRate() {
        int completed = findByStatus(JobStatus.COMPLETED).size();
        int failed = findByStatus(JobStatus.FAILED).size();
        if (completed + failed == 0) {
            return 0;
        }
        return completed * 100.0 / (completed + failed);
    }

    public List<BatchJob> getJobs() {
        return Collections.unmodifiableList(jobs);
    }
}