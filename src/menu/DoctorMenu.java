package menu;

import dao.*;
import ds.MenuStack;
import model.*;
import service.QueueService;
import service.FileManager;

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
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

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
        List<Admission> admissions = admissionDAO.getActiveAdmissionsByDoctor(loggedInDoctor.getDoctorID());
        if (admissions.isEmpty()) {
            System.out.println("No currently assigned patients.");
        } else {
            for (Admission ad : admissions) {
                Patient p = patientDAO.getPatientById(ad.getPatientID());
                System.out.println("Admission ID: " + ad.getAdmissionID() +
                        " | Patient: " + (p != null ? p.getName() : "Unknown") +
                        " | Room: " + ad.getRoomNumber() +
                        " | Status: " + ad.getStatus());
            }
        }

        navStack.pop();
    }

    private void requestTest() {
        navStack.push("RequestTest");
        System.out.println("\nPath: " + navStack.getPath());

        System.out.print("Enter Admission ID: ");
        int admissionId = sc.nextInt();

        Admission ad = admissionDAO.getAdmissionById(admissionId);
        if (ad == null) {
            System.out.println("Admission not found.");
            navStack.pop();
            return;
        }

        System.out.println("Available Test Types:");
        List<TestType> testTypes = testTypeDAO.getAllTestTypesByHospital(loggedInDoctor.getHospitalID());
        for (TestType tt : testTypes) {
            System.out.println(tt);
        }

        System.out.print("Enter Test Type ID: ");
        int testTypeId = sc.nextInt();
        sc.nextLine();

        System.out.print("Priority (NORMAL/EMERGENCY): ");
        String priority = sc.nextLine();

        System.out.print("Equipment Usage Date (yyyy-mm-dd): ");
        String equipmentUsageDate = sc.nextLine();

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
        tr.setPriority(priority.toUpperCase());
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

        System.out.print("Enter Report ID: ");
        int reportId = sc.nextInt();
        sc.nextLine();

        Report r = reportDAO.getReportById(reportId);
        if (r == null) {
            System.out.println("Report not found.");
            navStack.pop();
            return;
        }

        System.out.println(r);
        System.out.print("Enter Diagnosis Notes: ");
        String notes = sc.nextLine();

        boolean success = reportDAO.updateDoctorNotes(reportId, notes);

        if (success) {

            TestRequest tr = testRequestDAO.getTestRequestById(r.getTestRequestID());

            if (tr != null) {

                Admission adForNotes = admissionDAO.getAdmissionById(tr.getAdmissionID());

                if (adForNotes != null) {

                    fileManager.appendDiagnosisToReportFile(reportId, notes);

                    fileManager.addDiagnosisHistoryEntry(
                            adForNotes.getPatientID(),
                            reportId,
                            notes
                    );
                }
            }

            System.out.println("Notes added successfully!");

        } else {

            System.out.println("Failed to add notes.");

        }

        if (success) {
            TestRequest tr = testRequestDAO.getTestRequestById(r.getTestRequestID());
            Admission ad = (tr != null) ? admissionDAO.getAdmissionById(tr.getAdmissionID()) : null;
            if (ad != null) {
                fileManager.appendDiagnosisToReportFile(reportId, notes);
                fileManager.addDiagnosisHistoryEntry(ad.getPatientID(), reportId, loggedInDoctor.getName());
            }
        }

        navStack.pop();
    }
}