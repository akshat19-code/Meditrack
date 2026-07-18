package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.*;

import java.util.*;

public class MainMenu {

    private Scanner sc;
    private MenuStack navStack;
    private AuthService authService = new AuthService();

    public MainMenu(Scanner sc, MenuStack navStack) {
        this.sc = sc;
        this.navStack = navStack;
    }

    public void show() {
        navStack.push("MainMenu");
        boolean flag = true;

        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("===== MediTrack - Main Menu =====");
            System.out.println("1. Master Admin Login");
            System.out.println("2. Hospital Admin Login");
            System.out.println("3. Doctor Login");
            System.out.println("4. Lab Technician Login");
            System.out.println("5. Patient Login");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> masterAdminLoginFlow();
                case 2 -> adminLoginFlow();
                case 3 -> doctorLoginFlow();
                case 4 -> labTechLoginFlow();
                case 5 -> patientLoginFlow();
                case 9 -> {
                    System.out.println("Exiting MediTrack. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void masterAdminLoginFlow() {
        System.out.print("Username: ");
        String username = sc.next();
        System.out.print("Password: ");
        String password = sc.next();

        MasterAdmin ma = authService.masterAdminLogin(username, password);
        if (ma != null) {
            new MasterAdminMenu(sc, navStack, ma).show();
        }
    }

    private void adminLoginFlow() {
        System.out.print("Hospital Code: ");
        String code = sc.next();
        System.out.print("Username: ");
        String username = sc.next();
        System.out.print("Password: ");
        String password = sc.next();

        Admin a = authService.adminLogin(code, username, password);
        if (a != null) {
            new AdminMenu(sc, navStack, a).show();
        }
    }

    private void doctorLoginFlow() {
        System.out.print("Hospital Code: ");
        String code = sc.next();
        System.out.print("Username: ");
        String username = sc.next();
        System.out.print("Password: ");
        String password = sc.next();

        Doctor d = authService.doctorLogin(code, username, password);
        if (d != null) {
            new DoctorMenu(sc, navStack, d).show();
        }
    }

    private void labTechLoginFlow() {
        System.out.print("Hospital Code: ");
        String code = sc.next();
        System.out.print("Username: ");
        String username = sc.next();
        System.out.print("Password: ");
        String password = sc.next();

        LabTechnician lt = authService.labTechnicianLogin(code, username, password);
        if (lt != null) {
            new LabTechnicianMenu(sc, navStack, lt).show();
        }
    }

    private void patientLoginFlow() {
        System.out.print("Hospital Code: ");
        String code = sc.next();
        System.out.print("Username: ");
        String username = sc.next();
        System.out.print("Password: ");
        String password = sc.next();

        Patient p = authService.patientLogin(code, username, password);
        if (p != null) {
            new PatientMenu(sc, navStack, p).show();
        }
    }
}