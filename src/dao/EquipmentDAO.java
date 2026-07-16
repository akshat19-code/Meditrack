package dao;

import database.DatabaseConnection;
import model.Equipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    // Insert a new Equipment - called only when findByEquipmentName finds no existing match
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

    // Case-insensitive, hospital-scoped lookup - the core of the TestType auto-reuse feature.
    // If this returns non-null, TestTypeService reuses this EquipmentID instead of creating a new row.
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

    // Fetch Equipment by ID - used whenever TestType/TestRequest need full equipment details
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

    // Update Status only - used when a TestRequest starts processing (-> IN USE)
    // and when its Report is generated (-> AVAILABLE again)
    public boolean updateEquipmentStatus(int equipmentId, String newStatus) {
        String query = "UPDATE Equipment SET Status = ? WHERE EquipmentID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, equipmentId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Equipment status: " + e.getMessage());
            return false;
        }
    }

    // Fetch all Equipment in a Hospital - used by Admin when viewing equipment list
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

    // Helper method - builds an Equipment object from a ResultSet row
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