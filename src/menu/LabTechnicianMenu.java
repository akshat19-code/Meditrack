package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;

import java.util.Scanner;

public class LabTechnicianMenu {

    private Scanner sc;
    private MenuStack navStack;
    private LabTechnician loggedInLabTech;

    private QueueService queueService = new QueueService();
    private ReportAnalyser reportAnalyser = new ReportAnalyser();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();

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
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

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

        int testRequestId = queueService.processNextRequest();
        if (testRequestId != -1) {
            System.out.println("Now processing Test Request ID: " + testRequestId +
                    " (remember this ID to upload its result next)");
        }

        navStack.pop();
    }

    private void uploadResult() {
        navStack.push("UploadResult");
        System.out.println("\nPath: " + navStack.getPath());

        System.out.print("Enter Test Request ID (that you are currently processing): ");
        int testRequestId = sc.nextInt();
        System.out.print("Enter Result Value: ");
        double resultValue = sc.nextDouble();

        String analysisDate = java.time.LocalDate.now().toString();

        boolean success = reportAnalyser.generateReport(
                testRequestId, resultValue, analysisDate, loggedInLabTech.getLabTechID());

        System.out.println(success ? "Report generated successfully!" : "Failed to generate report.");

        navStack.pop();
    }
}