# Insurance Batch Processor

Utvecklare: Sidney Tellman

## Projektidé

A console application that manages and runs an insurance company's batch jobs (premium recalculation, claims settlement and renewal notices) against a register of insurance policies. The user can add, run, search and retry jobs, and view a batch run report.

## Superklass

- Namn: `BatchJob` (abstract class)
- Gemensamma fält: jobId, name, status (`JobStatus`: PENDING, COMPLETED, FAILED), retryCount
- Gemensamma metoder: `execute(PolicyRepository repo)`, `estimateRuntimeMinutes()`, `getDetails()`

## Subklasser 

1. `PremiumCalculationJob` — recalculates premiums using an index increase in percent. Overrides `execute()` and `estimateRuntimeMinutes()`.
2. `ClaimsSettlementJob` — pays out approved claims and fails if a claim exceeds the policy's coverage amount. Overrides `execute()` and `estimateRuntimeMinutes()`.
3. `RenewalNoticeJob` — finds policies expiring within a given number of days and generates renewal notices. Overrides `execute()` and `estimateRuntimeMinutes()`.

## Interface

- Namn: `Retryable`
- Metod(er): `canRetry()`, `retry(PolicyRepository repo)`
- Implementeras av (minst två subklasser): `PremiumCalculationJob`, `ClaimsSettlementJob`

`RenewalNoticeJob` deliberately does not implement it, since a rerun would send the same notice to the customer twice.

## Meny

1. Add job
2. Remove job
3. Run job by job ID
4. Run all pending jobs
5. Search jobs by status
6. Retry failed jobs
7. Show batch run report (success rate, total runtime, failed jobs)
0. Exit

## Felscenarion

- A claim in `ClaimsSettlementJob` exceeds the policy's coverage amount → `JobExecutionException`, and the job is marked FAILED.
- The user tries to run a job that is already COMPLETED → `InvalidJobStateException`.
- The user enters a job ID that doesn't exist → `JobNotFoundException`.
- A job is created with an empty name or a negative index percentage → `IllegalArgumentException` in the constructor.

## Motivering (fylls i senare i veckan)

> När ni kommit igång och gjort några ändringar: skriv kort varför strukturen ser ut som den gör, och om ni övervägde ett annat sätt att lösa det på. Detta behöver inte fyllas i redan i första commiten.

---

## Exempel (ifyllt) — Biblioteksystem

**Projektidé:** Ett system för att hantera ett biblioteks samling av utlåningsbara medier och vilka som är utlånade.

**Superklass**

- Namn: `Media`
- Gemensamma fält: title, id, isBorrowed (true/false)
- Gemensamma metoder: `showInfo()`, `borrow()`

**Subklasser**

1. `Book` — overridar `showInfo()` för att även visa författare
2. `Magazine` — overridar `borrow()` eftersom tidskrifter bara får lånas i en vecka
3. `Movie` — overridar `showInfo()` för att visa åldersgräns

**Interface**

- Namn: `Reservable`
- Metod: `reserve()`
- Implementeras av: `Book`, `Movie`

**Meny**

1. Lägga till medium
2. Ta bort medium
3. Söka på titel
4. Låna ut/lämna tillbaka
5. Reservera

**Felscenarion**

- Försök att låna ut ett medium som redan är utlånat
- Försök att skapa ett medium med tomt titel-fält
