package ui;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Reads and validates console input so the program never crashes on bad input.
 * <p>
 * Every method keeps asking until it gets a valid value. All input is read with
 * {@code nextLine()} and parsed afterwards, which avoids the classic bug where
 * {@code nextInt()} leaves a newline behind for the next read.
 */
public class InputHelper {

    private final Scanner scanner;

    /**
     * Creates a helper that reads from the given scanner.
     *
     * @param scanner the scanner to read from, usually {@code new Scanner(System.in)}
     */
    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Prints a prompt and reads one line of text.
     *
     * @param prompt text shown before reading
     * @return the trimmed line, or an empty string if the input stream has ended
     */
    private String readLine(String prompt) {
        System.out.print(prompt);
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Reads a string that is not empty.
     *
     * @param prompt text shown before reading
     * @return a string with at least one non-space character
     */
    public String readNonEmptyString(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads a whole number between {@code min} and {@code max}, inclusive.
     *
     * @param prompt text shown before reading
     * @param min    lowest allowed value
     * @param max    highest allowed value
     * @return a valid whole number within the range
     */
    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            String input = readLine(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Input must be a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("'" + input + "' is not a whole number. Try again.");
            }
        }
    }

    /**
     * Reads a decimal number between {@code min} and {@code max}, inclusive.
     * Accepts both "2.5" and "2,5", since Swedish keyboards use a comma.
     *
     * @param prompt text shown before reading
     * @param min    lowest allowed value
     * @param max    highest allowed value
     * @return a valid decimal number within the range
     */
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

    /**
     * Asks a yes/no question. Accepts English and Swedish answers.
     *
     * @param prompt text shown before reading, e.g. "Remove job? (y/n): "
     * @return {@code true} for y/yes/j/ja, {@code false} for n/no/nej
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            String input = readLine(prompt).toLowerCase();
            switch (input) {
                case "y", "yes", "j", "ja":
                    return true;
                case "n", "no", "nej":
                    return false;
                default:
                    System.out.println("Please answer y or n");
            }
        }
    }

    /**
     * Pauses until the user presses Enter, so results stay on screen before the menu is shown again.
     */
    public void waitForEnter() {
        readLine("Press Enter to return to the menu...");
    }
}