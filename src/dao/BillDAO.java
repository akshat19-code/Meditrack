package dao;

import database.DatabaseConnection;
import model.Bill;

import java.sql.*;

public class BillDAO {

    public  double getDoctorRevenue(int hospitalId) {

        String query = """
            SELECT SUM(b.DoctorFee) AS Revenue
            FROM Bill b
            JOIN Admission a ON b.AdmissionID = a.AdmissionID
            WHERE a.HospitalID = ?
            """;

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("Revenue");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Doctor Revenue: " + e.getMessage());
        }

        return 0;
    }

    public double getTestRevenue(int hospitalId) {

        String query = """
            SELECT SUM(b.TestCharge) AS Revenue
            FROM Bill b
            JOIN Admission a ON b.AdmissionID = a.AdmissionID
            WHERE a.HospitalID = ?
            """;

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("Revenue");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Test Revenue: " + e.getMessage());
        }

        return 0;
    }

    public double getRoomRevenue(int hospitalId) {

        String query = """
            SELECT SUM(b.RoomCharge) AS Revenue
            FROM Bill b
            JOIN Admission a ON b.AdmissionID = a.AdmissionID
            WHERE a.HospitalID = ?
            """;

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("Revenue");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Room Revenue: " + e.getMessage());
        }

        return 0;
    }

    public double getTotalRevenue(int hospitalId) {

        String query = """
            SELECT SUM(b.TotalAmount) AS Revenue
            FROM Bill b
            JOIN Admission a ON b.AdmissionID = a.AdmissionID
            WHERE a.HospitalID = ?
            """;

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("Revenue");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Total Revenue: " + e.getMessage());
        }

        return 0;
    }

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