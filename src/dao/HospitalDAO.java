package dao;

import database.DatabaseConnection;
import model.Hospital;

import java.sql.*;

public class HospitalDAO {

    // Insert a new Hospital - called when Master Admin registers one
    public boolean insertHospital(Hospital h) {
        String query = "INSERT INTO Hospital (HospitalCode, HospitalName, Street, City, State, " +
                "Pincode, PhoneNo, Email, Status, MasterAdminID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, h.getHospitalCode());
            pstmt.setString(2, h.getHospitalName());
            pstmt.setString(3, h.getStreet());
            pstmt.setString(4, h.getCity());
            pstmt.setString(5, h.getState());
            pstmt.setString(6, h.getPincode());
            pstmt.setString(7, h.getPhoneNo());
            pstmt.setString(8, h.getEmail());
            pstmt.setString(9, h.getStatus());
            pstmt.setInt(10, h.getMasterAdminID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Hospital: " + e.getMessage());
            return false;
        }
    }

    // Fetch a Hospital by its code - used at every login screen (Admin/Doctor/LabTech/Patient)
    // to validate the Hospital Code and get the HospitalID for further queries
    public Hospital getHospitalByCode(String hospitalCode) {
        String query = "SELECT * FROM Hospital WHERE HospitalCode = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, hospitalCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildHospitalFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Hospital: " + e.getMessage());
        }
        return null;
    }

    // Fetch a Hospital by ID - used whenever another table's HospitalID needs full hospital details
    public Hospital getHospitalById(int hospitalId) {
        String query = "SELECT * FROM Hospital WHERE HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildHospitalFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Hospital: " + e.getMessage());
        }
        return null;
    }

    // Update Status only - used by Master Admin to suspend/reactivate/remove a hospital
    public boolean updateHospitalStatus(int hospitalId, String newStatus) {
        String query = "UPDATE Hospital SET Status = ? WHERE HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, hospitalId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Hospital status: " + e.getMessage());
            return false;
        }
    }

    // Fetch all Hospitals - used for Master Admin's system-wide reports view
    public java.util.List<Hospital> getAllHospitals() {
        java.util.List<Hospital> hospitalList = new java.util.ArrayList<>();
        String query = "SELECT * FROM Hospital";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                hospitalList.add(buildHospitalFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Hospitals: " + e.getMessage());
        }
        return hospitalList;
    }

    // Helper method - builds a Hospital object from a ResultSet row
    private Hospital buildHospitalFromResultSet(ResultSet rs) throws SQLException {
        Hospital h = new Hospital();
        h.setHospitalID(rs.getInt("HospitalID"));
        h.setHospitalCode(rs.getString("HospitalCode"));
        h.setHospitalName(rs.getString("HospitalName"));
        h.setStreet(rs.getString("Street"));
        h.setCity(rs.getString("City"));
        h.setState(rs.getString("State"));
        h.setPincode(rs.getString("Pincode"));
        h.setPhoneNo(rs.getString("PhoneNo"));
        h.setEmail(rs.getString("Email"));
        h.setStatus(rs.getString("Status"));
        h.setMasterAdminID(rs.getInt("MasterAdminID"));
        return h;
    }
}