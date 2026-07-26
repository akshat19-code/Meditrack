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

        System.out.println(loggedInPatient);

        int age = patientDAO.calculateAge(loggedInPatient.getDob());
        if (age >= 0) {
            System.out.println("Age: " + age + " years");
        }

        navStack.pop();
    }

    // Helper - walks Report -> TestRequest -> TestType to find the real test
    // name for a report, since Report itself only stores TestRequestID.
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

        // ReportDAO already returns these ordered oldest-to-newest (AnalysisDate ASC).
        List<Report> reports = reportDAO.getReportsByPatient(loggedInPatient.getPatientID());

        if (reports.isEmpty()) {
            System.out.println("No report history available yet.");
        } else {
            // Custom PatientHistoryList - load reports oldest-to-newest via
            // addLast() (matching ReportDAO's existing order), then use the
            // list's own displayFromLast() to print newest-first.
            PatientHistoryList historyList = new PatientHistoryList();
            for (Report r : reports) {
                String testName = getTestNameForReport(r);
                historyList.addLast(r.getReportID(), testName, r.getResultValue(),
                        r.getResultStatus(), r.getAnalysisDate());
            }
            historyList.displayFromLast();
        }

        // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
        // Built-in equivalent - walk the List backwards to print newest-first,
        // matching the .txt file order, without needing any extra data structure.
        // System.out.println("---- Report History (Newest to Oldest) ----");
        // for (int i = reports.size() - 1; i >= 0; i--) {
        //     Report r = reports.get(i);
        //     String testName = getTestNameForReport(r);
        //     System.out.println("Report ID: " + r.getReportID() +
        //             " | Test: " + testName +
        //             " | Result: " + String.format("%.2f", r.getResultValue()) +
        //             " | Status: " + r.getResultStatus() +
        //             " | Date: " + r.getAnalysisDate());
        // }
        // System.out.println("--------------------------------------------");

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
            for (Admission ad : admissions) {
                Doctor d = doctorDAO.getDoctorById(ad.getDoctorID());
                String doctorName = (d != null) ? d.getName() : "Unknown Doctor";

                System.out.println(ad);
                System.out.println("Attending Doctor: " + doctorName);

                if (ad.getStatus().equalsIgnoreCase("DISCHARGED")) {
                    Bill b = billDAO.getBillByAdmissionId(ad.getAdmissionID());
                    if (b != null) {
                        System.out.println(b);
                    }
                }
            }
        }

        navStack.pop();
    }

    // Shows the patient's full lifecycle timeline - admissions, reports,
    // diagnoses, discharges - already written correctly to Patient_<ID>.txt
    // at every step; this just exposes that existing file to the patient.
    private void viewMyHistory() {
        navStack.push("ViewMyHistory");
        System.out.println("\nPath: " + navStack.getPath());

        String history = fileManager.readPatientHistory(loggedInPatient.getPatientID());
        System.out.println(history);

        navStack.pop();
    }

    // Same pattern as the other four roles' Change Password.
    private void changePassword() {
        navStack.push("ChangePassword");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Enter Current Password: ");
        String currentPassword = sc.nextLine();

        if (!loggedInPatient.getPassword().equals(currentPassword)) {
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

        boolean success = patientDAO.updatePassword(loggedInPatient.getPatientID(), newPassword);
        if (success) {
            loggedInPatient.setPassword(newPassword);
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }

        navStack.pop();
    }
}