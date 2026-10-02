package ui;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputHelper {

    private Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public double readDoubleInRange(String prompt, double min, double max) {
        while (true) {
            String input = readLine(prompt).replace(',', '.');
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Input must be a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("'" + input + "' is not a number. Try again.");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String input = readLine(prompt).toLowerCase();
            switch (input) {
                case "y", "yes", "j", "ja"
                    ;
                    return true;
                case "n", "no", "nej"
                    ;
                    return false;
                default:
                    System.out.println("Please answer y or n");
            }
        }
    }
}
