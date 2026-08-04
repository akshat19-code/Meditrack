package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.*;

import java.util.*;

public class DoctorMenu {

    private Scanner sc;
    private MenuStack navStack;
    private Doctor loggedInDoctor;

    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();
    private ReportDAO reportDAO = new ReportDAO();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private QueueService queueService = new QueueService();
    private FileManager fileManager = new FileManager();

    public DoctorMenu(Scanner sc, MenuStack navStack, Doctor d) {
        this.sc = sc;
        this.navStack = navStack;
        this.loggedInDoctor = d;
    }

    public void show() {
        navStack.push("DoctorMenu");
        boolean flag = true;

        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("===== Doctor Menu =====");
            System.out.println("1. View Assigned Patients");
            System.out.println("2. Request Lab Test");
            System.out.println("3. Review Report / Add Diagnosis Notes");
            System.out.println("4. Change Password");
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> viewAssignedPatients();
                case 2 -> requestTest();
                case 3 -> reviewReport();
                case 4 -> changePassword();
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

    private void viewAssignedPatients() {
        navStack.push("ViewAssignedPatients");
        System.out.println("\nPath: " + navStack.getPath());

        List<Admission> admissions = admissionDAO.getActiveAdmissionsByDoctor(loggedInDoctor.getDoctorID());

        if (admissions.isEmpty()) {
            System.out.println("No currently assigned patients.");
        } else {
            printAssignedPatientsTable(admissions);
        }

        navStack.pop();
    }

    private void requestTest() {
        navStack.push("RequestTest");
        System.out.println("\nPath: " + navStack.getPath());

        int admissionId = InputValidator.readInt(sc, "Enter Admission ID: ");

        Admission ad = admissionDAO.getAdmissionById(admissionId);
        if (ad == null) {
            System.out.println("Admission not found.");
            navStack.pop();
            return;
        }

        if (ad.getHospitalID() != loggedInDoctor.getHospitalID()) {
            System.out.println("This admission does not belong to your hospital.");
            navStack.pop();
            return;
        }

        if (ad.getDoctorID() != loggedInDoctor.getDoctorID()) {
            System.out.println("This patient is not assigned to you.");
            navStack.pop();
            return;
        }

        System.out.println("Available Test Types:");
        List<TestType> testTypes = testTypeDAO.getAllTestTypesByHospital(loggedInDoctor.getHospitalID());
        printTestTypeTable(testTypes);

        int testTypeId = InputValidator.readInt(sc, "Enter Test Type ID: ");
        sc.nextLine();

        String priority = InputValidator.readMenuChoice(sc, "Priority:",
                new String[]{"NORMAL", "EMERGENCY"},
                new String[]{"NORMAL", "EMERGENCY"});
        sc.nextLine();

        String equipmentUsageDate = InputValidator.readDate(sc, "Equipment Usage Date (yyyy-mm-dd): ", false, true);

        TestType selectedTest = testTypeDAO.getTestTypeById(testTypeId);
        if (selectedTest == null) {
            System.out.println("Invalid Test Type ID.");
            navStack.pop();
            return;
        }

        Patient p = patientDAO.getPatientById(ad.getPatientID());

        TestRequest tr = new TestRequest();
        tr.setRequestDate(java.time.LocalDate.now().toString());
        tr.setEquipmentUsageDate(equipmentUsageDate);
        tr.setPriority(priority);
        tr.setStatus("PENDING");
        tr.setAdmissionID(admissionId);
        tr.setDoctorID(loggedInDoctor.getDoctorID());
        tr.setTestTypeID(testTypeId);
        tr.setEquipmentID(selectedTest.getEquipmentID());

        boolean success = queueService.requestTest(tr,
                p != null ? p.getName() : "Unknown Patient",
                selectedTest.getTestName());

        System.out.println(success ? "Test request created successfully!" : "Failed to create test request.");

        navStack.pop();
    }

    private void reviewReport() {
        navStack.push("ReviewReport");
        System.out.println("\nPath: " + navStack.getPath());

        List<Report> reports = reportDAO.getReportsByDoctor(loggedInDoctor.getDoctorID());
        if (reports.isEmpty()) {
            System.out.println("No reports available for your patients yet.");
            navStack.pop();
            return;
        }
        printReportSummaryTable(reports);

        int reportId = InputValidator.readInt(sc, "Enter Report ID: ");
        sc.nextLine();

        Report r = reportDAO.getReportById(reportId);
        if (r == null) {
            System.out.println("Report not found.");
            navStack.pop();
            return;
        }

        TestRequest tr = testRequestDAO.getTestRequestById(r.getTestRequestID());
        Admission ad = (tr != null) ? admissionDAO.getAdmissionById(tr.getAdmissionID()) : null;

        if (ad == null) {
            System.out.println("Could not verify this report's admission details.");
            navStack.pop();
            return;
        }

        if (ad.getHospitalID() != loggedInDoctor.getHospitalID() || ad.getDoctorID() != loggedInDoctor.getDoctorID()) {
            System.out.println("This report does not belong to one of your patients.");
            navStack.pop();
            return;
        }

        System.out.println(r);
        System.out.print("Enter Diagnosis Notes: ");
        String notes = sc.nextLine();

        boolean success = reportDAO.updateDoctorNotes(reportId, notes);

        if (success) {
            fileManager.appendDiagnosisToReportFile(reportId, notes);
            fileManager.addDiagnosisHistoryEntry(ad.getPatientID(), reportId, loggedInDoctor.getName());
            System.out.println("Notes added successfully!");
        } else {
            System.out.println("Failed to add notes.");
        }

        navStack.pop();
    }

    // Same pattern as Master Admin / Hospital Admin's Change Password.
    private void changePassword() {
        navStack.push("ChangePassword");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Enter Current Password: ");
        String currentPassword = sc.nextLine();

        if (!loggedInDoctor.getPassword().equals(currentPassword)) {
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

        boolean success = doctorDAO.updatePassword(loggedInDoctor.getDoctorID(), newPassword);
        if (success) {
            loggedInDoctor.setPassword(newPassword);
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }

        navStack.pop();
    }

    // ==================== Private Table / Display Helpers ====================

    private void printAssignedPatientsTable(List<Admission> admissions) {
        System.out.println("-".repeat(90));
        System.out.printf("%-14s %-25s %-12s %-15s %-12s%n",
                "Admission ID", "Patient Name", "Room No.", "Room Type", "Status");
        System.out.println("-".repeat(90));

        for (Admission ad : admissions) {
            Patient p = patientDAO.getPatientById(ad.getPatientID());
            String patientName = (p != null) ? p.getName() : "Unknown";

            System.out.printf("%-14d %-25s %-12s %-15s %-12s%n",
                    ad.getAdmissionID(),
                    patientName,
                    ad.getRoomNumber(),
                    ad.getRoomType(),
                    ad.getStatus());
        }

        System.out.println("-".repeat(90));
    }

    private void printTestTypeTable(List<TestType> testTypes) {
        System.out.println("-".repeat(90));
        System.out.printf("%-5s %-30s %-20s %-10s%n",
                "ID", "Test Name", "Normal Range", "Charge");
        System.out.println("-".repeat(90));

        for (TestType tt : testTypes) {
            String range = String.format("%.2f - %.2f %s", tt.getNormalMin(), tt.getNormalMax(), tt.getUnit());
            System.out.printf("%-5d %-30s %-20s Rs.%-8.2f%n",
                    tt.getTestTypeID(),
                    tt.getTestName(),
                    range,
                    tt.getTestCharge());
        }

        System.out.println("-".repeat(90));
    }

    private void printReportSummaryTable(List<Report> reports) {
        System.out.println("-".repeat(100));
        System.out.printf("%-6s %-22s %-22s %-12s %-12s%n",
                "RepID", "Patient", "Test", "Status", "Date");
        System.out.println("-".repeat(100));

        for (Report r : reports) {
            String patientName = "Unknown";
            String testName = "Unknown Test";

            TestRequest tr = testRequestDAO.getTestRequestById(r.getTestRequestID());
            if (tr != null) {
                Admission ad = admissionDAO.getAdmissionById(tr.getAdmissionID());
                if (ad != null) {
                    Patient p = patientDAO.getPatientById(ad.getPatientID());
                    if (p != null) {
                        patientName = p.getName();
                    }
                }
                TestType tt = testTypeDAO.getTestTypeById(tr.getTestTypeID());
                if (tt != null) {
                    testName = tt.getTestName();
                }
            }

            System.out.printf("%-6d %-22s %-22s %-12s %-12s%n",
                    r.getReportID(),
                    patientName,
                    testName,
                    r.getResultStatus(),
                    r.getAnalysisDate());
        }

        System.out.println("-".repeat(100));
    }
}