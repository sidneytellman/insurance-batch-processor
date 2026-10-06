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