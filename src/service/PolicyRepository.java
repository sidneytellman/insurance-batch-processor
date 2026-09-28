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
}