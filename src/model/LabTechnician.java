package model;

public class LabTechnician {

    private int labTechID;
    private String firstName;
    private String lastName;
    private String name;
    private String username;
    private String email;
    private String password;
    private String phoneNo;
    private String qualification;
    private int hospitalID;

    public LabTechnician() {}

    public LabTechnician(int labTechID, String firstName, String lastName, String name,
                         String username, String email, String password, String phoneNo,
                         String qualification, int hospitalID) {
        this.labTechID = labTechID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.qualification = qualification;
        this.hospitalID = hospitalID;
    }

    public int getLabTechID() { return labTechID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNo() { return phoneNo; }
    public String getQualification() { return qualification; }
    public int getHospitalID() { return hospitalID; }

    public void setLabTechID(int labTechID) { this.labTechID = labTechID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }

    public String toString() {
        return "Lab Tech ID: " + labTechID +
                " | Name: " + name +
                " | Qualification: " + qualification +
                " | Hospital ID: " + hospitalID;
    }
}