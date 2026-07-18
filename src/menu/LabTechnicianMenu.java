package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.InputValidator;

import java.util.Scanner;

public class LabTechnicianMenu {

    private Scanner sc;
    private MenuStack navStack;
    private LabTechnician loggedInLabTech;

    private QueueService queueService = new QueueService();
    private ReportAnalyser reportAnalyser = new ReportAnalyser();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();

    public LabTechnicianMenu(Scanner sc, MenuStack navStack, LabTechnician lt) {
        this.sc = sc;
        this.navStack = navStack;
        this.loggedInLabTech = lt;
    }

    public void show() {
        navStack.push("LabTechnicianMenu");
        boolean flag = true;

        // Restore the in-memory queue from the DB, in case the program was restarted
        queueService.loadPendingRequests(loggedInLabTech.getHospitalID());

        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("===== Lab Technician Menu =====");
            System.out.println("1. View Pending Test Requests");
            System.out.println("2. Process Next Request");
            System.out.println("3. Upload Result");
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> viewQueue();
                case 2 -> processNext();
                case 3 -> uploadResult();
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

    private void viewQueue() {
        navStack.push("ViewQueue");
        System.out.println("\nPath: " + navStack.getPath());

        queueService.viewQueue();

        navStack.pop();
    }

    private void processNext() {
        navStack.push("ProcessNext");
        System.out.println("\nPath: " + navStack.getPath());

        // Uses processNextRequestWithDetails() instead of processNextRequest(),
        // so the Lab Technician sees the patient name and test name right away
        // instead of just a bare ID they'd have to look up separately.
        String[] details = queueService.processNextRequestWithDetails();

        if (details != null) {
            System.out.println("Now processing:");
            System.out.println("Request ID: " + details[0] +
                    " | Patient: " + details[1] +
                    " | Test: " + details[2] +
                    " | Priority: " + details[3]);
            System.out.println("(remember this Request ID to upload its result next)");
        }

        navStack.pop();
    }

    private void uploadResult() {
        navStack.push("UploadResult");
        System.out.println("\nPath: " + navStack.getPath());

        int testRequestId = InputValidator.readInt(sc, "Enter Test Request ID (that you are currently processing): ");

        TestRequest tr = testRequestDAO.getTestRequestById(testRequestId);
        if (tr == null) {
            System.out.println("Test request not found.");
            navStack.pop();
            return;
        }

        // Tenant isolation - this TestRequest's Admission must belong to the
        // Lab Technician's own hospital, otherwise any Lab Tech could upload
        // results for a test request from a completely different hospital.
        Admission ad = admissionDAO.getAdmissionById(tr.getAdmissionID());
        if (ad == null || ad.getHospitalID() != loggedInLabTech.getHospitalID()) {
            System.out.println("This test request does not belong to your hospital.");
            navStack.pop();
            return;
        }

        double resultValue = InputValidator.readDouble(sc, "Enter Result Value: ");

        String analysisDate = java.time.LocalDate.now().toString();

        boolean success = reportAnalyser.generateReport(
                testRequestId, resultValue, analysisDate, loggedInLabTech.getLabTechID());

        System.out.println(success ? "Report generated successfully!" : "Failed to generate report.");

        navStack.pop();
    }
}