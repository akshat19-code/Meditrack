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
    private MasterAdminDAO masterAdminDAO = new MasterAdminDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private FileManager fileManager = new FileManager();

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
            System.out.println("4. View Combined Login Log (All Hospitals)");
            System.out.println("5. View Login Log For One Hospital");
            System.out.println("6. Change Password");
            System.out.println("7. Add Hospital Admin");
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> registerHospital();
                case 2 -> viewAllHospitals();
                case 3 -> updateHospitalStatus();
                case 4 -> viewCombinedLoginLog();
                case 5 -> viewLoginLogForOneHospital();
                case 6 -> changePassword();
                case 7 -> addHospitalAdmin();
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

        sc.nextLine();
        System.out.print("Hospital Code: ");
        String code = sc.nextLine();

        if (hospitalDAO.getHospitalByCode(code) != null) {
            System.out.println("A hospital with this Hospital Code already exists.");
            navStack.pop();
            return;
        }

        System.out.print("Hospital Name: ");
        String name = sc.nextLine();
        String street = InputValidator.readAddressString(sc, "Street: ");
        String city = InputValidator.readAlphabeticString(sc, "City: ");
        String state = InputValidator.readAlphabeticString(sc, "State: ");
        String pincode = InputValidator.readPincode(sc, "Pincode: ");
        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");
        String email = InputValidator.readEmail(sc, "Email: ");

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

            Hospital newHospital = hospitalDAO.getHospitalByCode(code);
            if (newHospital != null) {
                System.out.print("Add a Hospital Admin for this hospital now? (Y/N): ");
                String addNow = sc.nextLine();
                if (addNow.equalsIgnoreCase("Y")) {
                    createAdminForHospital(newHospital.getHospitalID());
                } else {
                    System.out.println("Skipped. You can add one later via 'Add Hospital Admin' in the Master Admin Menu.");
                }
            }
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

        sc.nextLine();

        String status = InputValidator.readMenuChoice(sc, "New Status:",
                new String[]{"ACTIVE", "SUSPENDED", "REMOVED"},
                new String[]{"ACTIVE", "SUSPENDED", "REMOVED"});

        sc.nextLine();

        if (h.getStatus().equalsIgnoreCase(status)) {
            System.out.println("Hospital is already " + status + ". Status unchanged.");
            navStack.pop();
            return;
        }

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

    private void viewCombinedLoginLog() {
        navStack.push("ViewCombinedLoginLog");
        System.out.println("\nPath: " + navStack.getPath());

        String log = fileManager.readSystemLoginLog();
        System.out.println(log);

        navStack.pop();
    }

    private void viewLoginLogForOneHospital() {
        navStack.push("ViewLoginLogForOneHospital");
        System.out.println("\nPath: " + navStack.getPath());

        List<Hospital> hospitals = hospitalDAO.getAllHospitals();
        if (hospitals.isEmpty()) {
            System.out.println("No hospitals registered yet.");
            navStack.pop();
            return;
        }

        String[] labels = new String[hospitals.size()];
        String[] values = new String[hospitals.size()];

        for (int i = 0; i < hospitals.size(); i++) {
            Hospital h = hospitals.get(i);
            labels[i] = h.getHospitalName() + " (" + h.getHospitalCode() + ")";
            values[i] = h.getHospitalCode();
        }

        String selectedCode = InputValidator.readMenuChoice(sc, "Select Hospital:", labels, values);
        sc.nextLine();

        String log = fileManager.readHospitalLoginLog(selectedCode);
        System.out.println(log);

        navStack.pop();
    }

    // Asks for the CURRENT password first (proves it's really the logged-in
    // Master Admin typing, not just anyone at an unlocked session), then the
    // new password twice (typo protection - mismatched entries are rejected
    // before anything is saved). Updates the in-memory loggedInMasterAdmin
    // object too after a successful change, so a second Change Password in
    // this same session compares against the NEW password, not a stale one.
    private void changePassword() {
        navStack.push("ChangePassword");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Enter Current Password: ");
        String currentPassword = sc.nextLine();

        if (!loggedInMasterAdmin.getPassword().equals(currentPassword)) {
            System.out.println("Incorrect current password.");
            navStack.pop();
            return;
        }

        String newPassword = InputValidator.readNonEmptyString(sc, "Enter New Password: ");
        String confirmPassword = InputValidator.readNonEmptyString(sc, "Confirm New Password: ");

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match. Password not changed.");
            navStack.pop();
            return;
        }

        boolean success = masterAdminDAO.updatePassword(loggedInMasterAdmin.getMasterAdminID(), newPassword);
        if (success) {
            loggedInMasterAdmin.setPassword(newPassword);
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }

        navStack.pop();
    }

    // Lets Master Admin pick any existing hospital - new or old - and give it
    // an Admin account. This is the only path in the app that can create one,
    // so it covers every case: a brand-new hospital, an old one that never
    // got an admin, a second admin, or replacing a lost one.
    private void addHospitalAdmin() {
        navStack.push("AddHospitalAdmin");
        System.out.println("\nPath: " + navStack.getPath());

        List<Hospital> hospitals = hospitalDAO.getAllHospitals();
        if (hospitals.isEmpty()) {
            System.out.println("No hospitals registered yet.");
            navStack.pop();
            return;
        }

        String[] labels = new String[hospitals.size()];
        Integer[] hospitalIds = new Integer[hospitals.size()];

        for (int i = 0; i < hospitals.size(); i++) {
            Hospital h = hospitals.get(i);
            labels[i] = h.getHospitalName() + " (" + h.getHospitalCode() + ")";
            hospitalIds[i] = h.getHospitalID();
        }

        System.out.println("---- Select Hospital ----");
        for (int i = 0; i < labels.length; i++) {
            System.out.println((i + 1) + ". " + labels[i]);
        }
        int choice = InputValidator.readInt(sc, "Enter choice: ");
        sc.nextLine();

        if (choice < 1 || choice > hospitalIds.length) {
            System.out.println("Invalid choice.");
            navStack.pop();
            return;
        }

        createAdminForHospital(hospitalIds[choice - 1]);

        navStack.pop();
    }

    // Shared logic behind both "Add Hospital Admin" and the convenience
    // prompt right after registering a hospital. Takes an already-known
    // HospitalID, so it doesn't care whether the hospital is brand new
    // or years old.
    private void createAdminForHospital(int hospitalId) {
        System.out.println("\n---- New Hospital Admin ----");

        String firstName = InputValidator.readNonEmptyString(sc, "First Name: ");
        String lastName = InputValidator.readNonEmptyString(sc, "Last Name: ");
        String username = InputValidator.readNonEmptyString(sc, "Username: ");

        if (adminDAO.getAdminByUsername(username, hospitalId) != null) {
            System.out.println("An admin with this username already exists in this hospital.");
            return;
        }

        String password = InputValidator.readNonEmptyString(sc, "Password: ");
        String email = InputValidator.readEmail(sc, "Email: ");
        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");

        Admin a = new Admin();
        a.setFirstName(firstName);
        a.setLastName(lastName);
        a.setUsername(username);
        a.setPassword(password);
        a.setEmail(email);
        a.setPhoneNo(phone);
        a.setHospitalID(hospitalId);

        boolean success = adminDAO.insertAdmin(a);
        System.out.println(success ? "Hospital Admin added successfully!" : "Failed to add Hospital Admin.");
    }
}