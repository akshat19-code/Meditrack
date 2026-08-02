package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.*;
import java.util.*;

public class LabTechnicianMenu {

    private Scanner sc;
    private MenuStack navStack;
    private LabTechnician loggedInLabTech;

    private QueueService queueService = new QueueService();
    private ReportAnalyser reportAnalyser = new ReportAnalyser();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private LabTechnicianDAO labTechDAO = new LabTechnicianDAO();

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
            System.out.println("4. Change Password");
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> viewQueue();
                case 2 -> processNext();
                case 3 -> uploadResult();
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

    private void viewQueue() {
        navStack.push("ViewQueue");
        System.out.println("\nPath: " + navStack.getPath());

        queueService.viewQueue();

        navStack.pop();
    }

    private void processNext() {
        navStack.push("ProcessNext");
        System.out.println("\nPath: " + navStack.getPath());

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

    private void changePassword() {
        navStack.push("ChangePassword");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Enter Current Password: ");
        String currentPassword = sc.nextLine();

        if (!loggedInLabTech.getPassword().equals(currentPassword)) {
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

        boolean success = labTechDAO.updatePassword(loggedInLabTech.getLabTechID(), newPassword);
        if (success) {
            loggedInLabTech.setPassword(newPassword);
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }

        navStack.pop();
    }
}