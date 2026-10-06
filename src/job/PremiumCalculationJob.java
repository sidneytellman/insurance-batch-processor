package job;

import exception.InvalidJobStateException;
import exception.JobExecutionException;
import model.JobStatus;
import model.Policy;
import service.PolicyRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * Batch job that raises the premium of every policy by a fixed index percentage.
 * <p>
 * New premiums are rounded to two decimals. If a new premium would exceed the
 * policy's coverage amount, the job stops and fails.
 * <p>
 * The job remembers which policies it has already updated, so a retry after a
 * failure never raises the same premium twice. Implements {@link Retryable}.
 */
public class PremiumCalculationJob extends BatchJob implements Retryable {

    private final double indexPercent;
    private final Set<String> updatedPolicyNumbers = new HashSet<>();

    /**
     * Creates a new premium calculation job.
     *
     * @param jobId        unique identifier for the job
     * @param name         descriptive name of the job
     * @param indexPercent how much to raise each premium, e.g. 3.5 for 3.5 %
     * @throws IllegalArgumentException if the index percentage is zero or negative
     */
    public PremiumCalculationJob(String jobId, String name, double indexPercent) {
        super(jobId, name);
        if (indexPercent <= 0) {
            throw new IllegalArgumentException("Index percentage must be greater than zero.");
        }
        this.indexPercent = indexPercent;
    }

    /**
     * Raises the premium of every policy not already updated by this job.
     *
     * @param repository the policies to update
     * @throws JobExecutionException if a new premium would exceed the policy's coverage
     */
    @Override
    public void execute(PolicyRepository repository) {
        for (Policy policy : repository.getPolicies()) {
            if (updatedPolicyNumbers.contains(policy.getPolicyNumber())) {
                continue;
            }
            double newPremium = Math.round(policy.getPremium() * (1 + indexPercent / 100) * 100) / 100.0;

            if (newPremium > policy.getCoverageAmount()) {
                markFailed();
                throw new JobExecutionException("Policy " + policy.getPolicyNumber() + ": new premium " + newPremium
                        + " kr would exceed the coverage amount of " + policy.getCoverageAmount() + " kr.");
            }
            policy.setPremium(newPremium);
            updatedPolicyNumbers.add(policy.getPolicyNumber());
        }
        markCompleted();
    }

    /**
     * Estimates one minute plus one extra minute per 100 policies.
     *
     * @param repository the policies the job would update
     * @return estimated runtime in minutes
     */
    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getPolicies().size() / 100;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Index increase: " + indexPercent + "%";
    }

    /**
     * A job can be retried if it has failed and has retries left.
     *
     * @return {@code true} if the job is FAILED and the retry limit is not reached
     */
    @Override
    public boolean canRetry() {
        return getStatus() == JobStatus.FAILED && getRetryCount() < MAX_RETRIES;
    }

    /**
     * Runs the job again after a failure, skipping policies already updated.
     *
     * @param repository the policies to update
     * @throws InvalidJobStateException if the job is not FAILED or has no retries left
     * @throws JobExecutionException    if the retry fails as well
     */
    @Override
    public void retry(PolicyRepository repository) {
        if (!canRetry()) {
            throw new InvalidJobStateException("Job " + getJobId() + " cannot be retried: status is " + getStatus() + ", retries used " + getRetryCount() + " of " + MAX_RETRIES + ".");
        }
        incrementRetryCount();
        execute(repository);
    }
}