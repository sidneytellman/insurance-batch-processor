package job;

import exception.InvalidJobStateException;
import exception.JobExecutionException;
import model.JobStatus;
import model.Policy;
import service.PolicyRepository;

import java.util.HashSet;
import java.util.Set;

public class PremiumCalculationJob extends BatchJob implements Retryable {

    private final double indexPercent;
    private final Set<String> updatedPolicyNumbers = new HashSet<>();

    public PremiumCalculationJob(String jobId, String name, double indexPercent) {
        super(jobId, name);
        if (indexPercent <= 0) {
            throw new IllegalArgumentException("Index percentage must be greater than zero.");
        }
        this.indexPercent = indexPercent;
    }

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

    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getPolicies().size() / 100;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Index increase: " + indexPercent + "%";
    }

    @Override
    public boolean canRetry() {
        return getStatus() == JobStatus.FAILED && getRetryCount() < MAX_RETRIES;
    }

    @Override
    public void retry(PolicyRepository repository) {
        if (!canRetry()) {
            throw new InvalidJobStateException("Job " + getJobId() + " cannot be retried: status is " + getStatus() + ", retries used " + getRetryCount() + " of " + MAX_RETRIES + ".");
        }
        incrementRetryCount();
        execute(repository);
    }
}