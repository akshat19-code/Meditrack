package dao;

import database.DatabaseConnection;
import model.Equipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    public boolean insertEquipment(Equipment eq) {
        String query = "INSERT INTO Equipment (EquipmentName, Status, PurchaseDate, HospitalID) VALUES (?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, eq.getEquipmentName());
            pstmt.setString(2, eq.getStatus());
            pstmt.setString(3, eq.getPurchaseDate());
            pstmt.setInt(4, eq.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Equipment: " + e.getMessage());
            return false;
        }
    }

    public Equipment findByEquipmentName(String equipmentName, int hospitalId) {
        String query = "SELECT * FROM Equipment WHERE LOWER(EquipmentName) = LOWER(?) AND HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, equipmentName);
            pstmt.setInt(2, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildEquipmentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking Equipment: " + e.getMessage());
        }
        return null;
    }

    public Equipment getEquipmentById(int equipmentId) {
        String query = "SELECT * FROM Equipment WHERE EquipmentID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, equipmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildEquipmentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Equipment: " + e.getMessage());
        }
        return null;
    }

    public List<Equipment> getAllEquipmentByHospital(int hospitalId) {
        List<Equipment> equipmentList = new ArrayList<>();
        String query = "SELECT * FROM Equipment WHERE HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                equipmentList.add(buildEquipmentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Equipment: " + e.getMessage());
        }
        return equipmentList;
    }

    private Equipment buildEquipmentFromResultSet(ResultSet rs) throws SQLException {
        Equipment eq = new Equipment();
        eq.setEquipmentID(rs.getInt("EquipmentID"));
        eq.setEquipmentName(rs.getString("EquipmentName"));
        eq.setStatus(rs.getString("Status"));
        eq.setPurchaseDate(rs.getString("PurchaseDate"));
        eq.setHospitalID(rs.getInt("HospitalID"));
        return eq;
    }
}