package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.*;
import java.util.*;

public class MasterAdminMenu {

    private Scanner sc;
    private MenuStack navStack;
    private MasterAdmin loggedInMasterAdmin;
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private MasterAdminDAO masterAdminDAO = new MasterAdminDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();
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
            System.out.println("7. Manage Hospital Admin");
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
                case 7 -> manageHospitalAdmin();
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
            printHospitalTable(hospitals);
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
        printHospitalTable(hospitals);

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

        System.out.print("Enter your Master Admin Password to confirm: ");
        String password = sc.nextLine();

        if (!loggedInMasterAdmin.getPassword().equals(password)) {
            System.out.println("Incorrect password. Status change cancelled.");
            navStack.pop();
            return;
        }

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
        printLoginLogTable(log);

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

        printHospitalTable(hospitals);

        int hospitalId = InputValidator.readInt(sc, "Enter Hospital ID: ");
        sc.nextLine();

        Hospital h = hospitalDAO.getHospitalById(hospitalId);
        if (h == null) {
            System.out.println("Hospital not found.");
            navStack.pop();
            return;
        }

        String log = fileManager.readHospitalLoginLog(h.getHospitalCode());
        printLoginLogTable(log);

        navStack.pop();
    }

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

    private void addHospitalAdmin() {
        navStack.push("AddHospitalAdmin");
        System.out.println("\nPath: " + navStack.getPath());

        List<Hospital> hospitals = hospitalDAO.getAllHospitals();
        if (hospitals.isEmpty()) {
            System.out.println("No hospitals registered yet.");
            navStack.pop();
            return;
        }

        printHospitalTable(hospitals);

        int hospitalId = InputValidator.readInt(sc, "Enter Hospital ID: ");
        sc.nextLine();

        Hospital h = hospitalDAO.getHospitalById(hospitalId);
        if (h == null) {
            System.out.println("Hospital not found.");
            navStack.pop();
            return;
        }

        createAdminForHospital(hospitalId);

        navStack.pop();
    }

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

        if (adminDAO.getAdminByEmail(email) != null) {
            System.out.println("An admin with this email already exists.");
            return;
        }

        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");

        if (adminDAO.getAdminByPhone(phone) != null) {
            System.out.println("An admin with this phone number already exists.");
            return;
        }

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

    private void manageHospitalAdmin() {
        navStack.push("ManageHospitalAdmin");
        System.out.println("\nPath: " + navStack.getPath());

        List<Hospital> hospitals = hospitalDAO.getAllHospitals();
        if (hospitals.isEmpty()) {
            System.out.println("No hospitals registered yet.");
            navStack.pop();
            return;
        }

        printHospitalTable(hospitals);

        int hospitalId = InputValidator.readInt(sc, "Enter Hospital ID: ");
        sc.nextLine();

        Hospital h = hospitalDAO.getHospitalById(hospitalId);
        if (h == null) {
            System.out.println("Hospital not found.");
            navStack.pop();
            return;
        }

        boolean flag = true;
        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("---- Manage Hospital Admin (" + h.getHospitalName() + ") ----");
            System.out.println("1. View Hospital Admins");
            System.out.println("2. Add Hospital Admin");
            System.out.println("3. Remove Hospital Admin");
            System.out.println("0. Back");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> viewHospitalAdmins(hospitalId);
                case 2 -> createAdminForHospital(hospitalId);
                case 3 -> removeHospitalAdmin(hospitalId);
                case 0 -> flag = false;
                default -> System.out.println("Invalid choice.");
            }
        }

        navStack.pop();
    }

    private void viewHospitalAdmins(int hospitalId) {
        List<Admin> admins = adminDAO.getAllAdminsByHospital(hospitalId);
        if (admins.isEmpty()) {
            System.out.println("No admins found for this hospital.");
        } else {
            printAdminTable(admins);
        }
    }

    private void removeHospitalAdmin(int hospitalId) {
        List<Admin> admins = adminDAO.getAllAdminsByHospital(hospitalId);
        if (admins.isEmpty()) {
            System.out.println("No admins found for this hospital.");
            return;
        }

        printAdminTable(admins);

        int adminId = InputValidator.readInt(sc, "Enter Admin ID to remove: ");
        sc.nextLine();

        Admin toRemove = null;
        List<Admin> otherAdmins = new ArrayList<>();
        for (Admin a : admins) {
            if (a.getAdminID() == adminId) {
                toRemove = a;
            } else {
                otherAdmins.add(a);
            }
        }

        if (toRemove == null) {
            System.out.println("Admin not found in this hospital.");
            return;
        }

        System.out.print("Enter your Master Admin Password to confirm: ");
        String password = sc.nextLine();

        if (!loggedInMasterAdmin.getPassword().equals(password)) {
            System.out.println("Incorrect password. Removal cancelled.");
            return;
        }

        System.out.print("Are you sure you want to remove Admin '" + toRemove.getName() + "'? (Y/N): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Removal cancelled.");
            return;
        }

        if (!otherAdmins.isEmpty()) {
            System.out.println("Transfer existing admissions to another admin before removal:");
            printAdminTable(otherAdmins);

            int newAdminId = InputValidator.readInt(sc, "Enter Admin ID to transfer admissions to: ");
            sc.nextLine();

            boolean validTarget = false;
            for (Admin a : otherAdmins) {
                if (a.getAdminID() == newAdminId) {
                    validTarget = true;
                    break;
                }
            }

            if (!validTarget) {
                System.out.println("Invalid Admin ID selected. Removal cancelled.");
                return;
            }

            boolean reassigned = admissionDAO.reassignAdminForAdmissions(adminId, newAdminId);
            if (!reassigned) {
                System.out.println("Failed to transfer admissions. Removal cancelled.");
                return;
            }
            System.out.println("Admissions transferred successfully.");
        }

        boolean deleted = adminDAO.deleteAdmin(adminId);
        if (deleted) {
            System.out.println("Admin removed successfully!");
        } else {
            System.out.println("Failed to remove admin. They may still have associated records.");
        }
    }

    private void printAdminTable(List<Admin> admins) {
        System.out.println("-".repeat(85));
        System.out.printf("%-5s %-25s %-20s %-15s%n",
                "ID", "Name", "Username", "Phone");
        System.out.println("-".repeat(85));

        for (Admin a : admins) {
            System.out.printf("%-5d %-25s %-20s %-15s%n",
                    a.getAdminID(),
                    a.getName(),
                    a.getUsername(),
                    a.getPhoneNo());
        }

        System.out.println("-".repeat(85));
    }

    private void printHospitalTable(List<Hospital> hospitals){
        System.out.println("-".repeat(85));
        System.out.printf("%-5s %-8s %-35s %-15s %-10s%n",
                "ID",
                "Code",
                "Hospital Name",
                "City",
                "Status");
        System.out.println("-".repeat(85));

        for (Hospital h : hospitals) {
            System.out.printf("%-5d %-8s %-35s %-15s %-10s%n",
                    h.getHospitalID(),
                    h.getHospitalCode(),
                    h.getHospitalName(),
                    h.getCity(),
                    h.getStatus());
        }

        System.out.println("-".repeat(85));
    }

    private void printLoginLogTable(String log) {
        if (log == null || log.isBlank()) {
            System.out.println("No login records found.");
            return;
        }

        String[] lines = log.split("\r?\n");
        List<String[]> rows = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] parsed = parseLoginLogLine(trimmed);
            if (parsed != null) {
                rows.add(parsed);
            }
        }

        if (rows.isEmpty()) {
            System.out.println("No login records found.");
            return;
        }

        System.out.println("-".repeat(97));
        System.out.printf("%-23s %-10s %-17s %-21s %-15s%n",
                "Timestamp", "Status", "Role", "Username", "Hospital Code");
        System.out.println("-".repeat(97));

        for (String[] row : rows) {
            System.out.printf("%-23s %-10s %-17s %-21s %-15s%n",
                    row[0], row[1], row[2], row[3], row[4]);
        }

        System.out.println("-".repeat(97));
    }

    private String[] parseLoginLogLine(String line) {
        try {
            if (!line.startsWith("[")) {
                return null;
            }

            int closeBracket = line.indexOf(']');
            if (closeBracket == -1) {
                return null;
            }

            String timestamp = line.substring(1, closeBracket).trim();
            String remainder = line.substring(closeBracket + 1).trim();

            String[] parts = remainder.split("\\|");
            if (parts.length < 4) {
                return null;
            }

            String status = parts[0].trim();
            String role = parts[1].replace("Role:", "").trim();
            String username = parts[2].replace("Username:", "").trim();
            String hospitalCode = parts[3].replace("Hospital Code:", "").trim();

            return new String[]{timestamp, status, role, username, hospitalCode};

        } catch (Exception e) {
            return null;
        }
    }
}