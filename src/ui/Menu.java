package ui;

import exception.InvalidJobStateException;
import exception.JobExecutionException;
import exception.JobNotFoundException;
import job.BatchJob;
import job.JobFactory;
import job.JobType;
import model.JobStatus;
import service.JobRegister;

import java.util.List;

/**
 * Console menu for managing and running batch jobs.
 * <p>
 * This class only talks to the user: it shows options, reads input through
 * {@link InputHelper} and prints results. All rules and calculations live in
 * {@link JobRegister} and the job classes.
 * <p>
 * Each custom exception is caught in its own catch block with its own message,
 * so an error never stops the program.
 */
public class Menu {

    private final JobRegister register;
    private final InputHelper input;

    /**
     * Creates a menu for the given register.
     *
     * @param register the jobs to manage
     * @param input    helper for reading validated input
     */
    public Menu(JobRegister register, InputHelper input) {
        this.register = register;
        this.input = input;
    }

    /**
     * Shows the menu and handles choices until the user chooses 0 to exit.
     * After each action, waits for Enter so the result stays visible.
     */
    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.readIntInRange("Choose an option: ", 0, 8);
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> listJobs();
                    case 2 -> addJob();
                    case 3 -> runJob();
                    case 4 -> runAllPending();
                    case 5 -> searchByStatus();
                    case 6 -> retryFailed();
                    case 7 -> removeJob();
                    case 8 -> showReport();
                    case 0 -> running = false;
                }
            } catch (JobNotFoundException e) {
                System.out.println("Job not found: " + e.getMessage());
            } catch (InvalidJobStateException e) {
                System.out.println("Not allowed: " + e.getMessage());
            } catch (JobExecutionException e) {
                System.out.println("Job failed: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
            if (running) {
                System.out.println();
                input.waitForEnter();
                System.out.println();
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println("=== Insurance Batch Processor ===");
        System.out.println("1. List all jobs");
        System.out.println("2. Add a job");
        System.out.println("3. Run a job");
        System.out.println("4. Run all pending jobs");
        System.out.println("5. Search jobs by status");
        System.out.println("6. Retry failed jobs");
        System.out.println("7. Remove job");
        System.out.println("8. Show report");
        System.out.println("0. Exit");
    }

    private void listJobs() {
        printJobs(register.getJobs());
    }

    /**
     * Asks for a job type, ID, name and (if the type needs one) a parameter,
     * then creates the job through {@link JobFactory}. The type list comes from
     * {@link JobType#values()}, so new job types appear here without changes to this class.
     */
    private void addJob() {
        JobType[] types = JobType.values();
        System.out.println("Job type");
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getDisplayName());
        }
        JobType type = types[input.readIntInRange("Choose job type: ", 1, types.length) - 1];
        String id = input.readNonEmptyString("Job ID: ");
        String name = input.readNonEmptyString("Job name: ");

        double parameter = 0;
        if (type.hasParameter()) {
            if (type.isWholeNumber()) {
                parameter = input.readIntInRange(type.getParameterPrompt(), (int) type.getMin(), (int) type.getMax());
            } else {
                parameter = input.readDoubleInRange(type.getParameterPrompt(), type.getMin(), type.getMax());
            }
        }

        register.addJob(JobFactory.create(type, id, name, parameter));
        System.out.println("Job " + id + " added");
    }

    private void runJob() {
        String id = input.readNonEmptyString("Job ID to run: ");
        register.runJob(id);
        System.out.println("Job " + id + " completed");
    }

    private void runAllPending() {
        List<String> failures = register.runAllPendingJobs();
        if (failures.isEmpty()) {
            System.out.println("All pending jobs completed");
        } else {
            System.out.println(failures.size() + " job(s) failed:");
            failures.forEach(f -> System.out.println(" - " + f));
        }
    }

    private void searchByStatus() {
        JobStatus[] statuses = JobStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }
        int choice = input.readIntInRange("Choose job status: ", 1, statuses.length);
        printJobs(register.findByStatus(statuses[choice - 1]));
    }

    private void retryFailed() {
        List<String> failures = register.retryFailedJobs();
        if (failures.isEmpty()) {
            System.out.println("Retry finished with no failures");
        } else {
            System.out.println(failures.size() + " job(s) failed again:");
            failures.forEach(f -> System.out.println(" - " + f));
        }
    }

    private void removeJob() {
        String id = input.readNonEmptyString("Job ID to remove: ");
        if (input.readYesNo("Remove job " + id + "? (y/n): ")) {
            register.removeJob(id);
            System.out.println("Job " + id + " removed.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private void showReport() {
        System.out.println("=== Report ===");
        System.out.println("Total jobs:      " + register.getJobs().size());
        for (JobStatus status : JobStatus.values()) {
            System.out.println(status + ": " + register.findByStatus(status).size());
        }
        System.out.println("Estimated runtime of pending jobs: " + register.getTotalEstimatedRuntime() + " min");
        System.out.printf("Success rate: %.1f%%%n", register.getSuccessRate());
    }

    private void printJobs(List<BatchJob> jobs) {
        if (jobs.isEmpty()) {
            System.out.println("No jobs found.");
            return;
        }
        for (BatchJob job : jobs) {
            System.out.println(job.getDetails());
        }
    }
}