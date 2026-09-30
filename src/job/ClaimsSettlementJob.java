package job;

import exception.JobExecutionException;
import model.Claim;
import model.Policy;
import service.PolicyRepository;

public class ClaimsSettlementJob extends BatchJob {

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
}
