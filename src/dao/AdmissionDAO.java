package dao;

import database.DatabaseConnection;
import model.Admission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdmissionDAO {

    Connection con = DatabaseConnection.getConnection();

    public boolean insertAdmission(Admission ad) {
        String query = "INSERT INTO Admission (AdmissionCode, AdmissionDate, DischargeDate, RoomNumber, RoomType, RoomCharge, " +
                "Status, PatientID, DoctorID, AdminID, HospitalID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, ad.getAdmissionCode());
            pstmt.setString(2, ad.getAdmissionDate());
            pstmt.setString(3, ad.getDischargeDate());
            pstmt.setString(4, ad.getRoomNumber());
            pstmt.setString(5, ad.getRoomType());
            pstmt.setDouble(6, ad.getRoomCharge());
            pstmt.setString(7, ad.getStatus());
            pstmt.setInt(8, ad.getPatientID());
            pstmt.setInt(9, ad.getDoctorID());
            pstmt.setInt(10, ad.getAdminID());
            pstmt.setInt(11, ad.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Admission: " + e.getMessage());
            return false;
        }
    }

    public Admission getAdmissionById(int admissionId) {
        String query = "SELECT * FROM Admission WHERE AdmissionID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, admissionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildAdmissionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Admission: " + e.getMessage());
        }
        return null;
    }

    public List<Admission> getActiveAdmissionsByDoctor(int doctorId) {
        List<Admission> admissionList = new ArrayList<>();
        String query = "SELECT * FROM Admission WHERE DoctorID = ? AND Status = 'ADMITTED'";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                admissionList.add(buildAdmissionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Admissions: " + e.getMessage());
        }
        return admissionList;
    }

    public List<Admission> getAdmissionsByPatient(int patientId) {
        List<Admission> admissionList = new ArrayList<>();
        String query = "SELECT * FROM Admission WHERE PatientID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                admissionList.add(buildAdmissionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Admissions: " + e.getMessage());
        }
        return admissionList;
    }

    public String[] getPatientSummaryByAdmissionId(int admissionId) {
        String query = "SELECT PatientID, PatientName, DoctorName, HospitalName " +
                "FROM PatientSummaryView WHERE AdmissionID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, admissionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new String[] {
                        String.valueOf(rs.getInt("PatientID")),
                        rs.getString("PatientName"),
                        rs.getString("DoctorName"),
                        rs.getString("HospitalName")
                };
            }

        } catch (SQLException e) {
            System.out.println("Error fetching PatientSummaryView by AdmissionID: " + e.getMessage());
        }
        return null;
    }

    public List<String> getActivePatientSummariesByDoctor(int doctorId) {
        List<String> summaries = new ArrayList<>();
        String query = "SELECT AdmissionID, PatientName, RoomNumber, Status " +
                "FROM PatientSummaryView WHERE DoctorID = ? AND Status = 'ADMITTED'";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String line = "Admission ID: " + rs.getInt("AdmissionID") +
                        " | Patient: " + rs.getString("PatientName") +
                        " | Room: " + rs.getString("RoomNumber") +
                        " | Status: " + rs.getString("Status");
                summaries.add(line);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching patient summaries for Doctor: " + e.getMessage());
        }
        return summaries;
    }

    public boolean reassignAdminForAdmissions(int oldAdminId, int newAdminId) {
        String query = "UPDATE Admission SET AdminID = ? WHERE AdminID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, newAdminId);
            pstmt.setInt(2, oldAdminId);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error reassigning Admissions: " + e.getMessage());
            return false;
        }
    }

    public String generateAdmissionCode(int hospitalID){
        String query = "SELECT COUNT(*) FROM Admission WHERE HospitalID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("ADM%03d", nextId);
            }

        } catch (SQLException e) {
            System.out.println("Error generating Admission Code: " + e.getMessage());
        }

        return null;
    }

    private Admission buildAdmissionFromResultSet(ResultSet rs) throws SQLException {
        Admission ad = new Admission();
        ad.setAdmissionID(rs.getInt("AdmissionID"));
        ad.setAdmissionCode(rs.getString("AdmissionCode"));
        ad.setAdmissionDate(rs.getString("AdmissionDate"));
        ad.setDischargeDate(rs.getString("DischargeDate"));
        ad.setRoomNumber(rs.getString("RoomNumber"));
        ad.setRoomType(rs.getString("RoomType"));
        ad.setRoomCharge(rs.getDouble("RoomCharge"));
        ad.setStatus(rs.getString("Status"));
        ad.setPatientID(rs.getInt("PatientID"));
        ad.setDoctorID(rs.getInt("DoctorID"));
        ad.setAdminID(rs.getInt("AdminID"));
        ad.setHospitalID(rs.getInt("HospitalID"));
        return ad;
    }
}