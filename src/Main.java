import job.ClaimsSettlementJob;
import job.PremiumCalculationJob;
import job.RenewalNoticeJob;
import model.Claim;
import model.Policy;
import service.JobRegister;
import service.PolicyRepository;
import ui.InputHelper;
import ui.Menu;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        PolicyRepository repository = new PolicyRepository();
        addSampleData(repository);

        JobRegister register = new JobRegister(repository);
        register.addJob(new PremiumCalculationJob("J-001", "Annual premium indexation", 3.5));
        register.addJob(new ClaimsSettlementJob("J-002", "Nightly settlement"));
        register.addJob(new RenewalNoticeJob("J-003", "Monthly renewal notices", 30));

        InputHelper input = new InputHelper(new Scanner(System.in));
        new Menu(register, input).start();
    }

    private static void addSampleData(PolicyRepository repository) {
        LocalDate today = LocalDate.now();

        repository.addPolicy(new Policy("P-1001", "Anna Lindqvist", 4200, 1_500_000, today.plusDays(12)));
        repository.addPolicy(new Policy("P-1002", "Erik Johansson", 3100, 950_000, today.plusDays(25)));
        repository.addPolicy(new Policy("P-1003", "Maria Nilsson", 5600, 2_200_000, today.plusMonths(4)));
        repository.addPolicy(new Policy("P-1004", "Johan Berg", 2800, 800_000, today.plusMonths(9)));
        repository.addPolicy(new Policy("P-1005", "Sara Andersson", 3900, 1_200_000, today.plusDays(5)));

        repository.addClaim(new Claim("C-501", "P-1001", 12_500, true));
        repository.addClaim(new Claim("C-502", "P-1002", 4_300, true));
        repository.addClaim(new Claim("C-503", "P-1003", 27_000, false));
        repository.addClaim(new Claim("C-504", "P-1005", 8_750, true));
    }
}