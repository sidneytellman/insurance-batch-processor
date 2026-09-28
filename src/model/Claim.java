package model;

public class Claim {

    private final String claimId;
    private final String policyNumber;
    private final double amount;
    private final boolean approved;
    private boolean paid;

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

    public void markasPaid() {
        if (paid) {
            throw new IllegalStateException("Claim" + claimId + " has already been paid. ");
        }
        paid = true;
    }
}
