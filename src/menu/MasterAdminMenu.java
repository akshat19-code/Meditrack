package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.InputValidator;

import java.util.List;
import java.util.Scanner;

public class MasterAdminMenu {

    private Scanner sc;
    private MenuStack navStack;
    private MasterAdmin loggedInMasterAdmin;
    private HospitalDAO hospitalDAO = new HospitalDAO();

    public MasterAdminMenu(Scanner sc, MenuStack navStack, MasterAdmin ma) {
        this.sc = sc;
        this.navStack = navStack;
        this.loggedInMasterAdmin = ma;
    }

    public void show() {
        navStack.push("MasterAdminMenu");
        boolean flag = true;

        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("===== Master Admin Menu =====");
            System.out.println("1. Register New Hospital");
            System.out.println("2. View All Hospitals");
            System.out.println("3. Suspend / Reactivate Hospital");
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> registerHospital();
                case 2 -> viewAllHospitals();
                case 3 -> updateHospitalStatus();
                case 0 -> {
                    navStack.pop();
                    flag = false;
                }
                case 9 -> {
                    System.out.println("Exiting MediTrack. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void registerHospital() {
        navStack.push("RegisterHospital");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine(); // clear leftover newline
        System.out.print("Hospital Code: ");
        String code = sc.nextLine();

        // Duplicate HospitalCode check - this code is what every Admin/Doctor/
        // LabTech/Patient types at login to identify their hospital, so a
        // duplicate here would make login lookups ambiguous.
        if (hospitalDAO.getHospitalByCode(code) != null) {
            System.out.println("A hospital with this Hospital Code already exists.");
            navStack.pop();
            return;
        }

        System.out.print("Hospital Name: ");
        String name = sc.nextLine();
        System.out.print("Street: ");
        String street = sc.nextLine();
        String city = InputValidator.readAlphabeticString(sc, "City: ");
        String state = InputValidator.readAlphabeticString(sc, "State: ");
        String pincode = InputValidator.readPincode(sc, "Pincode: ");
        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");
        System.out.print("Email: ");
        String email = sc.nextLine();

        Hospital h = new Hospital();
        h.setHospitalCode(code);
        h.setHospitalName(name);
        h.setStreet(street);
        h.setCity(city);
        h.setState(state);
        h.setPincode(pincode);
        h.setPhoneNo(phone);
        h.setEmail(email);
        h.setStatus("ACTIVE");
        h.setMasterAdminID(loggedInMasterAdmin.getMasterAdminID());

        boolean success = hospitalDAO.insertHospital(h);
        if (success) {
            System.out.println("Hospital registered successfully!");
        } else {
            System.out.println("Failed to register hospital.");
        }

        navStack.pop();
    }

    private void viewAllHospitals() {
        navStack.push("ViewAllHospitals");
        System.out.println("\nPath: " + navStack.getPath());

        List<Hospital> hospitals = hospitalDAO.getAllHospitals();
        if (hospitals.isEmpty()) {
            System.out.println("No hospitals registered yet.");
        } else {
            for (Hospital h : hospitals) {
                System.out.println(h);
            }
        }

        navStack.pop();
    }

    private void updateHospitalStatus() {
        navStack.push("UpdateHospitalStatus");
        System.out.println("\nPath: " + navStack.getPath());

        // Show existing hospitals first, so Master Admin can see the ID and
        // current status of each hospital before being asked to pick one -
        // reuses the same getAllHospitals() call that "View All Hospitals" uses.
        List<Hospital> hospitals = hospitalDAO.getAllHospitals();
        if (hospitals.isEmpty()) {
            System.out.println("No hospitals registered yet.");
            navStack.pop();
            return;
        }

        System.out.println("---- Existing Hospitals ----");
        for (Hospital h : hospitals) {
            System.out.println(h);
        }
        System.out.println("----------------------------");

        int hospitalId = InputValidator.readInt(sc, "Enter Hospital ID: ");

        Hospital h = hospitalDAO.getHospitalById(hospitalId);
        if (h == null) {
            System.out.println("Hospital not found.");
            navStack.pop();
            return;
        }

        if (h.getStatus().equalsIgnoreCase("REMOVED")) {
            System.out.println("This hospital has been REMOVED and cannot be reactivated.");
            navStack.pop();
            return;
        }

        sc.nextLine(); // clear leftover newline before the menu prompt

        String status = InputValidator.readMenuChoice(sc, "New Status:",
                new String[]{"ACTIVE", "SUSPENDED", "REMOVED"},
                new String[]{"ACTIVE", "SUSPENDED", "REMOVED"});

        sc.nextLine(); // clear leftover newline from readMenuChoice's internal nextInt()

        // Confirmation prompt before a destructive/hard-to-reverse status change
        if (status.equals("SUSPENDED") || status.equals("REMOVED")) {
            System.out.print("Are you sure you want to set this hospital to " + status + "? (Y/N): ");
            String confirm = sc.nextLine();
            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("Status change cancelled.");
                navStack.pop();
                return;
            }
        }

        boolean success = hospitalDAO.updateHospitalStatus(hospitalId, status);
        if (success) {
            System.out.println("Hospital status updated to " + status);
        } else {
            System.out.println("Failed to update hospital status.");
        }

        navStack.pop();
    }
}