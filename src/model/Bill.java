package model;

public class Bill {

    private int billID;
    private double roomCharge;
    private double doctorFee;
    private double testCharge;
    private double totalAmount;
    private String billDate;
    private int admissionID;

    public Bill() {}

    public Bill(int billID, double roomCharge, double doctorFee, double testCharge,
                double totalAmount, String billDate, int admissionID) {
        this.billID = billID;
        this.roomCharge = roomCharge;
        this.doctorFee = doctorFee;
        this.testCharge = testCharge;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.admissionID = admissionID;
    }

    public int getBillID() { return billID; }
    public double getRoomCharge() { return roomCharge; }
    public double getDoctorFee() { return doctorFee; }
    public double getTestCharge() { return testCharge; }
    public double getTotalAmount() { return totalAmount; }
    public String getBillDate() { return billDate; }
    public int getAdmissionID() { return admissionID; }

    public void setBillID(int billID) { this.billID = billID; }
    public void setRoomCharge(double roomCharge) { this.roomCharge = roomCharge; }
    public void setDoctorFee(double doctorFee) { this.doctorFee = doctorFee; }
    public void setTestCharge(double testCharge) { this.testCharge = testCharge; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setBillDate(String billDate) { this.billDate = billDate; }
    public void setAdmissionID(int admissionID) { this.admissionID = admissionID; }

    public String toString() {
        return "Bill ID: " + billID +
                " | Room Charge: Rs." + roomCharge +
                " | Doctor Fee: Rs." + doctorFee +
                " | Test Charge: Rs." + testCharge +
                " | Total: Rs." + totalAmount +
                " | Date: " + billDate;
    }
}