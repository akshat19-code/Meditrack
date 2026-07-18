package model;

public class Doctor {

    private int doctorID;
    private String firstName;
    private String lastName;
    private String name;
    private String username;
    private String email;
    private String password;
    private String phoneNo;
    private String specialization;
    private String department;
    private String qualification;
    private double consultationFee;
    private int patientCount;
    private int hospitalID;

    public Doctor() {}

    public Doctor(int doctorID, String firstName, String lastName, String name,
                  String username, String email, String password, String phoneNo,
                  String specialization, String department, String qualification,
                  double consultationFee, int patientCount, int hospitalID) {
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.specialization = specialization;
        this.department = department;
        this.qualification = qualification;
        this.consultationFee = consultationFee;
        this.patientCount = patientCount;
        this.hospitalID = hospitalID;
    }

    public int getDoctorID() { return doctorID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNo() { return phoneNo; }
    public String getSpecialization() { return specialization; }
    public String getDepartment() { return department; }
    public String getQualification() { return qualification; }
    public double getConsultationFee() { return consultationFee; }
    public int getPatientCount() { return patientCount; }
    public int getHospitalID() { return hospitalID; }

    public void setDoctorID(int doctorID) { this.doctorID = doctorID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setDepartment(String department) { this.department = department; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    public void setPatientCount(int patientCount) { this.patientCount = patientCount; }
    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }

    public String toString() {
        return "Doctor ID: " + doctorID +
                " | Name: " + name +
                " | Specialization: " + specialization +
                " | Department: " + department +
                " | Patients: " + patientCount +
                " | Fee: Rs." + String.format("%.2f", consultationFee);
    }
}