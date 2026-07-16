package dao;

import database.DatabaseConnection;
import model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    // Insert a new Doctor - called when Admin adds a doctor
    public boolean insertDoctor(Doctor d) {
        String query = "INSERT INTO Doctor (FirstName, LastName, Name, Username, Password, Email, PhoneNo, " +
                "Specialization, Department, Qualification, ConsultationFee, PatientCount, HospitalID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, d.getFirstName());
            pstmt.setString(2, d.getLastName());
            pstmt.setString(3, d.getFirstName() + " " + d.getLastName());
            pstmt.setString(4, d.getUsername());
            pstmt.setString(5, d.getPassword());
            pstmt.setString(6, d.getEmail());
            pstmt.setString(7, d.getPhoneNo());
            pstmt.setString(8, d.getSpecialization());
            pstmt.setString(9, d.getDepartment());
            pstmt.setString(10, d.getQualification());
            pstmt.setDouble(11, d.getConsultationFee());
            pstmt.setInt(12, 0);   // new doctor always starts with 0 patients
            pstmt.setInt(13, d.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Doctor: " + e.getMessage());
            return false;
        }
    }

    // Fetch a Doctor by Username within a specific Hospital - used for login
    public Doctor getDoctorByUsername(String username, int hospitalId) {
        String query = "SELECT * FROM Doctor WHERE Username = ? AND HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildDoctorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Doctor: " + e.getMessage());
        }
        return null;
    }

    // Fetch a Doctor by ID - used whenever another table's DoctorID needs full details
    // (e.g. showing Admission info, or Patient viewing their assigned doctor)
    public Doctor getDoctorById(int doctorId) {
        String query = "SELECT * FROM Doctor WHERE DoctorID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildDoctorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Doctor: " + e.getMessage());
        }
        return null;
    }

    // Fetch all Doctors in a Hospital - used by Workload Manager to find the least busy doctor,
    // and by Admin when assigning a doctor to a new Admission
    public List<Doctor> getAllDoctorsByHospital(int hospitalId) {
        List<Doctor> doctorList = new ArrayList<>();
        String query = "SELECT * FROM Doctor WHERE HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                doctorList.add(buildDoctorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Doctors: " + e.getMessage());
        }
        return doctorList;
    }

    // Increment PatientCount by 1 - called whenever a new Admission is created for this doctor
    // (Workload Manager relies on this staying accurate)
    public boolean incrementPatientCount(int doctorId) {
        String query = "UPDATE Doctor SET PatientCount = PatientCount + 1 WHERE DoctorID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating PatientCount: " + e.getMessage());
            return false;
        }
    }

    // Helper method - builds a Doctor object from a ResultSet row
    private Doctor buildDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setDoctorID(rs.getInt("DoctorID"));
        d.setFirstName(rs.getString("FirstName"));
        d.setLastName(rs.getString("LastName"));
        d.setName(rs.getString("Name"));
        d.setUsername(rs.getString("Username"));
        d.setPassword(rs.getString("Password"));
        d.setEmail(rs.getString("Email"));
        d.setPhoneNo(rs.getString("PhoneNo"));
        d.setSpecialization(rs.getString("Specialization"));
        d.setDepartment(rs.getString("Department"));
        d.setQualification(rs.getString("Qualification"));
        d.setConsultationFee(rs.getDouble("ConsultationFee"));
        d.setPatientCount(rs.getInt("PatientCount"));
        d.setHospitalID(rs.getInt("HospitalID"));
        return d;
    }
}