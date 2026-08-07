package dao;

import database.DatabaseConnection;
import model.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    Connection con = DatabaseConnection.getConnection();

    public boolean insertReport(Report r) {
        String query = "INSERT INTO Report (ReportCode, ResultValue, ResultStatus, AnalysisDate, DoctorNotes, TestRequestID, LabTechID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, r.getReportCode());
            pstmt.setDouble(2, r.getResultValue());
            pstmt.setString(3, r.getResultStatus());
            pstmt.setString(4, r.getAnalysisDate());
            pstmt.setString(5, r.getDoctorNotes());
            pstmt.setInt(6, r.getTestRequestID());
            pstmt.setInt(7, r.getLabTechID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Report: " + e.getMessage());
            return false;
        }
    }

    public Report getReportById(int reportId) {
        String query = "SELECT * FROM Report WHERE ReportID = ?";

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

    public List<Report> getReportsByDoctor(int doctorId) {
        List<Report> reportList = new ArrayList<>();
        String query = "SELECT r.* FROM Report r " +
                "JOIN TestRequest tr ON r.TestRequestID = tr.TestRequestID " +
                "JOIN Admission a ON tr.AdmissionID = a.AdmissionID " +
                "WHERE a.DoctorID = ? " +
                "ORDER BY r.AnalysisDate DESC";

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

    public String generateReportCode(int hospitalID){
        String query = "SELECT COUNT(*) FROM Report WHERE HospitalID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("RPT%03d", nextId);
            }

        } catch (SQLException e) {
            System.out.println("Error generating Report Code: " + e.getMessage());
        }

        return null;
    }

    private Report buildReportFromResultSet(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setReportID(rs.getInt("ReportID"));
        r.setReportCode(rs.getString("ReportCode"));
        r.setResultValue(rs.getDouble("ResultValue"));
        r.setResultStatus(rs.getString("ResultStatus"));
        r.setAnalysisDate(rs.getString("AnalysisDate"));
        r.setDoctorNotes(rs.getString("DoctorNotes"));
        r.setTestRequestID(rs.getInt("TestRequestID"));
        r.setLabTechID(rs.getInt("LabTechID"));
        return r;
    }
}