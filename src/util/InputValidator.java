package util;

import java.util.InputMismatchException;
import java.util.Scanner;

// Small centralized helper for reading and validating input safely.
// Every menu class was previously calling sc.nextInt() / sc.nextDouble()
// directly, which crashes the whole program with an uncaught
// InputMismatchException if the user types anything non-numeric.
// This class also centralizes basic field validation (non-empty text,
// non-negative/positive numbers, and restricted choice lists like
// Priority or RoomType) so every menu applies the same rules the same way.
public class InputValidator {

    // Keeps asking until the user enters a valid integer
    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = sc.nextInt();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a whole number.");
                sc.next(); // clears the bad token out of the Scanner buffer
            }
        }
    }

    // Keeps asking until the user enters a valid decimal number
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

    // Keeps asking until the user types something that isn't blank -
    // used for names, usernames, passwords, etc. so nobody can just
    // hit Enter and leave a required field empty in the database.
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

    // Keeps asking until the user enters text containing only letters and spaces -
// used for City/State where numbers genuinely don't make sense (unlike Street,
// which is left as plain non-empty text since real addresses contain numbers).
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

    // Keeps asking until the user enters zero or a positive number -
    // used for NormalMin/NormalMax, where 0 is a genuinely valid value
    // (e.g. Urine Routine Examination's NormalMin is 0).
    public static double readNonNegativeDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("Value cannot be negative. Please try again.");
        }
    }

    // Keeps asking until the user enters a number greater than zero -
    // used for actual charges/fees (ConsultationFee, RoomCharge, TestCharge),
    // where a charge of 0 doesn't make sense.
    public static double readPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than zero. Please try again.");
        }
    }

    // Keeps asking until the user's input (case-insensitive) matches one of
    // the allowed values - used for Priority, RoomType, Gender, BloodGroup,
    // and anywhere else a column should only ever hold one of a fixed set
    // of values, instead of any free-typed string.
    public static String readValidatedChoice(Scanner sc, String prompt, String[] allowedValues) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim().toUpperCase();
            for (String allowed : allowedValues) {
                if (allowed.equals(value)) {
                    return value;
                }
            }
            System.out.println("Invalid value. Allowed options: " + String.join(", ", allowedValues));
        }
    }

    // Prints a numbered menu of fixed options and returns the matching String
    // value once the user picks a valid number - used anywhere a field only
    // accepts a predefined set of values (Hospital Status, Priority, Room Type,
    // Gender, Blood Group, etc.), instead of asking the user to type the exact
    // value themselves. The label shown to the user and the actual String
    // stored in the database can be different (labels array vs values array),
    // but in every current use case they are the same text.
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

    // Keeps asking until the user enters exactly 10 digits - used for PhoneNo,
// so neither text nor a wrong-length number gets stored.
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

    // Keeps asking until the user enters exactly 6 digits - used for Pincode,
    // matching the standard Indian postal code format.
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

    // Keeps asking until the user enters a real, valid calendar date in
// YYYY-MM-DD format, using java.time.LocalDate to actually parse it -
// used for DOB, so garbage like "abcd" or "2026-13-45" can't get stored.
// disallowFuture=true additionally rejects dates after today (for DOB,
// since a birth date in the future makes no sense).
    public static String readDate(Scanner sc, String prompt, boolean disallowFuture) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(value);
                if (disallowFuture && date.isAfter(java.time.LocalDate.now())) {
                    System.out.println("Date cannot be in the future. Please try again.");
                    continue;
                }
                return value;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }
}