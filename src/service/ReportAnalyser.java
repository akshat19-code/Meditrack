package service;

import dao.*;
import model.*;

public class ReportAnalyser {

    private ReportDAO reportDAO = new ReportDAO();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private LabTechnicianDAO labTechDAO = new LabTechnicianDAO();
    private FileManager fileManager = new FileManager();

    public String analyseResult(double resultValue, double normalMin, double normalMax) {
        if (resultValue >= normalMin && resultValue <= normalMax) {
            return "NORMAL";
        }

        double range = normalMax - normalMin;
        double criticalLowerBound = normalMin - (range * 0.20);
        double criticalUpperBound = normalMax + (range * 0.20);

        if (resultValue < criticalLowerBound || resultValue > criticalUpperBound) {
            return "CRITICAL";
        }
        return "ABNORMAL";
    }

    public boolean generateReport(int testRequestId, double resultValue, String analysisDate, int labTechId) {
        TestRequest tr = testRequestDAO.getTestRequestById(testRequestId);
        if (tr == null) {
            System.out.println("Test request not found.");
            return false;
        }

        TestType tt = testTypeDAO.getTestTypeById(tr.getTestTypeID());
        if (tt == null) {
            System.out.println("Test type not found.");
            return false;
        }

        String status = analyseResult(resultValue, tt.getNormalMin(), tt.getNormalMax());

        Report r = new Report();
        r.setResultValue(resultValue);
        r.setResultStatus(status);
        r.setAnalysisDate(analysisDate);
        r.setDoctorNotes(null);
        r.setTestRequestID(testRequestId);
        r.setLabTechID(labTechId);

        boolean inserted = reportDAO.insertReport(r);
        if (!inserted) {
            return false;
        }

        testRequestDAO.updateTestRequestStatus(testRequestId, "COMPLETED");

        Report savedReport = reportDAO.getReportByTestRequestId(testRequestId);

        String[] patientSummary = admissionDAO.getPatientSummaryByAdmissionId(tr.getAdmissionID());
        LabTechnician lt = labTechDAO.getLabTechnicianById(labTechId);

        if (savedReport != null && patientSummary != null && lt != null) {
            int patientId = Integer.parseInt(patientSummary[0]);
            String patientName = patientSummary[1];

            fileManager.writeReportFile(savedReport.getReportID(), testRequestId, patientName,
                    tt.getTestName(), resultValue, status, analysisDate, lt.getName());
            fileManager.addReportHistoryEntry(patientId, savedReport.getReportID(),
                    tt.getTestName(), status, analysisDate);
        }

        if (status.equals("CRITICAL")) {
            System.out.println("ALERT: Critical result! Doctor should be notified immediately.");
        }

        return true;
    }
}