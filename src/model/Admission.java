package model;

public class Admission {

    private int admissionID;
    private String admissionDate;
    private String dischargeDate;
    private String roomNumber;
    private String roomType;
    private double roomCharge;
    private String status;
    private int patientID;
    private int doctorID;
    private int adminID;
    private int hospitalID;

    public Admission() {}

    public Admission(int admissionID, String admissionDate, String dischargeDate,
                     String roomNumber, String roomType, double roomCharge, String status,
                     int patientID, int doctorID, int adminID, int hospitalID) {
        this.admissionID = admissionID;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomCharge = roomCharge;
        this.status = status;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.adminID = adminID;
        this.hospitalID = hospitalID;
    }

    public int getAdmissionID() { return admissionID; }
    public String getAdmissionDate() { return admissionDate; }
    public String getDischargeDate() { return dischargeDate; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getRoomCharge() { return roomCharge; }
    public String getStatus() { return status; }
    public int getPatientID() { return patientID; }
    public int getDoctorID() { return doctorID; }
    public int getAdminID() { return adminID; }
    public int getHospitalID() { return hospitalID; }

    public void setAdmissionID(int admissionID) { this.admissionID = admissionID; }
    public void setAdmissionDate(String admissionDate) { this.admissionDate = admissionDate; }
    public void setDischargeDate(String dischargeDate) { this.dischargeDate = dischargeDate; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setRoomCharge(double roomCharge) { this.roomCharge = roomCharge; }
    public void setStatus(String status) { this.status = status; }
    public void setPatientID(int patientID) { this.patientID = patientID; }
    public void setDoctorID(int doctorID) { this.doctorID = doctorID; }
    public void setAdminID(int adminID) { this.adminID = adminID; }
    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }

    public String toString() {
        return "Admission ID: " + admissionID +
                " | Patient ID: " + patientID +
                " | Doctor ID: " + doctorID +
                " | Room: " + roomNumber +
                " | Room Type: " + roomType +
                " | Status: " + status +
                " | Admission Date: " + admissionDate;
    }
}