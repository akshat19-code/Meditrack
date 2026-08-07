package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.*;

import java.util.*;

public class PatientMenu {

    private Scanner sc;
    private MenuStack navStack;
    private Patient loggedInPatient;

    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private ReportDAO reportDAO = new ReportDAO();
    private BillDAO billDAO = new BillDAO();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private HealthScoreService healthScoreService = new HealthScoreService();
    private FileManager fileManager = new FileManager();

    public PatientMenu(Scanner sc, MenuStack navStack, Patient p) {
        this.sc = sc;
        this.navStack = navStack;
        this.loggedInPatient = p;
    }

    public void show() {
        navStack.push("PatientMenu");
        boolean flag = true;

        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("===== Patient Menu =====");
            System.out.println("1. View My Details");
            System.out.println("2. View Report History");
            System.out.println("3. View Health Score");
            System.out.println("4. View Admission Status & Bill");
            System.out.println("5. View My History");
            System.out.println("6. Change Password");
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> viewMyDetails();
                case 2 -> viewReportHistory();
                case 3 -> viewHealthScore();
                case 4 -> viewAdmissionAndBill();
                case 5 -> viewMyHistory();
                case 6 -> changePassword();
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

    private void viewMyDetails() {
        navStack.push("ViewMyDetails");
        System.out.println("\nPath: " + navStack.getPath());

        int age = patientDAO.calculateAge(loggedInPatient.getDob());
        printPatientDetails(loggedInPatient, age);

        navStack.pop();
    }

    private String getTestNameForReport(Report r) {
        TestRequest tr = testRequestDAO.getTestRequestById(r.getTestRequestID());
        if (tr == null) {
            return "Unknown Test";
        }

        TestType tt = testTypeDAO.getTestTypeById(tr.getTestTypeID());
        return (tt != null) ? tt.getTestName() : "Unknown Test";
    }

    private void viewReportHistory() {
        navStack.push("ViewReportHistory");
        System.out.println("\nPath: " + navStack.getPath());

        List<Report> reports = reportDAO.getReportsByPatient(loggedInPatient.getPatientID());

        if (reports.isEmpty()) {
            System.out.println("No report history available yet.");
        } else {
            PatientHistoryList historyList = new PatientHistoryList();
            for (Report r : reports) {
                String testName = getTestNameForReport(r);
                historyList.addLast(r.getReportID(), testName, r.getResultValue(),
                        r.getResultStatus(), r.getAnalysisDate());
            }
            historyList.displayFromLast();
        }
        navStack.pop();
    }

    private void viewHealthScore() {
        navStack.push("ViewHealthScore");
        System.out.println("\nPath: " + navStack.getPath());

        double score = healthScoreService.calculateHealthScore(loggedInPatient.getPatientID());
        System.out.println("Your Health Score: " + String.format("%.2f", score) + " / 100");

        navStack.pop();
    }

    private void viewAdmissionAndBill() {
        navStack.push("ViewAdmissionAndBill");
        System.out.println("\nPath: " + navStack.getPath());

        List<Admission> admissions = admissionDAO.getAdmissionsByPatient(loggedInPatient.getPatientID());

        if (admissions.isEmpty()) {
            System.out.println("No admission records found.");
        } else {
            printAdmissionBillTable(admissions);
        }

        navStack.pop();
    }

    private void viewMyHistory() {
        navStack.push("ViewMyHistory");
        System.out.println("\nPath: " + navStack.getPath());

        String history = fileManager.readPatientHistory(loggedInPatient.getPatientID());
        System.out.println(history);

        navStack.pop();
    }

    private void changePassword() {
        navStack.push("ChangePassword");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Enter Current Password: ");
        String currentPassword = sc.nextLine();

        if (!loggedInPatient.getPassword().equals(PasswordUtil.hashPassword(currentPassword))) {
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

        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        boolean success = patientDAO.updatePassword(loggedInPatient.getPatientID(), hashedNewPassword);
        if (success) {
            loggedInPatient.setPassword(hashedNewPassword);
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }

        navStack.pop();
    }


    private void printPatientDetails(Patient p, int age) {
        System.out.println("-".repeat(60));
        System.out.printf("%-15s: %s%n", "Patient ID", p.getPatientID());
        System.out.printf("%-15s: %s%n", "Name", p.getName());
        System.out.printf("%-15s: %s%n", "DOB", p.getDob());
        if (age >= 0) {
            System.out.printf("%-15s: %d years%n", "Age", age);
        }
        System.out.printf("%-15s: %s%n", "Gender", p.getGender());
        System.out.printf("%-15s: %s%n", "Blood Group", p.getBloodGroup());
        System.out.printf("%-15s: %s%n", "Phone", p.getPhoneNo());
        System.out.printf("%-15s: %s%n", "Email", p.getEmail());
        System.out.printf("%-15s: %s, %s, %s - %s%n", "Address",
                p.getStreet(), p.getCity(), p.getState(), p.getPincode());
        System.out.println("-".repeat(60));
    }

    private void printAdmissionBillTable(List<Admission> admissions) {
        System.out.println("-".repeat(112));
        System.out.printf("%-6s %-20s %-10s %-13s %-11s %-14s %-14s %-12s%n",
                "No.", "Doctor", "Room No.", "Room Type", "Status", "Admitted", "Discharged", "Bill Total");
        System.out.println("-".repeat(112));

        int srNo = 1;
        for (Admission ad : admissions) {
            Doctor d = doctorDAO.getDoctorById(ad.getDoctorID());
            String doctorName = (d != null) ? d.getName() : "Unknown Doctor";

            String dischargeDisplay = (ad.getDischargeDate() == null || ad.getDischargeDate().isBlank())
                    ? "-" : ad.getDischargeDate();

            String billDisplay = "-";
            if (ad.getStatus().equalsIgnoreCase("DISCHARGED")) {
                Bill b = billDAO.getBillByAdmissionId(ad.getAdmissionID());
                if (b != null) {
                    billDisplay = "Rs." + String.format("%.2f", b.getTotalAmount());
                }
            }

            System.out.printf("%-6d %-20s %-10s %-13s %-11s %-14s %-14s %-12s%n",
                    srNo++,
                    doctorName,
                    ad.getRoomNumber(),
                    ad.getRoomType(),
                    ad.getStatus(),
                    ad.getAdmissionDate(),
                    dischargeDisplay,
                    billDisplay);
        }

        System.out.println("-".repeat(112));
    }
}