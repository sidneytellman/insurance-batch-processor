package job;

/**
 * The types of batch job a user can create.
 * <p>
 * Each type holds what the menu needs to create it: a display name and, if the
 * job takes an extra parameter, the prompt and allowed range for that value.
 * The menu builds its type list from {@link #values()}, so adding a constant
 * here makes the new type appear in the menu automatically.
 */
public enum JobType {
    PREMIUM_CALCULATION("Premium calculation", "Index increase (%): ", 0.1, 100, false),
    CLAIMS_SETTLEMENT("Claims settlement", null, 0, 0, false),
    RENEWAL_NOTICE("Renewal notice", "Notify policies expiring within how many days: ", 1, 365, true),
    CLAIMS_AUDIT("Claims audit", "Flag approved claims above (kr): ", 1, 10_000_000, false);

    private final String displayName;
    private final String parameterPrompt;
    private final double min;
    private final double max;
    private final boolean wholeNumber;

    /**
     * @param displayName     name shown in the menu
     * @param parameterPrompt prompt for the extra parameter, or {@code null} if the job has none
     * @param min             lowest allowed parameter value
     * @param max             highest allowed parameter value
     * @param wholeNumber     {@code true} if the parameter must be a whole number
     */
    JobType(String displayName, String parameterPrompt, double min, double max, boolean wholeNumber) {
        this.displayName = displayName;
        this.parameterPrompt = parameterPrompt;
        this.min = min;
        this.max = max;
        this.wholeNumber = wholeNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParameterPrompt() {
        return parameterPrompt;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isWholeNumber() {
        return wholeNumber;
    }

    /**
     * @return {@code true} if this job type needs an extra parameter when created
     */
    public boolean hasParameter() {
        return parameterPrompt != null;
    }
}