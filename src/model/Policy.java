package model;

import java.time.LocalDate;

public class Policy {

    private final String policyNumber;
    private final String holderName;
    private double premium;
    private final double coverageAmount;
    private final LocalDate expiryDate;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getPremium() {
        return premium;
    }

    public double getCoverageAmount() {
        return coverageAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setPremium(double premium) {
        if (premium <= 0) {
            throw new IllegalArgumentException("Premium cannot be negative or zero.");
        }
        this.premium = premium;

    }

    public Policy(String policyNumber, String holderName, double premium, double coverageAmount, LocalDate expiryDate) {
        if (policyNumber == null || policyNumber.isBlank()) {
            throw new IllegalArgumentException("Policy number cannot be empty.");
        }
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Holder name cannot be empty.");
        }
        if (premium <= 0) {
            throw new IllegalArgumentException("Premium cannot be negative or zero.");
        }
        if (coverageAmount <= 0) {
            throw new IllegalArgumentException("Coverage amount cannot be negative or zero.");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date is required.");
        }


        this.policyNumber = policyNumber;
        this.holderName = holderName;
        this.premium = premium;
        this.coverageAmount = coverageAmount;
        this.expiryDate = expiryDate;
    }

}
