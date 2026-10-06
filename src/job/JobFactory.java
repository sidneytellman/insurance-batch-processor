package job;

/**
 * Creates batch jobs from a {@link JobType}.
 * <p>
 * This is the only place that decides which job class to instantiate, so the
 * menu never needs to know about specific job classes. The switch has no
 * default case: if a new {@link JobType} constant is added without a matching
 * case here, the code will not compile.
 */
public class JobFactory {

    private JobFactory() {
    }

    /**
     * Creates a job of the given type.
     *
     * @param type      which kind of job to create
     * @param jobId     unique identifier for the job
     * @param name      descriptive name of the job
     * @param parameter the job's extra value (index %, days or threshold); ignored by types without one
     * @return a new job with status PENDING
     * @throws IllegalArgumentException if the job's constructor rejects the values
     */
    public static BatchJob create(JobType type, String jobId, String name, double parameter) {
        return switch (type) {
            case PREMIUM_CALCULATION -> new PremiumCalculationJob(jobId, name, parameter);
            case CLAIMS_SETTLEMENT -> new ClaimsSettlementJob(jobId, name);
            case RENEWAL_NOTICE -> new RenewalNoticeJob(jobId, name, (int) parameter);
            case CLAIMS_AUDIT -> new ClaimsAuditJob(jobId, name, parameter);
        };
    }
}