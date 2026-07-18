package menu;

import dao.*;
import ds.MenuStack;
import model.*;
import service.QueueService;
import service.FileManager;
import util.InputValidator;

import java.util.List;
import java.util.Scanner;

public class DoctorMenu {

    private Scanner sc;
    private MenuStack navStack;
    private Doctor loggedInDoctor;

    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();
    private ReportDAO reportDAO = new ReportDAO();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
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
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> viewAssignedPatients();
                case 2 -> requestTest();
                case 3 -> reviewReport();
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
        System.out.println("Logged In Doctor ID = " + loggedInDoctor.getDoctorID());

        List<String> summaries = admissionDAO.getActivePatientSummariesByDoctor(loggedInDoctor.getDoctorID());

        if (summaries.isEmpty()) {
            System.out.println("No currently assigned patients.");
        } else {
            for (String line : summaries) {
                System.out.println(line);
            }
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
        for (TestType tt : testTypes) {
            System.out.println(tt);
        }

        int testTypeId = InputValidator.readInt(sc, "Enter Test Type ID: ");
        sc.nextLine();

        String priority = InputValidator.readMenuChoice(sc, "Priority:",
                new String[]{"NORMAL", "EMERGENCY"},
                new String[]{"NORMAL", "EMERGENCY"});
        sc.nextLine(); // clear leftover newline from readMenuChoice's internal nextInt()

        String equipmentUsageDate = InputValidator.readDate(sc, "Equipment Usage Date (yyyy-mm-dd): ", false);

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
}