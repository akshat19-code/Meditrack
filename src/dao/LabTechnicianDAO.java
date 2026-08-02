package dao;

import database.DatabaseConnection;
import model.LabTechnician;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabTechnicianDAO {

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

    // Username stays hospital-specific.
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

    public boolean updatePassword(int labTechId, String newPassword) {
        String query = "UPDATE LabTechnician SET Password = ? WHERE LabTechID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, labTechId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating LabTechnician password: " + e.getMessage());
            return false;
        }
    }

    // NEW - Phone number checked GLOBALLY across all hospitals.
    public LabTechnician getLabTechnicianByPhone(String phone) {
        String query = "SELECT * FROM LabTechnician WHERE PhoneNo = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildLabTechFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking LabTechnician phone number: " + e.getMessage());
        }
        return null;
    }

    // NEW - Email checked GLOBALLY across all hospitals.
    public LabTechnician getLabTechnicianByEmail(String email) {
        String query = "SELECT * FROM LabTechnician WHERE Email = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildLabTechFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking LabTechnician email: " + e.getMessage());
        }
        return null;
    }

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