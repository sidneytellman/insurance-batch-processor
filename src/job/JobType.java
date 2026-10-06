package job;

public enum JobType {

    PREMIUM_CALCULATION("Premium calculation", "Index increase (%): ", 0, 100, false),
    CLAIMS_SETTLEMENT("Claims settlement", null, 0, 0, false),
    RENEWAL_NOTICE("Renewal notice", "Notify policies expiring within how many days: ", 1, 365, true),
    CLAIMS_AUDIT("Claims audit", "Flag approved claims above (kr): ", 1, 10_000_000, false);

    private final String displayName;
    private final String parameterPrompt;
    private final double min;
    private final double max;
    private final boolean wholeNumber;

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

    public boolean hasParameter() {
        return parameterPrompt != null;
    }
}


