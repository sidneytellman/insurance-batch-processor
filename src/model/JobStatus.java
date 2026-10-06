package model;

/**
 * The possible states of a batch job.
 * <p>
 * Every job starts as PENDING. A run ends in COMPLETED or FAILED.
 * Only a FAILED job that implements {@code Retryable} can be run again.
 */
public enum JobStatus {
    /** Created but not yet run. */
    PENDING,
    /** Ran successfully. */
    COMPLETED,
    /** Stopped because of an error during the run. */
    FAILED
}