package job;

import model.Policy;
import service.PolicyRepository;

import java.time.LocalDate;

public class RenewalNoticeJob extends BatchJob {

    private final int daysBeforeRenewal;

    public RenewalNoticeJob(String jobId, String name, int daysBeforeRenewal) {
        super(jobId, name);
        if (daysBeforeRenewal <= 0) {
            throw new IllegalArgumentException("Days before renewal must be greater than zero.");
        }
        this.daysBeforeRenewal = daysBeforeRenewal;
    }

    @Override
    public void execute(PolicyRepository repository) {
        LocalDate cutoff = LocalDate.now().plusDays(daysBeforeRenewal);

        for (Policy policy : repository.getPolicies()) {
            if (!policy.getExpiryDate().isAfter(cutoff)) {
                System.out.println("Renewal notice: policy " + policy.getPolicyNumber()
                        + " (" + policy.getHolderName() + ") expires on " + policy.getExpiryDate() + ".");
            }
        }
        markCompleted();
    }

    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getPolicies().size() / 100;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Notifies policies expiring within " + daysBeforeRenewal + " days";
    }
}