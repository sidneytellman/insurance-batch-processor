package model;

/**
 * An insurance claim made against a policy.
 * <p>
 * Everything except the paid flag is fixed when the claim is created. A claim
 * can only go from unpaid to paid, never back, and can only be paid once.
 */
public class Claim {

    private final String claimId;
    private final String policyNumber;
    private final double amount;
    private final boolean approved;
    private boolean paid;

    /**
     * Creates a new, unpaid claim.
     *
     * @param claimId      unique identifier for the claim, e.g. "C-501"
     * @param policyNumber the policy the claim is made against
     * @param amount       claimed amount in kr
     * @param approved     whether the claim has been approved for payment
     * @throws IllegalArgumentException if the ID or policy number is empty, or the amount is zero or negative
     */
    public Claim(String claimId, String policyNumber, double amount, boolean approved) {

        if (claimId == null || claimId.isBlank()) {
            throw new IllegalArgumentException("Claim ID cannot be empty.");
        }
        if (policyNumber == null || policyNumber.isBlank()) {
            throw new IllegalArgumentException("Policy number cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Claim amount must be greater than zero.");
        }

        this.claimId = claimId;
        this.policyNumber = policyNumber;
        this.amount = amount;
        this.approved = approved;
        this.paid = false;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isPaid() {
        return paid;
    }

    /**
     * Marks the claim as paid.
     *
     * @throws IllegalStateException if the claim has already been paid
     */
    public void markAsPaid() {
        if (paid) {
            throw new IllegalStateException("Claim " + claimId + " has already been paid.");
        }
        paid = true;
    }
}