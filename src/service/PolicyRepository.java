package service;

import model.Claim;
import model.Policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Stores all policies and claims in memory.
 * <p>
 * Policy numbers must be unique, and a claim can only be added if its policy
 * already exists. The lists returned are read-only, so data can only be added
 * through this class's methods.
 */
public class PolicyRepository {

    private final List<Policy> policies = new ArrayList<>();
    private final List<Claim> claims = new ArrayList<>();

    /**
     * Adds a policy.
     *
     * @param policy the policy to add
     * @throws IllegalArgumentException if the policy is null or its number is already in use
     */
    public void addPolicy(Policy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Policy cannot be null.");
        }
        if (findByPolicyNumber(policy.getPolicyNumber()).isPresent()) {
            throw new IllegalArgumentException("A policy with number " + policy.getPolicyNumber() + " already exists.");
        }
        policies.add(policy);
    }

    /**
     * Finds a policy by its number, ignoring upper and lower case.
     *
     * @param policyNumber the number to look for
     * @return the matching policy, or an empty {@code Optional} if none exists
     */
    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        for (Policy policy : policies) {
            if (policy.getPolicyNumber().equalsIgnoreCase(policyNumber)) {
                return Optional.of(policy);
            }
        }
        return Optional.empty();
    }

    /**
     * @return an unmodifiable list of all policies
     */
    public List<Policy> getPolicies() {
        return Collections.unmodifiableList(policies);
    }

    /**
     * Adds a claim.
     *
     * @param claim the claim to add
     * @throws IllegalArgumentException if the claim is null or its policy does not exist
     */
    public void addClaim(Claim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("Claim cannot be null.");
        }
        if (findByPolicyNumber(claim.getPolicyNumber()).isEmpty()) {
            throw new IllegalArgumentException("Cannot add claim: no policy with number " + claim.getPolicyNumber() + " exists.");
        }
        claims.add(claim);
    }

    /**
     * @return an unmodifiable list of all claims
     */
    public List<Claim> getClaims() {
        return Collections.unmodifiableList(claims);
    }

    /**
     * Finds the claims that are approved but not yet paid, in the order they were added.
     *
     * @return the claims waiting for payment, empty if there are none
     */
    public List<Claim> getUnpaidApprovedClaims() {
        List<Claim> result = new ArrayList<>();
        for (Claim claim : claims) {
            if (claim.isApproved() && !claim.isPaid()) {
                result.add(claim);
            }
        }
        return result;
    }
}