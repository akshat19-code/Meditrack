package service;

import dao.*;
import ds.TestRequestQueue;
import model.*;

import java.util.*;

public class QueueService {

    // Single custom queue - enqueue() itself decides front (EMERGENCY) vs
    // rear (NORMAL) placement, and deleteFromFront()/deleteFromFrontWithDetails()
    // always serve the correct next request.
    private TestRequestQueue testRequestQueue = new TestRequestQueue();

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // Built-in equivalent using the Java Collections Framework - two separate
    // FIFO Queue<QueueEntry> objects (LinkedList-backed). emergencyQueue is
    // always drained first, so EMERGENCY requests are always processed before
    // NORMAL ones, while still preserving arrival order inside each priority
    // level. To use this instead of the custom TestRequestQueue above:
    //   1. Comment out the testRequestQueue field above, uncomment these two.
    //   2. Replace addToQueue()/processNextRequest()/processNextRequestWithDetails()/
    //      viewQueue()/isQueueEmpty() bodies with the commented versions below each method.
    // private static class QueueEntry {
    //     int testRequestId;
    //     String patientName;
    //     String testName;
    //     String priority;
    //
    //     QueueEntry(int testRequestId, String patientName, String testName, String priority) {
    //         this.testRequestId = testRequestId;
    //         this.patientName = patientName;
    //         this.testName = testName;
    //         this.priority = priority;
    //     }
    // }
    // private Queue<QueueEntry> emergencyQueue = new LinkedList<>();
    // private Queue<QueueEntry> normalQueue = new LinkedList<>();

    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();

    // Helper - looks up the real Patient name and Test name for a TestRequest,
    // since the DB row only stores IDs, but the Queue needs readable names for display
    private String getPatientName(TestRequest tr) {
        Admission ad = admissionDAO.getAdmissionById(tr.getAdmissionID());
        if (ad == null) return "Unknown Patient";

        Patient p = patientDAO.getPatientById(ad.getPatientID());
        return (p != null) ? p.getName() : "Unknown Patient";
    }

    private String getTestName(TestRequest tr) {
        TestType tt = testTypeDAO.getTestTypeById(tr.getTestTypeID());
        return (tt != null) ? tt.getTestName() : "Unknown Test";
    }

    // Adds one entry into the queue - enqueue() itself checks priority and
    // decides front (EMERGENCY) vs rear (NORMAL) placement.
    private void addToQueue(int testRequestId, String patientName, String testName, String priority) {
        testRequestQueue.enqueue(testRequestId, patientName, testName, priority);
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // private void addToQueue(int testRequestId, String patientName, String testName, String priority) {
    //     QueueEntry entry = new QueueEntry(testRequestId, patientName, testName, priority);
    //     if (priority.equalsIgnoreCase("EMERGENCY")) {
    //         emergencyQueue.add(entry);
    //     } else {
    //         normalQueue.add(entry);
    //     }
    // }

    // Called once when the program starts, to rebuild the in-memory queue
    // from whatever PENDING requests already exist in the database
    public void loadPendingRequests(int hospitalId) {
        List<TestRequest> pending = testRequestDAO.getPendingTestRequests(hospitalId);
        for (TestRequest tr : pending) {
            String patientName = getPatientName(tr);
            String testName = getTestName(tr);
            addToQueue(tr.getTestRequestID(), patientName, testName, tr.getPriority());
        }
        System.out.println("Queue restored with " + pending.size() + " pending request(s).");
    }

    // Called when a Doctor requests a test - inserts into DB first (to get an ID),
    // then adds into the queue using the same ID and real names
    public boolean requestTest(TestRequest tr, String patientName, String testName) {
        int newId = testRequestDAO.insertTestRequest(tr);
        if (newId == -1) {
            System.out.println("Failed to create test request.");
            return false;
        }
        addToQueue(newId, patientName, testName, tr.getPriority());
        return true;
    }

    // Called when Lab Technician picks up the next request to process.
    // deleteFromFront() always returns the correct next request - EMERGENCY
    // entries were inserted at the front, so they naturally come out first.
    // Equipment.Status is now updated automatically by the UpdateEquipmentStatus
    // trigger when TestRequest.Status changes to PROCESSING - no manual update needed.
    public int processNextRequest() {
        int testRequestId = testRequestQueue.deleteFromFront();

        if (testRequestId == -1) {
            return -1;
        }

        testRequestDAO.updateTestRequestStatus(testRequestId, "PROCESSING");

        return testRequestId;
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // public int processNextRequest() {
    //     QueueEntry entry;
    //     if (!emergencyQueue.isEmpty()) {
    //         entry = emergencyQueue.poll();
    //     } else if (!normalQueue.isEmpty()) {
    //         entry = normalQueue.poll();
    //     } else {
    //         System.out.println("No pending test requests.");
    //         return -1;
    //     }
    //     int testRequestId = entry.testRequestId;
    //     testRequestDAO.updateTestRequestStatus(testRequestId, "PROCESSING");
    //     return testRequestId;
    // }

    // Same as processNextRequest(), but also returns the patient name and test
    // name, instead of throwing them away and forcing the Lab Technician to
    // look the ID up separately. deleteFromFrontWithDetails() already returns
    // these in the same array layout this method needs, so this is a thin
    // wrapper that just also updates the Status in the database.
    // Array layout: [0] = TestRequestID, [1] = PatientName, [2] = TestName, [3] = Priority.
    // Returns null if the queue is empty.
    public String[] processNextRequestWithDetails() {
        String[] details = testRequestQueue.deleteFromFrontWithDetails();

        if (details == null) {
            return null;
        }

        testRequestDAO.updateTestRequestStatus(Integer.parseInt(details[0]), "PROCESSING");

        return details;
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // public String[] processNextRequestWithDetails() {
    //     QueueEntry entry;
    //     if (!emergencyQueue.isEmpty()) {
    //         entry = emergencyQueue.poll();
    //     } else if (!normalQueue.isEmpty()) {
    //         entry = normalQueue.poll();
    //     } else {
    //         System.out.println("No pending test requests.");
    //         return null;
    //     }
    //     testRequestDAO.updateTestRequestStatus(entry.testRequestId, "PROCESSING");
    //     return new String[] {
    //             String.valueOf(entry.testRequestId),
    //             entry.patientName,
    //             entry.testName,
    //             entry.priority
    //     };
    // }

    // View the queue without removing anything - useful for a "View Pending Requests"
    // menu option. display() already prints front-to-rear, which is exactly the
    // order requests would actually be processed in (EMERGENCY entries sit at
    // the front since enqueue() places them there).
    public void viewQueue() {
        testRequestQueue.display();
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // public void viewQueue() {
    //     if (emergencyQueue.isEmpty() && normalQueue.isEmpty()) {
    //         System.out.println("Queue is empty.");
    //         return;
    //     }
    //     System.out.println("---- Current Test Request Queue ----");
    //     for (QueueEntry entry : emergencyQueue) {
    //         System.out.println("Request ID: " + entry.testRequestId +
    //                 " | Patient: " + entry.patientName +
    //                 " | Test: " + entry.testName +
    //                 " | Priority: " + entry.priority);
    //     }
    //     for (QueueEntry entry : normalQueue) {
    //         System.out.println("Request ID: " + entry.testRequestId +
    //                 " | Patient: " + entry.patientName +
    //                 " | Test: " + entry.testName +
    //                 " | Priority: " + entry.priority);
    //     }
    //     System.out.println("-------------------------------------");
    // }

    public boolean isQueueEmpty() {
        return testRequestQueue.isEmpty();
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // public boolean isQueueEmpty() {
    //     return emergencyQueue.isEmpty() && normalQueue.isEmpty();
    // }
}