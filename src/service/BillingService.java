package service;

import dao.*;
import model.*;

import java.util.List;

public class BillingService {

    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private TestRequestDAO testRequestDAO = new TestRequestDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();
    private BillDAO billDAO = new BillDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private FileManager fileManager = new FileManager();

    // Called when Admin discharges a patient. Marks the Admission DISCHARGED,
    // then calculates and inserts the final Bill (RoomCharge + DoctorFee + sum of TestCharges).
    public boolean dischargeAndGenerateBill(int admissionId, String dischargeDate) {
        Admission ad = admissionDAO.getAdmissionById(admissionId);
        if (ad == null) {
            System.out.println("Admission not found.");
            return false;
        }

        Doctor d = doctorDAO.getDoctorById(ad.getDoctorID());
        if (d == null) {
            System.out.println("Doctor not found.");
            return false;
        }

        // Sum up TestCharge for every TestRequest under this Admission
        double totalTestCharge = 0;
        List<TestRequest> requests = testRequestDAO.getTestRequestsByAdmission(admissionId);
        for (TestRequest tr : requests) {
            TestType tt = testTypeDAO.getTestTypeById(tr.getTestTypeID());
            if (tt != null) {
                totalTestCharge += tt.getTestCharge();
            }
        }

        double roomCharge = ad.getRoomCharge();
        double doctorFee = d.getConsultationFee();
        double totalAmount = roomCharge + doctorFee + totalTestCharge;

        Bill b = new Bill();
        b.setRoomCharge(roomCharge);
        b.setDoctorFee(doctorFee);
        b.setTestCharge(totalTestCharge);
        b.setTotalAmount(totalAmount);
        b.setBillDate(dischargeDate);
        b.setAdmissionID(admissionId);

        boolean billInserted = billDAO.insertBill(b);
        if (!billInserted) {
            System.out.println("Failed to generate bill.");
            return false;
        }

        boolean discharged = admissionDAO.dischargePatient(admissionId, dischargeDate);
        if (!discharged) {
            System.out.println("Failed to update admission status.");
            return false;
        }

        // ---- File writing: fetch the row back to get its auto-generated BillID ----
        Bill savedBill = billDAO.getBillByAdmissionId(admissionId);
        Patient p = patientDAO.getPatientById(ad.getPatientID());
        Hospital h = hospitalDAO.getHospitalById(ad.getHospitalID());

        if (savedBill != null && p != null && h != null) {
            fileManager.writeBillFile(savedBill.getBillID(), admissionId, p.getName(), d.getName(),
                    h.getHospitalName(), roomCharge, doctorFee, totalTestCharge, totalAmount, dischargeDate);
            fileManager.addDischargeHistoryEntry(p.getPatientID(), dischargeDate, totalAmount);
        }

        System.out.println("Patient discharged. Total Bill: Rs." + totalAmount);
        return true;
    }
}