package job;

import model.Claim;
import service.PolicyRepository;

public class ClaimsAuditJob extends BatchJob {

    private final double threshold;

    public ClaimsAuditJob(String jobId, String name, double threshold) {
        super(jobId, name);
        if (threshold <= 0) {
            throw new IllegalArgumentException("Threshold must be greater than zero");
        }
        this.threshold = threshold;
    }

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

    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getClaims().size() / 200;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Flags approved claims above " + threshold + " kr";
    }


}
