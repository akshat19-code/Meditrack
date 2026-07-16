package model;

public class Hospital {

    private int hospitalID;
    private String hospitalCode;
    private String hospitalName;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String phoneNo;
    private String email;
    private String status;
    private int masterAdminID;

    public Hospital() {}

    public Hospital(int hospitalID, String hospitalCode, String hospitalName,
                    String street, String city, String state, String pincode,
                    String phoneNo, String email, String status, int masterAdminID) {
        this.hospitalID = hospitalID;
        this.hospitalCode = hospitalCode;
        this.hospitalName = hospitalName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.phoneNo = phoneNo;
        this.email = email;
        this.status = status;
        this.masterAdminID = masterAdminID;
    }

    public int getHospitalID() { return hospitalID; }
    public String getHospitalCode() { return hospitalCode; }
    public String getHospitalName() { return hospitalName; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
    public String getPhoneNo() { return phoneNo; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public int getMasterAdminID() { return masterAdminID; }

    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }
    public void setHospitalCode(String hospitalCode) { this.hospitalCode = hospitalCode; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
    public void setMasterAdminID(int masterAdminID) { this.masterAdminID = masterAdminID; }

    public String toString() {
        return "Hospital ID: " + hospitalID +
                " | Code: " + hospitalCode +
                " | Name: " + hospitalName +
                " | City: " + city +
                " | Status: " + status;
    }
}