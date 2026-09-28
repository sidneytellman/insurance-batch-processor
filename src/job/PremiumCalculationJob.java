package job;

import service.PolicyRepository;
import model.Policy;


public class PremiumCalculationJob extends BatchJob {

    private final double indexPercent;

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
            double newPremium = policy.getPremium() * (1 + indexPercent / 100);
            policy.setPremium(Math.round(newPremium * 100) / 100.0);
        }
        markCompleted();
    }

    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getPolicies().size() / 100;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Index Increase: " + indexPercent + "%";
    }


}
