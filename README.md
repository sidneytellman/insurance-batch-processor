# Insurance Batch Processor

Utvecklare: Sidney Tellman

## Projektidé

A console application that manages and runs an insurance company's batch jobs (premium recalculation, claims settlement, renewal notices and claims audits) against a register of insurance policies and claims. The user can add, run, search, retry and remove jobs, and view a batch run report.

## Superklass

- Namn: `BatchJob` (abstract class)
- Gemensamma fält: `jobId`, `name`, `status` (`JobStatus`: PENDING, COMPLETED, FAILED), `retryCount`
- Gemensamma metoder:
  - `execute(PolicyRepository repository)` (abstract)
  - `estimateRuntimeMinutes(PolicyRepository repository)` (abstract)
  - `getDetails()`, which each subclass extends with its own details
  - `markCompleted()` / `markFailed()` (protected, so only the job itself can change its status)

## Subklasser

1. `PremiumCalculationJob`: raises every policy's premium by an index percentage. Fails if a new premium would exceed the policy's coverage. Remembers which policies it has already updated, so a retry never raises the same premium twice.
2. `ClaimsSettlementJob`: pays out approved, unpaid claims. Fails if a claim exceeds its policy's coverage amount. Claims paid before the failure stay paid, so a retry only processes what is left.
3. `RenewalNoticeJob`: prints a renewal notice for every policy expiring within a given number of days.
4. `ClaimsAuditJob`: flags approved claims above a threshold amount for manual review. Added last, to show that a new job type only needs a new class, one `JobType` constant and one line in `JobFactory`.

All four override `execute()`, `estimateRuntimeMinutes()` and `getDetails()`.

## Interface

- Namn: `Retryable`
- Metod(er): `canRetry()`, `retry(PolicyRepository repository)`, constant `MAX_RETRIES = 3`
- Implementeras av (minst två subklasser): `PremiumCalculationJob`, `ClaimsSettlementJob`

`RenewalNoticeJob` and `ClaimsAuditJob` deliberately do not implement it. Neither of them has a failure case, so there is nothing to retry, and rerunning a renewal job would send the same notice to a customer twice.

## Struktur

```
src/
  Main.java                   sample data + starts the menu
  exception/
    InvalidJobStateException.java
    JobExecutionException.java
    JobNotFoundException.java
  job/
    BatchJob.java             abstract superclass
    PremiumCalculationJob.java
    ClaimsSettlementJob.java
    RenewalNoticeJob.java
    ClaimsAuditJob.java
    Retryable.java            interface
    JobType.java              enum, name + parameter for each type
    JobFactory.java           creates a job from a JobType
  model/
    Policy.java
    Claim.java
    JobStatus.java
  service/
    PolicyRepository.java     policies and claims
    JobRegister.java          stores and runs jobs
  ui/
    InputHelper.java          input that can't crash the program
    Menu.java
```

Calls go downward: `Menu` uses `JobRegister`, which runs the jobs, which read and update data through `PolicyRepository`. The menu never calculates or validates anything itself.

## Meny

1. List all jobs
2. Add a job (the type list is built from `JobType.values()`)
3. Run a job by job ID
4. Run all pending jobs
5. Search jobs by status
6. Retry failed jobs
7. Remove a job (asks for confirmation)
8. Show report (jobs per status, estimated runtime of pending jobs, success rate)
0. Exit

After each action the program waits for Enter, so the result stays on screen.

## Felscenarion

- A claim exceeds its policy's coverage amount → `JobExecutionException`, and `ClaimsSettlementJob` is marked FAILED.
- A new premium would exceed the coverage amount → `JobExecutionException`, and `PremiumCalculationJob` is marked FAILED.
- The user runs a job that is not PENDING → `InvalidJobStateException`.
- The user enters a job ID that doesn't exist → `JobNotFoundException`.
- The user adds a job with an ID that is already in use → `IllegalArgumentException`.
- A job, policy or claim is created with empty text or a zero/negative amount → `IllegalArgumentException` in the constructor.
- The user types letters, nothing, or a number out of range at any prompt → `InputHelper` explains the problem and asks again.

`Menu` catches each custom exception in its own catch block with its own message, so an error never stops the program.

## Testdata

`Main` creates five policies, five claims and one job of each type (J-001 to J-004). Expiry dates are relative to today, so the renewal job always finds policies to notify.

Claim C-505 (900,000 kr) deliberately exceeds the coverage of policy P-1004 (800,000 kr). This makes `ClaimsSettlementJob` fail, so the FAILED status and the error handling can be demonstrated. Running all pending jobs gives a success rate of 75 %.

Retrying the failed job processes the remaining claims but fails again on C-505, since the claim is still too large. After `MAX_RETRIES` attempts, `canRetry()` returns false and the job is no longer retried. In a real system someone would correct the claim before the rerun.

## Motivering

**Abstract class plus interface.** Everything every job has in common (ID, name, status, retry count) lives in `BatchJob`, so each subclass only contains what makes it different. Retrying is not something every job needs, so it is an interface instead of a method in the superclass. That way only the jobs that change data can be retried. `JobRegister` checks for the interface (`instanceof Retryable`) rather than for specific classes, so it never needs to know which job types exist.

**Protected status methods.** Status can only be changed by the job itself through `markCompleted()` and `markFailed()`. The menu or register can never set a job to COMPLETED by mistake.

**Packages.** I split the code into model, service, job and ui packages so each class has one responsibility. `Menu` only talks to the user, and `JobRegister` holds the rules for running jobs. This made it easy to change how input works (adding the Enter pause, for example) without touching any logic.

**JobType and JobFactory.** At first, `Menu` had a switch that created each job class directly. That meant every new job type required a change in the menu. I moved that decision into `JobFactory` and let the menu build its type list from `JobType`. Adding `ClaimsAuditJob` afterwards only took a new class, one enum constant and one line in the factory, with no change to `Menu`. The factory uses a switch expression with no default case, so the code will not compile if a new `JobType` is added without handling it.

**Custom exceptions.** Each error has its own exception with a name that says what went wrong, so the menu can give a specific message instead of catching a generic `Exception`. They extend `RuntimeException`, so methods do not need `throws` declarations everywhere. I considered checked exceptions, but they would have spread `throws` clauses through every layer without making the error handling clearer.

**Safe reruns.** The domain comes from my work with insurance batch processing, where a rerun must never apply the same change twice. That is why `PremiumCalculationJob` remembers which policies it has already updated, and why `ClaimsSettlementJob` only picks up unpaid claims.

**What I would do differently with more time.** All data is kept in memory and disappears when the program closes. The next step would be saving policies, claims and jobs to a file or database. I would also add a way to correct a failed claim from the menu, so a retry could actually succeed.