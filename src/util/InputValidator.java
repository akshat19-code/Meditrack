package util;

import java.util.*;
import java.time.*;
import java.time.format.*;

public class InputValidator {

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = sc.nextInt();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a whole number.");
                sc.next();
            }
        }
    }

    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = sc.nextDouble();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number (e.g. 100 or 100.50).");
                sc.next(); // clears the bad token out of the Scanner buffer
            }
        }
    }

    public static String readNonEmptyString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    public static String readAlphabeticString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty() && value.matches("[a-zA-Z ]+")) {
                return value;
            }
            System.out.println("Please enter letters only (no numbers or symbols).");
        }
    }

    public static double readNonNegativeDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("Value cannot be negative. Please try again.");
        }
    }

    public static double readPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than zero. Please try again.");
        }
    }

    public static String readMenuChoice(Scanner sc, String title, String[] labels, String[] values) {
        while (true) {
            System.out.println(title);
            for (int i = 0; i < labels.length; i++) {
                System.out.println((i + 1) + ". " + labels[i]);
            }
            int choice = readInt(sc, "Enter choice: ");

            if (choice >= 1 && choice <= values.length) {
                return values[choice - 1];
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    public static String readPhoneNumber(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (value.matches("[0-9]{10}")) {
                return value;
            }
            System.out.println("Please enter a valid 10-digit phone number (numbers only).");
        }
    }

    public static String readPincode(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (value.matches("[0-9]{6}")) {
                return value;
            }
            System.out.println("Please enter a valid 6-digit pincode (numbers only).");
        }
    }

    public static String readDate(Scanner sc, String prompt, boolean disallowFuture) {
        return readDate(sc, prompt, disallowFuture, false);
    }

    public static String readDate(Scanner sc, String prompt, boolean disallowFuture, boolean disallowPast) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(value);
                if (disallowFuture && date.isAfter(LocalDate.now())) {
                    System.out.println("Date cannot be in the future. Please try again.");
                    continue;
                }
                if (disallowPast && date.isBefore(LocalDate.now())) {
                    System.out.println("Date cannot be in the past. Please try again.");
                    continue;
                }
                return value;
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }

    public static String readAddressString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty() && value.matches(".*[a-zA-Z].*")) {
                return value;
            }
            System.out.println("Please enter a valid address (must contain letters, not just numbers).");
        }
    }

    public static String readEmail(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                return value;
            }
            System.out.println("Please enter a valid email address (e.g. name@example.com).");
        }
    }
}