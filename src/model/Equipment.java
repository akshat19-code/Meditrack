package model;

public class Equipment {

    private int equipmentID;
    private String equipmentName;
    private String status;
    private String purchaseDate;
    private int hospitalID;

    public Equipment() {}

    public Equipment(int equipmentID, String equipmentName, String status,
                     String purchaseDate, int hospitalID) {
        this.equipmentID = equipmentID;
        this.equipmentName = equipmentName;
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.hospitalID = hospitalID;
    }

    public int getEquipmentID() { return equipmentID; }
    public String getEquipmentName() { return equipmentName; }
    public String getStatus() { return status; }
    public String getPurchaseDate() { return purchaseDate; }
    public int getHospitalID() { return hospitalID; }

    public void setEquipmentID(int equipmentID) { this.equipmentID = equipmentID; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public void setStatus(String status) { this.status = status; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }

    public String toString() {
        return "Equipment ID: " + equipmentID +
                " | Name: " + equipmentName +
                " | Status: " + status +
                " | Purchase Date: " + purchaseDate;
    }
}