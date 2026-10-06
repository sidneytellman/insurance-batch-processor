package job;

import model.Claim;
import service.PolicyRepository;

/**
 * Batch job that flags large approved claims for manual review.
 * <p>
 * Every approved claim above the threshold amount is printed, paid or not.
 * The job never fails and does not change any data. It was added after the
 * other three jobs to show that a new job type only needs a new class, one
 * {@link JobType} constant and one line in {@link JobFactory}.
 */
public class ClaimsAuditJob extends BatchJob {

    private final double threshold;

    /**
     * Creates a new claims audit job.
     *
     * @param jobId     unique identifier for the job
     * @param name      descriptive name of the job
     * @param threshold claims above this amount (in kr) are flagged
     * @throws IllegalArgumentException if the threshold is zero or negative
     */
    public ClaimsAuditJob(String jobId, String name, double threshold) {
        super(jobId, name);
        if (threshold <= 0) {
            throw new IllegalArgumentException("Audit threshold must be greater than zero.");
        }
        this.threshold = threshold;
    }

    /**
     * Prints every approved claim above the threshold, followed by how many were flagged.
     *
     * @param repository the claims to check
     */
    @Override
    public void execute(PolicyRepository repository) {
        int flagged = 0;
        for (Claim claim : repository.getClaims()) {
            if (claim.isApproved() && claim.getAmount() > threshold) {
                System.out.println("Flagged for review: claim " + claim.getClaimId()
                        + " on policy " + claim.getPolicyNumber() + " (" + claim.getAmount() + " kr).");
                flagged++;
            }
        }
        System.out.println(flagged + " claim(s) flagged.");
        markCompleted();
    }

    /**
     * Estimates one minute plus one extra minute per 200 claims.
     *
     * @param repository the claims the job would check
     * @return estimated runtime in minutes
     */
    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getClaims().size() / 200;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Flags approved claims above " + threshold + " kr";
    }
}