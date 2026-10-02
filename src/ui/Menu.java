package ui;

import exception.InvalidJobStateException;
import exception.JobExecutionException;
import exception.JobNotFoundException;
import job.BatchJob;
import job.ClaimsSettlementJob;
import job.PremiumCalculationJob;
import job.RenewalNoticeJob;
import model.JobStatus;
import service.JobRegister;

import java.util.List;

public class Menu {

    private final JobRegister register;
    private final InputHelper input;

    public Menu(JobRegister register, InputHelper input) {
        this.register = register;
        this.input = input;
    }

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

    public void printMenu() {
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

    private void addJob() {
        System.out.println("Job type");
        System.out.println("1. Premium calculation");
        System.out.println("2. Claims settlement");
        System.out.println("3. Renewal notice");
        int type = input.readIntInRange("Choose job type: ", 1, 3);
        String id = input.readNonEmptyString("Job ID: ");
        String name = input.readNonEmptyString("Job name: ");

        BatchJob job = switch (type) {
            case 1 -> new PremiumCalculationJob(id, name, input.readDoubleInRange("Index increase (%): ", 0, 100));
            case 2 -> new ClaimsSettlementJob(id, name);
            default ->
                    new RenewalNoticeJob(id, name, input.readIntInRange("Notify policies expiring within how many days: ", 1, 365));
        };
        register.addJob(job);
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