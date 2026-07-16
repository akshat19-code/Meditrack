package dao;

import database.DatabaseConnection;
import model.MasterAdmin;

import java.sql.*;

public class MasterAdminDAO {

    // Fetch a MasterAdmin by Username - used for login
    public MasterAdmin getMasterAdminByUsername(String username) {
        String query = "SELECT * FROM MasterAdmin WHERE Username = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MasterAdmin ma = new MasterAdmin();
                ma.setMasterAdminID(rs.getInt("MasterAdminID"));
                ma.setFirstName(rs.getString("FirstName"));
                ma.setLastName(rs.getString("LastName"));
                ma.setUsername(rs.getString("Username"));
                ma.setPassword(rs.getString("Password"));
                ma.setEmail(rs.getString("Email"));
                ma.setPhoneNo(rs.getString("PhoneNo"));
                return ma;
            }

        } catch (SQLException e) {
            System.out.println("Error fetching MasterAdmin: " + e.getMessage());
        }
        return null;
    }
}