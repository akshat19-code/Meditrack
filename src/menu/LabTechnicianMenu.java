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
    private PatientDAO patientDAO = new PatientDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();

    public LabTechnicianMenu(Scanner sc, MenuStack navStack, LabTechnician lt) {
        this.sc = sc;
        this.navStack = navStack;
        this.loggedInLabTech = lt;
    }

    public void show() {
        navStack.push("LabTechnicianMenu");
        boolean flag = true;

        queueService.loadPendingRequests(loggedInLabTech.getHospitalID());

        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("===== Lab Technician Menu =====");
            System.out.println("1. View Pending Test Requests");
            System.out.println("2. Process Next Request");
            System.out.println("3. Upload Result");
            System.out.println("4. Change Password");
            System.out.println("0. Log Off");
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
            printRequestDetails(details);
            System.out.println("You can upload the result from 'Upload Result'.");

            try {
                System.out.println("\nCollecting Sample...");
                Thread.sleep(700);

                System.out.println("Processing Test...");
                for (int i = 0; i < 6; i++) {
                    Thread.sleep(400);
                    System.out.print(".");
                }
                System.out.println();

                System.out.println("Generating Report...");
                Thread.sleep(700);

                System.out.println("✔ Report Generated Successfully.\n");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        navStack.pop();
    }

    private void uploadResult() {
        navStack.push("UploadResult");
        System.out.println("\nPath: " + navStack.getPath());

        List<TestRequest> processingRequests =
                testRequestDAO.getRequestsByStatusForHospital(loggedInLabTech.getHospitalID(), "PROCESSING");

        if (processingRequests.isEmpty()) {
            System.out.println("No test requests are currently being processed.");
            System.out.println("(Use 'Process Next Request' first to move a request into PROCESSING.)");
            navStack.pop();
            return;
        }

        printProcessingRequestsTable(processingRequests);

        TestRequest tr;

        while (true) {
            int choice = InputValidator.readInt(sc, "Select Test Request (0 to cancel): ");

            if (choice == 0) {
                navStack.pop();
                return;
            }

            if (choice < 1 || choice > processingRequests.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            TestRequest selected = processingRequests.get(choice - 1);
            tr = selected;
            break;
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
                tr.getTestRequestID(), resultValue, analysisDate, loggedInLabTech.getLabTechID());

        System.out.println(success ? "Report generated successfully!" : "Failed to generate report.");

        navStack.pop();
    }

    private void changePassword() {
        navStack.push("ChangePassword");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Enter Current Password: ");
        String currentPassword = sc.nextLine();

        if (!loggedInLabTech.getPassword().equals(PasswordUtil.hashPassword(currentPassword))) {
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
        boolean success = labTechDAO.updatePassword(loggedInLabTech.getLabTechID(), hashedNewPassword);
        if (success) {
            loggedInLabTech.setPassword(hashedNewPassword);
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }

        navStack.pop();
    }

    private void printRequestDetails(String[] details) {
        System.out.println("-".repeat(70));
        System.out.printf("%-5s %-20s %-20s %-10s%n",
                "No.", "Patient", "Test", "Priority");
        System.out.println("-".repeat(70));
        System.out.printf("%-5d %-20s %-20s %-10s%n",
                1, details[1], details[2], details[3]);
        System.out.println("-".repeat(70));
    }

    private void printProcessingRequestsTable(List<TestRequest> requests) {
        System.out.println("-".repeat(95));
        System.out.printf("%-6s %-22s %-22s %-10s %-15s%n",
                "No.", "Patient", "Test", "Priority", "Usage Date");
        System.out.println("-".repeat(95));

        int srNo = 1;
        for (TestRequest tr : requests) {
            String patientName = "Unknown";
            Admission ad = admissionDAO.getAdmissionById(tr.getAdmissionID());
            if (ad != null) {
                Patient p = patientDAO.getPatientById(ad.getPatientID());
                if (p != null) {
                    patientName = p.getName();
                }
            }

            TestType tt = testTypeDAO.getTestTypeById(tr.getTestTypeID());
            String testName = (tt != null) ? tt.getTestName() : "Unknown Test";

            System.out.printf("%-6d %-22s %-22s %-10s %-15s%n",
                    srNo++,
                    patientName,
                    testName,
                    tr.getPriority(),
                    tr.getEquipmentUsageDate());
        }

        System.out.println("-".repeat(95));
    }
}