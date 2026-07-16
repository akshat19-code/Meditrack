package dao;

import database.DatabaseConnection;
import model.Bill;

import java.sql.*;

public class BillDAO {

    // Insert a new Bill - called by BillingService right after Admin discharges a patient
    public boolean insertBill(Bill b) {
        String query = "INSERT INTO Bill (RoomCharge, DoctorFee, TestCharge, TotalAmount, BillDate, AdmissionID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setDouble(1, b.getRoomCharge());
            pstmt.setDouble(2, b.getDoctorFee());
            pstmt.setDouble(3, b.getTestCharge());
            pstmt.setDouble(4, b.getTotalAmount());
            pstmt.setString(5, b.getBillDate());
            pstmt.setInt(6, b.getAdmissionID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Bill: " + e.getMessage());
            return false;
        }
    }

    // Fetch a Bill by its AdmissionID - since Bill is 1:1 with Admission.
    // Used when Patient views their final bill after discharge.
    public Bill getBillByAdmissionId(int admissionId) {
        String query = "SELECT * FROM Bill WHERE AdmissionID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, admissionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Bill b = new Bill();
                b.setBillID(rs.getInt("BillID"));
                b.setRoomCharge(rs.getDouble("RoomCharge"));
                b.setDoctorFee(rs.getDouble("DoctorFee"));
                b.setTestCharge(rs.getDouble("TestCharge"));
                b.setTotalAmount(rs.getDouble("TotalAmount"));
                b.setBillDate(rs.getString("BillDate"));
                b.setAdmissionID(rs.getInt("AdmissionID"));
                return b;
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Bill: " + e.getMessage());
        }
        return null;
    }
}