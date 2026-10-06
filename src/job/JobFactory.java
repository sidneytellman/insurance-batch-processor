package job;

public class JobFactory {

    private JobFactory() {
    }

    public static BatchJob create(JobType type, String jobId, String name, double parameter) {
        return switch(type) {
            case PREMIUM_CALCULATION -> new PremiumCalculationJob(jobId, name, parameter);
            case CLAIMS_SETTLEMENT -> new ClaimsSettlementJob(jobId, name);
            case RENEWAL_NOTICE -> new RenewalNoticeJob(jobId, name, (int) parameter);
            case CLAIMS_AUDIT -> new ClaimsAuditJob(jobId, name, parameter);
        };
    }
}
