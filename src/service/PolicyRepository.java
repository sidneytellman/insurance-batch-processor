package service;

import model.Claim;
import model.Policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PolicyRepository {

    private final List<Policy> policies = new ArrayList<>();
    private final List<Claim> claims = new ArrayList<>();

    public void addPolicy(Policy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Policy cannot be null.");
        }
        if (findByPolicyNumber(policy.getPolicyNumber()).isPresent()) {
            throw new IllegalArgumentException("A policy with number " + policy.getPolicyNumber() + " already exists.");
        }
        policies.add(policy);
    }

    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        for (Policy policy : policies) {
            if (policy.getPolicyNumber().equalsIgnoreCase(policyNumber)) {
                return Optional.of(policy);
            }
        }
        return Optional.empty();
    }

    public List<Policy> getPolicies() {
        return Collections.unmodifiableList(policies);
    }

    public void addClaim(Claim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("Claim cannot be null.");
        }
        if (findByPolicyNumber(claim.getPolicyNumber()).isEmpty()) {
            throw new IllegalArgumentException("Cannot add claim: no policy with number " + claim.getPolicyNumber() + " exists.");
        }
        claims.add(claim);
    }

    public List<Claim> getClaims() {
        return Collections.unmodifiableList(claims);
    }

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