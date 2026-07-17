package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;

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
            System.out.println("3. Suspend / Reactivate / Remove Hospital");
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

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
        System.out.print("Hospital Name: ");
        String name = sc.nextLine();
        System.out.print("Street: ");
        String street = sc.nextLine();
        System.out.print("City: ");
        String city = sc.nextLine();
        System.out.print("State: ");
        String state = sc.nextLine();
        System.out.print("Pincode: ");
        String pincode = sc.nextLine();
        System.out.print("Phone No: ");
        String phone = sc.nextLine();
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
        System.out.println("\nAvailable Hospitals:");

        List<Hospital> hospitals = hospitalDAO.getAllHospitals();

        for (Hospital h : hospitals) {
            System.out.println(
                    h.getHospitalID() + " -> " +
                            h.getHospitalCode() + " -> " +
                            h.getHospitalName() +
                            " (" + h.getStatus() + ")"
            );
        }

        System.out.println();

        System.out.print("Enter Hospital ID: ");
        int hospitalId = sc.nextInt();
        System.out.print("New Status (ACTIVE / SUSPENDED / REMOVED): ");
        String status = sc.next();

        boolean success = hospitalDAO.updateHospitalStatus(hospitalId, status.toUpperCase());
        if (success) {
            System.out.println("Hospital status updated to " + status.toUpperCase());
        } else {
            System.out.println("Failed to update hospital status.");
        }

        navStack.pop();
    }
}