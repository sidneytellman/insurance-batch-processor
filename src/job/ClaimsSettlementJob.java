package job;

import exception.InvalidJobStateException;
import exception.JobExecutionException;
import model.Claim;
import model.JobStatus;
import model.Policy;
import service.PolicyRepository;

/**
 * Batch job that pays out all approved claims that have not been paid yet.
 * <p>
 * Each claim is checked against its policy's coverage before payment. If a claim
 * exceeds the coverage, the job stops and fails. Claims already paid in the same
 * run stay paid, so a retry only processes what is left.
 * <p>
 * Implements {@link Retryable}, so a failed run can be retried up to
 * {@link Retryable#MAX_RETRIES} times.
 */
public class ClaimsSettlementJob extends BatchJob implements Retryable {

    /**
     * Creates a new claims settlement job.
     *
     * @param jobId unique identifier for the job
     * @param name  descriptive name of the job
     */
    public ClaimsSettlementJob(String jobId, String name) {
        super(jobId, name);
    }

    /**
     * Pays every unpaid, approved claim in the repository, in order.
     *
     * @param repository the policies and claims to process
     * @throws JobExecutionException if a claim exceeds its policy's coverage
     *                               or refers to a policy that does not exist
     */
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

    /**
     * Estimates one minute plus one extra minute per 50 claims waiting to be paid.
     *
     * @param repository the claims the job would process
     * @return estimated runtime in minutes
     */
    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getUnpaidApprovedClaims().size() / 50;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Settles approved claims";
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
     * Runs the job again after a failure and counts the attempt.
     *
     * @param repository the policies and claims to process
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