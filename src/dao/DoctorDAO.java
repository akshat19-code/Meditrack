package dao;

import database.DatabaseConnection;
import model.Doctor;

import java.sql.*;
import java.util.*;

public class DoctorDAO {
    Connection con = DatabaseConnection.getConnection();
    public boolean insertDoctor(Doctor d) {
        String query = "INSERT INTO Doctor (DoctorCode, FirstName, LastName, Name, Username, Password, Email, PhoneNo, " +
                "Specialization, Department, Qualification, ConsultationFee, PatientCount, HospitalID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, d.getDoctorCode());
            pstmt.setString(2, d.getFirstName());
            pstmt.setString(3, d.getLastName());
            pstmt.setString(4, d.getFirstName() + " " + d.getLastName());
            pstmt.setString(5, d.getUsername());
            pstmt.setString(6, d.getPassword());
            pstmt.setString(7, d.getEmail());
            pstmt.setString(8, d.getPhoneNo());
            pstmt.setString(9, d.getSpecialization());
            pstmt.setString(10, d.getDepartment());
            pstmt.setString(11, d.getQualification());
            pstmt.setDouble(12, d.getConsultationFee());
            pstmt.setInt(13, 0);
            pstmt.setInt(14, d.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Doctor: " + e.getMessage());
            return false;
        }
    }

    public Doctor getDoctorByUsername(String username, int hospitalId) {
        String query = "SELECT * FROM Doctor WHERE Username = ? AND HospitalID = ?";


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

    public Doctor getDoctorById(int doctorId) {
        String query = "SELECT * FROM Doctor WHERE DoctorID = ?";


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

    public List<Doctor> getAllDoctorsByHospital(int hospitalId) {
        List<Doctor> doctorList = new ArrayList<>();
        String query = "SELECT * FROM Doctor WHERE HospitalID = ?";


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

    public boolean updatePassword(int doctorId, String newPassword) {
        String query = "UPDATE Doctor SET Password = ? WHERE DoctorID = ?";


        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, doctorId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Doctor password: " + e.getMessage());
            return false;
        }
    }
    public Doctor getDoctorByPhone(String phone) {
        String query = "SELECT * FROM Doctor WHERE PhoneNo = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildDoctorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking Doctor phone number: " + e.getMessage());
        }
        return null;
    }

    public Doctor getDoctorByEmail(String email) {
        String query = "SELECT * FROM Doctor WHERE Email = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildDoctorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking Doctor email: " + e.getMessage());
        }
        return null;
    }

    public String generateDoctorCode(int hospitalID){
        String query = "SELECT COUNT(*) FROM Doctor WHERE HospitalID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("DOC%03d", nextId);
            }

        } catch (SQLException e) {
            System.out.println("Error generating Doctor Code: " + e.getMessage());
        }

        return null;
    }

    private Doctor buildDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setDoctorID(rs.getInt("DoctorID"));
        d.setDoctorCode(rs.getString("DoctorCode"));
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