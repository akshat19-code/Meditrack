package dao;

import database.DatabaseConnection;
import model.LabTechnician;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabTechnicianDAO {

    // Insert a new LabTechnician - called when Admin adds a lab technician
    public boolean insertLabTechnician(LabTechnician lt) {
        String query = "INSERT INTO LabTechnician (FirstName, LastName, Name, Username, Password, Email, PhoneNo, Qualification, HospitalID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, lt.getFirstName());
            pstmt.setString(2, lt.getLastName());
            pstmt.setString(3, lt.getFirstName() + " " + lt.getLastName());
            pstmt.setString(4, lt.getUsername());
            pstmt.setString(5, lt.getPassword());
            pstmt.setString(6, lt.getEmail());
            pstmt.setString(7, lt.getPhoneNo());
            pstmt.setString(8, lt.getQualification());
            pstmt.setInt(9, lt.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting LabTechnician: " + e.getMessage());
            return false;
        }
    }

    // Fetch a LabTechnician by Username within a specific Hospital - used for login
    public LabTechnician getLabTechnicianByUsername(String username, int hospitalId) {
        String query = "SELECT * FROM LabTechnician WHERE Username = ? AND HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildLabTechFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching LabTechnician: " + e.getMessage());
        }
        return null;
    }

    // Fetch a LabTechnician by ID - used when a Report needs to display who uploaded it
    public LabTechnician getLabTechnicianById(int labTechId) {
        String query = "SELECT * FROM LabTechnician WHERE LabTechID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, labTechId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildLabTechFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching LabTechnician: " + e.getMessage());
        }
        return null;
    }

    // Fetch all Lab Technicians in a Hospital - used by Admin when viewing the
    // lab technician list (mirrors DoctorDAO.getAllDoctorsByHospital())
    public List<LabTechnician> getAllLabTechniciansByHospital(int hospitalId) {
        List<LabTechnician> labTechList = new ArrayList<>();
        String query = "SELECT * FROM LabTechnician WHERE HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                labTechList.add(buildLabTechFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching LabTechnicians: " + e.getMessage());
        }
        return labTechList;
    }

    // Helper method - builds a LabTechnician object from a ResultSet row
    private LabTechnician buildLabTechFromResultSet(ResultSet rs) throws SQLException {
        LabTechnician lt = new LabTechnician();
        lt.setLabTechID(rs.getInt("LabTechID"));
        lt.setFirstName(rs.getString("FirstName"));
        lt.setLastName(rs.getString("LastName"));
        lt.setName(rs.getString("Name"));
        lt.setUsername(rs.getString("Username"));
        lt.setPassword(rs.getString("Password"));
        lt.setEmail(rs.getString("Email"));
        lt.setPhoneNo(rs.getString("PhoneNo"));
        lt.setQualification(rs.getString("Qualification"));
        lt.setHospitalID(rs.getInt("HospitalID"));
        return lt;
    }
}