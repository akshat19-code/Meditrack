package model;

public class Report {

    private int reportID;
    private double resultValue;
    private String resultStatus;
    private String analysisDate;
    private String doctorNotes;
    private int testRequestID;
    private int labTechID;

    public Report() {}

    public Report(int reportID, double resultValue, String resultStatus,
                  String analysisDate, String doctorNotes,
                  int testRequestID, int labTechID) {
        this.reportID = reportID;
        this.resultValue = resultValue;
        this.resultStatus = resultStatus;
        this.analysisDate = analysisDate;
        this.doctorNotes = doctorNotes;
        this.testRequestID = testRequestID;
        this.labTechID = labTechID;
    }

    public int getReportID() { return reportID; }
    public double getResultValue() { return resultValue; }
    public String getResultStatus() { return resultStatus; }
    public String getAnalysisDate() { return analysisDate; }
    public String getDoctorNotes() { return doctorNotes; }
    public int getTestRequestID() { return testRequestID; }
    public int getLabTechID() { return labTechID; }

    public void setReportID(int reportID) { this.reportID = reportID; }
    public void setResultValue(double resultValue) { this.resultValue = resultValue; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public void setAnalysisDate(String analysisDate) { this.analysisDate = analysisDate; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }
    public void setTestRequestID(int testRequestID) { this.testRequestID = testRequestID; }
    public void setLabTechID(int labTechID) { this.labTechID = labTechID; }

    public String toString() {
        return "Report ID: " + reportID +
                " | Result: " + String.format("%.2f", resultValue) +
                " | Status: " + resultStatus +
                " | Date: " + analysisDate +
                " | Notes: " + doctorNotes;
    }
}