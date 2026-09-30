package job;

import exception.InvalidJobStateException;
import exception.JobExecutionException;
import model.Claim;
import model.JobStatus;
import model.Policy;
import service.PolicyRepository;

public class ClaimsSettlementJob extends BatchJob implements Retryable {

    public ClaimsSettlementJob(String jobId, String name) {
        super(jobId, name);
    }

    @Override
    public void execute(PolicyRepository repository) {
        for (Claim claim : repository.getUnpaidApprovedClaims()) {
            Policy policy = repository.findByPolicyNumber(claim.getPolicyNumber()).orElseThrow(() -> new JobExecutionException("Claim " + claim.getClaimId() + " refers to unknown policy " + claim.getPolicyNumber() + "."));

            if (claim.getAmount() > policy.getCoverageAmount()) {
                markFailed();
                throw new JobExecutionException("Claim " + claim.getClaimId() + " (" + claim.getAmount() + " kr) exceeds coverage of policy " + policy.getPolicyNumber() + " (" + policy.getCoverageAmount() + " kr).");
            }
            claim.markAsPaid();
        }
        markCompleted();
    }

    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getUnpaidApprovedClaims().size() / 50;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Settles approved claims";
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