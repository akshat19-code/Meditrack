package dao;

import database.DatabaseConnection;
import model.Admission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdmissionDAO {

    public boolean insertAdmission(Admission ad) {
        String query = "INSERT INTO Admission (AdmissionDate, DischargeDate, RoomNumber, RoomType, RoomCharge, " +
                "Status, PatientID, DoctorID, AdminID, HospitalID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, ad.getAdmissionDate());
            pstmt.setString(2, ad.getDischargeDate());
            pstmt.setString(3, ad.getRoomNumber());
            pstmt.setString(4, ad.getRoomType());
            pstmt.setDouble(5, ad.getRoomCharge());
            pstmt.setString(6, ad.getStatus());
            pstmt.setInt(7, ad.getPatientID());
            pstmt.setInt(8, ad.getDoctorID());
            pstmt.setInt(9, ad.getAdminID());
            pstmt.setInt(10, ad.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Admission: " + e.getMessage());
            return false;
        }
    }

    public Admission getAdmissionById(int admissionId) {
        String query = "SELECT * FROM Admission WHERE AdmissionID = ?";

        Connection con = DatabaseConnection.getConnection();

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

        Connection con = DatabaseConnection.getConnection();

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

        Connection con = DatabaseConnection.getConnection();

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

        Connection con = DatabaseConnection.getConnection();

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

    public boolean reassignAdminForAdmissions(int oldAdminId, int newAdminId) {
        String query = "UPDATE Admission SET AdminID = ? WHERE AdminID = ?";

        Connection con = DatabaseConnection.getConnection();

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

    private Admission buildAdmissionFromResultSet(ResultSet rs) throws SQLException {
        Admission ad = new Admission();
        ad.setAdmissionID(rs.getInt("AdmissionID"));
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