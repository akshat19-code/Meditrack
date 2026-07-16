package dao;

import database.DatabaseConnection;
import model.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // Insert a new Admin - called when Master Admin creates the first Hospital Admin
    public boolean insertAdmin(Admin a) {
        String query = "INSERT INTO Admin (FirstName, LastName, Name, Username, Password, Email, PhoneNo, HospitalID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, a.getFirstName());
            pstmt.setString(2, a.getLastName());
            pstmt.setString(3, a.getFirstName() + " " + a.getLastName());
            pstmt.setString(4, a.getUsername());
            pstmt.setString(5, a.getPassword());
            pstmt.setString(6, a.getEmail());
            pstmt.setString(7, a.getPhoneNo());
            pstmt.setInt(8, a.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Admin: " + e.getMessage());
            return false;
        }
    }

    // Fetch an Admin by Username within a specific Hospital - used for login
    // (Hospital Code + Username + Password, per your login rules)
    public Admin getAdminByUsername(String username, int hospitalId) {
        String query = "SELECT * FROM Admin WHERE Username = ? AND HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildAdminFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Admin: " + e.getMessage());
        }
        return null;
    }

    // Helper method - builds an Admin object from a ResultSet row
    private Admin buildAdminFromResultSet(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setAdminID(rs.getInt("AdminID"));
        a.setFirstName(rs.getString("FirstName"));
        a.setLastName(rs.getString("LastName"));
        a.setUsername(rs.getString("Username"));
        a.setPassword(rs.getString("Password"));
        a.setEmail(rs.getString("Email"));
        a.setPhoneNo(rs.getString("PhoneNo"));
        a.setHospitalID(rs.getInt("HospitalID"));
        return a;
    }
}