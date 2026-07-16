package model;

public class TestRequest {

    private int testRequestID;
    private String requestDate;
    private String equipmentUsageDate;
    private String priority;
    private String status;
    private int admissionID;
    private int doctorID;
    private int testTypeID;
    private int equipmentID;

    public TestRequest() {}

    public TestRequest(int testRequestID, String requestDate, String equipmentUsageDate,
                       String priority, String status, int admissionID, int doctorID,
                       int testTypeID, int equipmentID) {
        this.testRequestID = testRequestID;
        this.requestDate = requestDate;
        this.equipmentUsageDate = equipmentUsageDate;
        this.priority = priority;
        this.status = status;
        this.admissionID = admissionID;
        this.doctorID = doctorID;
        this.testTypeID = testTypeID;
        this.equipmentID = equipmentID;
    }

    public int getTestRequestID() { return testRequestID; }
    public String getRequestDate() { return requestDate; }
    public String getEquipmentUsageDate() { return equipmentUsageDate; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public int getAdmissionID() { return admissionID; }
    public int getDoctorID() { return doctorID; }
    public int getTestTypeID() { return testTypeID; }
    public int getEquipmentID() { return equipmentID; }

    public void setTestRequestID(int testRequestID) { this.testRequestID = testRequestID; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public void setEquipmentUsageDate(String equipmentUsageDate) {this.equipmentUsageDate = equipmentUsageDate;}
    public void setPriority(String priority) { this.priority = priority; }
    public void setStatus(String status) { this.status = status; }
    public void setAdmissionID(int admissionID) { this.admissionID = admissionID; }
    public void setDoctorID(int doctorID) { this.doctorID = doctorID; }
    public void setTestTypeID(int testTypeID) { this.testTypeID = testTypeID; }
    public void setEquipmentID(int equipmentID) { this.equipmentID = equipmentID; }

    @Override
    public String toString() {
        return "Request ID: " + testRequestID +
                " | Admission ID: " + admissionID +
                " | Test Type ID: " + testTypeID +
                " | Priority: " + priority +
                " | Status: " + status +
                " | Request Date: " + requestDate +
                " | Equipment Usage Date: " + equipmentUsageDate;
    }
}