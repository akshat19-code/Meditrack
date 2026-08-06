package dao;

import database.*;
import model.Admin;

import java.sql.*;
import java.util.*;

public class AdminDAO {

    Connection con = DatabaseConnection.getConnection();

    public boolean insertAdmin(Admin a) {
        String query = "INSERT INTO Admin (FirstName, LastName, Name, Username, Password, Email, PhoneNo, HospitalID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

    public Admin getAdminByUsername(String username, int hospitalId) {
        String query = "SELECT * FROM Admin WHERE Username = ? AND HospitalID = ?";

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

    public boolean updatePassword(int adminId, String newPassword) {
        String query = "UPDATE Admin SET Password = ? WHERE AdminID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, adminId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Admin password: " + e.getMessage());
            return false;
        }
    }

    public Admin getAdminByPhone(String phone) {
        String query = "SELECT * FROM Admin WHERE PhoneNo = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildAdminFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking Admin phone number: " + e.getMessage());
        }
        return null;
    }

    public Admin getAdminByEmail(String email) {
        String query = "SELECT * FROM Admin WHERE Email = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildAdminFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking Admin email: " + e.getMessage());
        }
        return null;
    }

    public List<Admin> getAllAdminsByHospital(int hospitalId) {
        List<Admin> adminList = new ArrayList<>();
        String query = "SELECT * FROM Admin WHERE HospitalID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                adminList.add(buildAdminFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Admins: " + e.getMessage());
        }
        return adminList;
    }

    public boolean deleteAdmin(int adminId) {
        String query = "DELETE FROM Admin WHERE AdminID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, adminId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting Admin: " + e.getMessage());
            return false;
        }
    }

    private Admin buildAdminFromResultSet(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setAdminID(rs.getInt("AdminID"));
        a.setFirstName(rs.getString("FirstName"));
        a.setLastName(rs.getString("LastName"));
        a.setName(rs.getString("Name"));
        a.setUsername(rs.getString("Username"));
        a.setPassword(rs.getString("Password"));
        a.setEmail(rs.getString("Email"));
        a.setPhoneNo(rs.getString("PhoneNo"));
        a.setHospitalID(rs.getInt("HospitalID"));
        return a;
    }
}