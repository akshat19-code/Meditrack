package model;

public class Admin {

    private int adminID;
    private String firstName;
    private String lastName;
    private String name;
    private String username;
    private String email;
    private String password;
    private String phoneNo;
    private int hospitalID;

    public Admin() {}

    public Admin(int adminID, String firstName, String lastName, String name,
                 String username, String email, String password, String phoneNo,
                 int hospitalID) {
        this.adminID = adminID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.hospitalID = hospitalID;
    }

    public int getAdminID() { return adminID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNo() { return phoneNo; }
    public int getHospitalID() { return hospitalID; }

    public void setAdminID(int adminID) { this.adminID = adminID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }

    public String toString() {
        return "Admin ID: " + adminID +
                " | Name: " + name +
                " | Username: " + username +
                " | Email: " + email +
                " | Phone: " + phoneNo +
                " | Hospital ID: " + hospitalID;
    }
}