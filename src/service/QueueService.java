package service;

import dao.*;
import ds.TestRequestQueue;
import model.*;

import java.util.*;

public class QueueService {

    private TestRequestQueue testRequestQueue = new TestRequestQueue();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();

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

    private void addToQueue(int testRequestId, String patientName, String testName, String priority) {
        testRequestQueue.enqueue(testRequestId, patientName, testName, priority);
    }

    public void loadPendingRequests(int hospitalId) {
        List<TestRequest> pending = testRequestDAO.getPendingTestRequests(hospitalId);
        for (TestRequest tr : pending) {
            String patientName = getPatientName(tr);
            String testName = getTestName(tr);
            addToQueue(tr.getTestRequestID(), patientName, testName, tr.getPriority());
        }
        System.out.println("Queue restored with " + pending.size() + " pending request(s).");
    }

    public boolean requestTest(TestRequest tr, String patientName, String testName) {
        int newId = testRequestDAO.insertTestRequest(tr);
        if (newId == -1) {
            System.out.println("Failed to create test request.");
            return false;
        }
        addToQueue(newId, patientName, testName, tr.getPriority());
        return true;
    }

    public String[] processNextRequestWithDetails() {
        String[] details = testRequestQueue.deleteFromFrontWithDetails();

        if (details == null) {
            return null;
        }

        testRequestDAO.updateTestRequestStatus(Integer.parseInt(details[0]), "PROCESSING");

        return details;
    }

    public void viewQueue() {
        testRequestQueue.display();
    }
}