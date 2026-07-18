package service;

import dao.*;
import database.DatabaseConnection;
import model.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class BillingService {

    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private BillDAO billDAO = new BillDAO();
    private FileManager fileManager = new FileManager();

    // Called when Admin discharges a patient. Delegates the entire discharge +
    // bill generation flow to the GenerateBillAndDischarge stored procedure,
    // which internally uses CalculateBill(AdmissionID), inserts the Bill row,
    // updates Admission.Status/DischargeDate (both using CURDATE()), and wraps
    // it all in a transaction.
    //
    // Patient/Doctor/Hospital names needed for the bill file are fetched via
    // PatientSummaryView in a single query, instead of the old chain of
    // getAdmissionById() -> getDoctorById() -> getPatientById() -> getHospitalById().
    public boolean dischargeAndGenerateBill(int admissionId) {

        String[] summary = admissionDAO.getPatientSummaryByAdmissionId(admissionId);
        if (summary == null) {
            System.out.println("Admission or associated Patient/Doctor/Hospital details not found.");
            return false;
        }

        int patientId = Integer.parseInt(summary[0]);
        String patientName = summary[1];
        String doctorName = summary[2];
        String hospitalName = summary[3];

        Connection con = DatabaseConnection.getConnection();

        try (CallableStatement cstmt = con.prepareCall("{call GenerateBillAndDischarge(?)}")) {
            cstmt.setInt(1, admissionId);
            cstmt.execute();
        } catch (SQLException e) {
            System.out.println("Error generating bill and discharging patient: " + e.getMessage());
            return false;
        }

        // ---- Fetch the Bill the procedure just inserted, so we can write the file ----
        Bill savedBill = billDAO.getBillByAdmissionId(admissionId);
        if (savedBill == null) {
            System.out.println("Failed to retrieve generated bill.");
            return false;
        }

        fileManager.writeBillFile(savedBill.getBillID(), admissionId, patientName, doctorName,
                hospitalName, savedBill.getRoomCharge(), savedBill.getDoctorFee(),
                savedBill.getTestCharge(), savedBill.getTotalAmount(), savedBill.getBillDate());
        fileManager.addDischargeHistoryEntry(patientId, savedBill.getBillDate(), savedBill.getTotalAmount());

        System.out.println("Patient discharged. Total Bill: Rs." + String.format("%.2f", savedBill.getTotalAmount()));
        return true;
    }
}