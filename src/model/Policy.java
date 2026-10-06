package model;

import java.time.LocalDate;

/**
 * An insurance policy held by a customer.
 * <p>
 * The premium is the only field that can change after creation, since the
 * premium calculation job raises it. All other fields are fixed.
 */
public class Policy {

    private final String policyNumber;
    private final String holderName;
    private double premium;
    private final double coverageAmount;
    private final LocalDate expiryDate;

    /**
     * Creates a new policy.
     *
     * @param policyNumber   unique identifier for the policy, e.g. "P-1001"
     * @param holderName     name of the policy holder
     * @param premium        yearly premium in kr
     * @param coverageAmount the most the policy pays out, in kr
     * @param expiryDate     the date the policy expires
     * @throws IllegalArgumentException if any text is empty, an amount is zero or negative, or the date is missing
     */
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

    /**
     * Sets a new premium.
     *
     * @param premium the new yearly premium in kr
     * @throws IllegalArgumentException if the premium is zero or negative
     */
    public void setPremium(double premium) {
        if (premium <= 0) {
            throw new IllegalArgumentException("Premium cannot be negative or zero.");
        }
        this.premium = premium;
    }
}