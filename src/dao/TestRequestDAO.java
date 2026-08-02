package dao;

import database.DatabaseConnection;
import model.TestRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestRequestDAO {

    public int insertTestRequest(TestRequest tr) {
        String query = "INSERT INTO TestRequest (RequestDate, EquipmentUsageDate, Priority, Status, AdmissionID, DoctorID, TestTypeID, EquipmentID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tr.getRequestDate());
            pstmt.setString(2, tr.getEquipmentUsageDate());
            pstmt.setString(3, tr.getPriority());
            pstmt.setString(4, tr.getStatus());
            pstmt.setInt(5, tr.getAdmissionID());
            pstmt.setInt(6, tr.getDoctorID());
            pstmt.setInt(7, tr.getTestTypeID());
            pstmt.setInt(8, tr.getEquipmentID());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);   // return the new TestRequestID
                }
            }

        } catch (SQLException e) {
            System.out.println("Error inserting TestRequest: " + e.getMessage());
        }
        return -1;
    }

    public TestRequest getTestRequestById(int testRequestId) {
        String query = "SELECT * FROM TestRequest WHERE TestRequestID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, testRequestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildTestRequestFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching TestRequest: " + e.getMessage());
        }
        return null;
    }

    public List<TestRequest> getPendingTestRequests(int hospitalId) {
        List<TestRequest> requestList = new ArrayList<>();
        String query = "SELECT tr.* FROM TestRequest tr " +
                "JOIN Admission a ON tr.AdmissionID = a.AdmissionID " +
                "WHERE a.HospitalID = ? AND tr.Status = 'PENDING' " +
                "ORDER BY tr.RequestDate ASC";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requestList.add(buildTestRequestFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching pending TestRequests: " + e.getMessage());
        }
        return requestList;
    }


    public boolean updateTestRequestStatus(int testRequestId, String newStatus) {
        String query = "UPDATE TestRequest SET Status = ? WHERE TestRequestID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, testRequestId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating TestRequest status: " + e.getMessage());
            return false;
        }
    }

    private TestRequest buildTestRequestFromResultSet(ResultSet rs) throws SQLException {
        TestRequest tr = new TestRequest();
        tr.setTestRequestID(rs.getInt("TestRequestID"));
        tr.setRequestDate(rs.getString("RequestDate"));
        tr.setEquipmentUsageDate(rs.getString("EquipmentUsageDate"));
        tr.setPriority(rs.getString("Priority"));
        tr.setStatus(rs.getString("Status"));
        tr.setAdmissionID(rs.getInt("AdmissionID"));
        tr.setDoctorID(rs.getInt("DoctorID"));
        tr.setTestTypeID(rs.getInt("TestTypeID"));
        tr.setEquipmentID(rs.getInt("EquipmentID"));
        return tr;
    }
}