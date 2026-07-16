package model;

public class Patient {

    private int patientID;
    private String firstName;
    private String lastName;
    private String name;
    private String username;
    private String email;
    private String password;
    private String phoneNo;
    private String dob;
    private String gender;
    private String bloodGroup;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private int hospitalID;

    public Patient() {}

    public Patient(int patientID, String firstName, String lastName, String name,
                   String username, String email, String password, String phoneNo,
                   String dob, String gender, String bloodGroup, String street,
                   String city, String state, String pincode, int hospitalID) {
        this.patientID = patientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.dob = dob;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.hospitalID = hospitalID;
    }

    public int getPatientID() { return patientID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNo() { return phoneNo; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getBloodGroup() { return bloodGroup; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
    public int getHospitalID() { return hospitalID; }

    public void setPatientID(int patientID) { this.patientID = patientID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }

    public String toString() {
        return "Patient ID: " + patientID +
                " | Name: " + name +
                " | DOB: " + dob +
                " | Gender: " + gender +
                " | Blood Group: " + bloodGroup +
                " | City: " + city;
    }
}