package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;

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
    private HealthScoreService healthScoreService = new HealthScoreService();

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
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> viewMyDetails();
                case 2 -> viewReportHistory();
                case 3 -> viewHealthScore();
                case 4 -> viewAdmissionAndBill();
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
        // We use this List directly - no need to copy it into another collection.
        List<Report> reports = reportDAO.getReportsByPatient(loggedInPatient.getPatientID());

        if (reports.isEmpty()) {
            System.out.println("No report history available yet.");
        } else {
            // Walk the list backwards to print newest-first, matching the
            // .txt file order, without needing any extra data structure.
            System.out.println("---- Report History (Newest to Oldest) ----");
            for (int i = reports.size() - 1; i >= 0; i--) {
                Report r = reports.get(i);
                String testName = getTestNameForReport(r);

                System.out.println("Report ID: " + r.getReportID() +
                        " | Test: " + testName +
                        " | Result: " + r.getResultValue() +
                        " | Status: " + r.getResultStatus() +
                        " | Date: " + r.getAnalysisDate());
            }
            System.out.println("--------------------------------------------");
        }

        navStack.pop();
    }

    private void viewHealthScore() {
        navStack.push("ViewHealthScore");
        System.out.println("\nPath: " + navStack.getPath());

        double score = healthScoreService.calculateHealthScore(loggedInPatient.getPatientID());
        System.out.println("Your Health Score: " + score + " / 100");

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
                System.out.println(ad);
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
}