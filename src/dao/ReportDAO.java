package dao;

import database.DatabaseConnection;
import model.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public boolean insertReport(Report r) {
        String query = "INSERT INTO Report (ResultValue, ResultStatus, AnalysisDate, DoctorNotes, TestRequestID, LabTechID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setDouble(1, r.getResultValue());
            pstmt.setString(2, r.getResultStatus());
            pstmt.setString(3, r.getAnalysisDate());
            pstmt.setString(4, r.getDoctorNotes());
            pstmt.setInt(5, r.getTestRequestID());
            pstmt.setInt(6, r.getLabTechID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Report: " + e.getMessage());
            return false;
        }
    }

    public Report getReportById(int reportId) {
        String query = "SELECT * FROM Report WHERE ReportID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, reportId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildReportFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Report: " + e.getMessage());
        }
        return null;
    }

    public Report getReportByTestRequestId(int testRequestId) {
        String query = "SELECT * FROM Report WHERE TestRequestID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, testRequestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildReportFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Report: " + e.getMessage());
        }
        return null;
    }

    public boolean updateDoctorNotes(int reportId, String newNote) {
        Report existing = getReportById(reportId);
        if (existing == null) {
            System.out.println("Report not found - cannot update notes.");
            return false;
        }

        String oldNotes = existing.getDoctorNotes();
        String combinedNotes;

        if (oldNotes == null || oldNotes.isBlank()) {
            combinedNotes = newNote;
        } else {
            combinedNotes = oldNotes + "\n----------\n" + newNote;
        }

        String query = "UPDATE Report SET DoctorNotes = ? WHERE ReportID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, combinedNotes);
            pstmt.setInt(2, reportId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating DoctorNotes: " + e.getMessage());
            return false;
        }
    }

    public List<Report> getReportsByPatient(int patientId) {
        List<Report> reportList = new ArrayList<>();
        String query = "SELECT r.* FROM Report r " +
                "JOIN TestRequest tr ON r.TestRequestID = tr.TestRequestID " +
                "JOIN Admission a ON tr.AdmissionID = a.AdmissionID " +
                "WHERE a.PatientID = ? " +
                "ORDER BY r.AnalysisDate ASC";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reportList.add(buildReportFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Patient Reports: " + e.getMessage());
        }
        return reportList;
    }

    // NEW - fetches all Reports belonging to the given Doctor's patients,
    // following the same join pattern as getReportsByPatient. Added to
    // support showing a Doctor's report list before asking for a Report ID.
    public List<Report> getReportsByDoctor(int doctorId) {
        List<Report> reportList = new ArrayList<>();
        String query = "SELECT r.* FROM Report r " +
                "JOIN TestRequest tr ON r.TestRequestID = tr.TestRequestID " +
                "JOIN Admission a ON tr.AdmissionID = a.AdmissionID " +
                "WHERE a.DoctorID = ? " +
                "ORDER BY r.AnalysisDate DESC";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reportList.add(buildReportFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Doctor Reports: " + e.getMessage());
        }
        return reportList;
    }

    private Report buildReportFromResultSet(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setReportID(rs.getInt("ReportID"));
        r.setResultValue(rs.getDouble("ResultValue"));
        r.setResultStatus(rs.getString("ResultStatus"));
        r.setAnalysisDate(rs.getString("AnalysisDate"));
        r.setDoctorNotes(rs.getString("DoctorNotes"));
        r.setTestRequestID(rs.getInt("TestRequestID"));
        r.setLabTechID(rs.getInt("LabTechID"));
        return r;
    }
}