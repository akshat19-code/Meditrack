package model;

public class MasterAdmin {

    private int masterAdminID;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String phoneNo;

    // Constructor
    public MasterAdmin(int masterAdminID, String firstName, String lastName,
                       String username, String email, String password, String phoneNo) {
        this.masterAdminID = masterAdminID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
    }

    // Empty Constructor
    public MasterAdmin() {}

    // Getters
    public int getMasterAdminID() { return masterAdminID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNo() { return phoneNo; }

    // Setters
    public void setMasterAdminID(int masterAdminID) { this.masterAdminID = masterAdminID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    // Display
    public String toString() {
        return "Master Admin ID: " + masterAdminID +
                " | Name: " + firstName + " " + lastName +
                " | Username: " + username +
                " | Email: " + email +
                " | Phone: " + phoneNo;
    }
}