package job;

import model.Policy;
import service.PolicyRepository;

import java.time.LocalDate;

/**
 * Batch job that sends renewal notices for policies that expire soon.
 * <p>
 * A notice is printed for every policy whose expiry date falls within the
 * given number of days from today. Policies that have already expired are
 * included too. The job never fails and does not change any data.
 */
public class RenewalNoticeJob extends BatchJob {

    private final int daysBeforeRenewal;

    /**
     * Creates a new renewal notice job.
     *
     * @param jobId             unique identifier for the job
     * @param name              descriptive name of the job
     * @param daysBeforeRenewal how many days ahead to look for expiring policies
     * @throws IllegalArgumentException if the number of days is zero or negative
     */
    public RenewalNoticeJob(String jobId, String name, int daysBeforeRenewal) {
        super(jobId, name);
        if (daysBeforeRenewal <= 0) {
            throw new IllegalArgumentException("Days before renewal must be greater than zero.");
        }
        this.daysBeforeRenewal = daysBeforeRenewal;
    }

    /**
     * Prints a renewal notice for each policy expiring on or before the cutoff date.
     *
     * @param repository the policies to check
     */
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

    /**
     * Estimates one minute plus one extra minute per 100 policies.
     *
     * @param repository the policies the job would check
     * @return estimated runtime in minutes
     */
    @Override
    public int estimateRuntimeMinutes(PolicyRepository repository) {
        return 1 + repository.getPolicies().size() / 100;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Notifies policies expiring within " + daysBeforeRenewal + " days";
    }
}