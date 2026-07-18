package service;

import dao.*;
import ds.TestRequestQueue;
import model.*;

import java.util.*;

public class QueueService {

    // Small helper class - just holds the display fields for one request
    // waiting in the queue. No Comparable/Comparator logic here at all;
    // ordering is handled entirely by WHICH queue the entry sits in
    // (see below), not by sorting.
    private static class QueueEntry {
        int testRequestId;
        String patientName;
        String testName;
        String priority;

        QueueEntry(int testRequestId, String patientName, String testName, String priority) {
            this.testRequestId = testRequestId;
            this.patientName = patientName;
            this.testName = testName;
            this.priority = priority;
        }
    }

    // Two separate FIFO queues from the Java Collections Framework.
    // LinkedList implements the Queue interface, so add() joins the back
    // and poll() removes from the front - this alone guarantees FIFO
    // order within each queue, with no extra code needed.
    //
    // emergencyQueue is always drained first, so EMERGENCY requests are
    // always processed before NORMAL ones, while still preserving arrival
    // order inside each priority level.
    private Queue<QueueEntry> emergencyQueue = new LinkedList<>();
    private Queue<QueueEntry> normalQueue = new LinkedList<>();

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // Custom TestRequestQueue equivalent - a single queue where enqueue()
    // itself decides front (EMERGENCY) vs rear (NORMAL) placement, and
    // deleteFromFront() always serves the correct next request.
    // To use this instead of the two Queue<QueueEntry> above:
    //   1. Uncomment the line below.
    //   2. Replace addToQueue()/processNextRequest()/viewQueue() bodies
    //      with the commented versions further down in this file.
    // private TestRequestQueue testRequestQueue = new TestRequestQueue();

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

    // Adds one entry into the correct queue based on its priority.
    // This is the ONLY place priority decides anything - after this,
    // both queues just behave like plain FIFO lines.
    private void addToQueue(int testRequestId, String patientName, String testName, String priority) {
        QueueEntry entry = new QueueEntry(testRequestId, patientName, testName, priority);

        if (priority.equalsIgnoreCase("EMERGENCY")) {
            emergencyQueue.add(entry);
        } else {
            normalQueue.add(entry);
        }
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // private void addToQueue(int testRequestId, String patientName, String testName, String priority) {
    //     testRequestQueue.enqueue(testRequestId, patientName, testName, priority);
    // }

    // Called once when the program starts, to rebuild the in-memory queues
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
    // then adds into the correct in-memory queue using the same ID and real names
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
    // Always checks the emergencyQueue first - if it has anything waiting,
    // that gets processed no matter how long the normalQueue is.
    // Only once emergencyQueue is empty does normalQueue get served.
    // Equipment.Status is now updated automatically by the UpdateEquipmentStatus
    // trigger when TestRequest.Status changes to PROCESSING - no manual update needed.
    public int processNextRequest() {
        QueueEntry entry;

        if (!emergencyQueue.isEmpty()) {
            entry = emergencyQueue.poll();
        } else if (!normalQueue.isEmpty()) {
            entry = normalQueue.poll();
        } else {
            System.out.println("No pending test requests.");
            return -1;
        }

        int testRequestId = entry.testRequestId;

        testRequestDAO.updateTestRequestStatus(testRequestId, "PROCESSING");

        return testRequestId;
    }

    // Same as processNextRequest(), but also returns the patient name and test
    // name that the QueueEntry already had in memory, instead of throwing them
    // away and forcing the Lab Technician to look the ID up separately.
    // Array layout: [0] = TestRequestID, [1] = PatientName, [2] = TestName, [3] = Priority.
    // Returns null if the queue is empty (equivalent to the old -1 return).
    public String[] processNextRequestWithDetails() {
        QueueEntry entry;

        if (!emergencyQueue.isEmpty()) {
            entry = emergencyQueue.poll();
        } else if (!normalQueue.isEmpty()) {
            entry = normalQueue.poll();
        } else {
            System.out.println("No pending test requests.");
            return null;
        }

        testRequestDAO.updateTestRequestStatus(entry.testRequestId, "PROCESSING");

        return new String[] {
                String.valueOf(entry.testRequestId),
                entry.patientName,
                entry.testName,
                entry.priority
        };
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // public int processNextRequest() {
    //     if (testRequestQueue.isEmpty()) {
    //         System.out.println("No pending test requests.");
    //         return -1;
    //     }
    //     int testRequestId = testRequestQueue.deleteFromFront();
    //     testRequestDAO.updateTestRequestStatus(testRequestId, "PROCESSING");
    //     return testRequestId;
    // }

    // View the queue without removing anything - useful for a "View Pending Requests"
    // menu option. Prints all EMERGENCY requests first (in their arrival order),
    // then all NORMAL requests (in their arrival order) - matching exactly the
    // order they would actually be processed in.
    public void viewQueue() {
        if (emergencyQueue.isEmpty() && normalQueue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }

        System.out.println("---- Current Test Request Queue ----");

        for (QueueEntry entry : emergencyQueue) {
            System.out.println("Request ID: " + entry.testRequestId +
                    " | Patient: " + entry.patientName +
                    " | Test: " + entry.testName +
                    " | Priority: " + entry.priority);
        }

        for (QueueEntry entry : normalQueue) {
            System.out.println("Request ID: " + entry.testRequestId +
                    " | Patient: " + entry.patientName +
                    " | Test: " + entry.testName +
                    " | Priority: " + entry.priority);
        }

        System.out.println("-------------------------------------");
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // public void viewQueue() {
    //     testRequestQueue.display();
    // }

    public boolean isQueueEmpty() {
        return emergencyQueue.isEmpty() && normalQueue.isEmpty();
    }

    // ---- DATA STRUCTURES EVALUATION ALTERNATIVE (commented) ----
    // public boolean isQueueEmpty() {
    //     return testRequestQueue.isEmpty();
    // }
}