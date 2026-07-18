package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {

    public static void createTables() {
        Connection con = DatabaseConnection.getConnection();

        try {
            Statement stmt = con.createStatement();

            // 1. MasterAdmin
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MasterAdmin (" +
                    "MasterAdminID INT AUTO_INCREMENT, " +
                    "FirstName VARCHAR(50), " +
                    "LastName VARCHAR(50), " +
                    "Name VARCHAR(100), " +
                    "Username VARCHAR(50), " +
                    "Password VARCHAR(50), " +
                    "Email VARCHAR(100), " +
                    "PhoneNo VARCHAR(15), " +
                    "CONSTRAINT pk_masteradmin PRIMARY KEY (MasterAdminID)" +
                    ")");

            // 2. Hospital
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Hospital (" +
                    "HospitalID INT AUTO_INCREMENT, " +
                    "HospitalCode VARCHAR(20), " +
                    "HospitalName VARCHAR(150), " +
                    "Street VARCHAR(100), " +
                    "City VARCHAR(50), " +
                    "State VARCHAR(50), " +
                    "Pincode VARCHAR(10), " +
                    "PhoneNo VARCHAR(15), " +
                    "Email VARCHAR(100), " +
                    "Status VARCHAR(20), " +
                    "MasterAdminID INT, " +
                    "CONSTRAINT pk_hospital PRIMARY KEY (HospitalID), " +
                    "CONSTRAINT fk_hospital_masteradmin FOREIGN KEY (MasterAdminID) REFERENCES MasterAdmin(MasterAdminID)" +
                    ")");

            // 3. Admin
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Admin (" +
                    "AdminID INT AUTO_INCREMENT, " +
                    "FirstName VARCHAR(50), " +
                    "LastName VARCHAR(50), " +
                    "Name VARCHAR(100), " +
                    "Username VARCHAR(50), " +
                    "Password VARCHAR(50), " +
                    "Email VARCHAR(100), " +
                    "PhoneNo VARCHAR(15), " +
                    "HospitalID INT, " +
                    "CONSTRAINT pk_admin PRIMARY KEY (AdminID), " +
                    "CONSTRAINT fk_admin_hospital FOREIGN KEY (HospitalID) REFERENCES Hospital(HospitalID)" +
                    ")");

            // 4. Doctor
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Doctor (" +
                    "DoctorID INT AUTO_INCREMENT, " +
                    "FirstName VARCHAR(50), " +
                    "LastName VARCHAR(50), " +
                    "Name VARCHAR(100), " +
                    "Username VARCHAR(50), " +
                    "Password VARCHAR(50), " +
                    "Email VARCHAR(100), " +
                    "PhoneNo VARCHAR(15), " +
                    "Specialization VARCHAR(100), " +
                    "Department VARCHAR(100), " +
                    "Qualification VARCHAR(100), " +
                    "ConsultationFee DOUBLE, " +
                    "PatientCount INT DEFAULT 0, " +
                    "HospitalID INT, " +
                    "CONSTRAINT pk_doctor PRIMARY KEY (DoctorID), " +
                    "CONSTRAINT fk_doctor_hospital FOREIGN KEY (HospitalID) REFERENCES Hospital(HospitalID)" +
                    ")");

            // 5. LabTechnician
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS LabTechnician (" +
                    "LabTechID INT AUTO_INCREMENT, " +
                    "FirstName VARCHAR(50), " +
                    "LastName VARCHAR(50), " +
                    "Name VARCHAR(100), " +
                    "Username VARCHAR(50), " +
                    "Password VARCHAR(50), " +
                    "Email VARCHAR(100), " +
                    "PhoneNo VARCHAR(15), " +
                    "Qualification VARCHAR(100), " +
                    "HospitalID INT, " +
                    "CONSTRAINT pk_labtechnician PRIMARY KEY (LabTechID), " +
                    "CONSTRAINT fk_labtech_hospital FOREIGN KEY (HospitalID) REFERENCES Hospital(HospitalID)" +
                    ")");

            // 6. Patient
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Patient (" +
                    "PatientID INT AUTO_INCREMENT, " +
                    "FirstName VARCHAR(50), " +
                    "LastName VARCHAR(50), " +
                    "Name VARCHAR(100), " +
                    "Username VARCHAR(50), " +
                    "Password VARCHAR(50), " +
                    "Email VARCHAR(100), " +
                    "PhoneNo VARCHAR(15), " +
                    "DOB DATE, " +
                    "Gender VARCHAR(10), " +
                    "BloodGroup VARCHAR(5), " +
                    "Street VARCHAR(100), " +
                    "City VARCHAR(50), " +
                    "State VARCHAR(50), " +
                    "Pincode VARCHAR(10), " +
                    "FilePath VARCHAR(255), " +
                    "HospitalID INT, " +
                    "CONSTRAINT pk_patient PRIMARY KEY (PatientID), " +
                    "CONSTRAINT fk_patient_hospital FOREIGN KEY (HospitalID) REFERENCES Hospital(HospitalID)" +
                    ")");

            // 7. Equipment
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Equipment (" +
                    "EquipmentID INT AUTO_INCREMENT, " +
                    "EquipmentName VARCHAR(100), " +
                    "Status VARCHAR(30), " +
                    "PurchaseDate DATE, " +
                    "HospitalID INT, " +
                    "CONSTRAINT pk_equipment PRIMARY KEY (EquipmentID), " +
                    "CONSTRAINT fk_equipment_hospital FOREIGN KEY (HospitalID) REFERENCES Hospital(HospitalID)" +
                    ")");

            // 8. TestType
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TestType (" +
                    "TestTypeID INT AUTO_INCREMENT, " +
                    "TestName VARCHAR(100), " +
                    "NormalMin DOUBLE, " +
                    "NormalMax DOUBLE, " +
                    "Unit VARCHAR(20), " +
                    "TestCharge DOUBLE, " +
                    "HospitalID INT, " +
                    "EquipmentID INT, " +
                    "CONSTRAINT pk_testtype PRIMARY KEY (TestTypeID), " +
                    "CONSTRAINT fk_testtype_hospital FOREIGN KEY (HospitalID) REFERENCES Hospital(HospitalID), " +
                    "CONSTRAINT fk_testtype_equipment FOREIGN KEY (EquipmentID) REFERENCES Equipment(EquipmentID)" +
                    ")");

            // 9. Admission
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Admission (" +
                    "AdmissionID INT AUTO_INCREMENT, " +
                    "AdmissionDate DATE, " +
                    "DischargeDate DATE, " +
                    "RoomNumber VARCHAR(10), " +
                    "RoomType VARCHAR(20), " +
                    "RoomCharge DOUBLE, " +
                    "Status VARCHAR(20), " +
                    "PatientID INT, " +
                    "DoctorID INT, " +
                    "AdminID INT, " +
                    "HospitalID INT, " +
                    "CONSTRAINT pk_admission PRIMARY KEY (AdmissionID), " +
                    "CONSTRAINT fk_admission_patient FOREIGN KEY (PatientID) REFERENCES Patient(PatientID), " +
                    "CONSTRAINT fk_admission_doctor FOREIGN KEY (DoctorID) REFERENCES Doctor(DoctorID), " +
                    "CONSTRAINT fk_admission_admin FOREIGN KEY (AdminID) REFERENCES Admin(AdminID), " +
                    "CONSTRAINT fk_admission_hospital FOREIGN KEY (HospitalID) REFERENCES Hospital(HospitalID)" +
                    ")");

            // 10. TestRequest  (EquipmentUsageDate added)
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TestRequest (" +
                    "TestRequestID INT AUTO_INCREMENT, " +
                    "RequestDate DATE, " +
                    "EquipmentUsageDate DATE, " +
                    "Priority VARCHAR(20), " +
                    "Status VARCHAR(20), " +
                    "AdmissionID INT, " +
                    "DoctorID INT, " +
                    "TestTypeID INT, " +
                    "EquipmentID INT, " +
                    "CONSTRAINT pk_testrequest PRIMARY KEY (TestRequestID), " +
                    "CONSTRAINT fk_testrequest_admission FOREIGN KEY (AdmissionID) REFERENCES Admission(AdmissionID), " +
                    "CONSTRAINT fk_testrequest_doctor FOREIGN KEY (DoctorID) REFERENCES Doctor(DoctorID), " +
                    "CONSTRAINT fk_testrequest_testtype FOREIGN KEY (TestTypeID) REFERENCES TestType(TestTypeID), " +
                    "CONSTRAINT fk_testrequest_equipment FOREIGN KEY (EquipmentID) REFERENCES Equipment(EquipmentID)" +
                    ")");

            // 11. Report
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Report (" +
                    "ReportID INT AUTO_INCREMENT, " +
                    "ResultValue DOUBLE, " +
                    "ResultStatus VARCHAR(20), " +
                    "AnalysisDate DATE, " +
                    "DoctorNotes VARCHAR(2000), " +
                    "TestRequestID INT UNIQUE, " +
                    "LabTechID INT, " +
                    "CONSTRAINT pk_report PRIMARY KEY (ReportID), " +
                    "CONSTRAINT fk_report_testrequest FOREIGN KEY (TestRequestID) REFERENCES TestRequest(TestRequestID), " +
                    "CONSTRAINT fk_report_labtech FOREIGN KEY (LabTechID) REFERENCES LabTechnician(LabTechID)" +
                    ")");

            // 12. Bill
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Bill (" +
                    "BillID INT AUTO_INCREMENT, " +
                    "RoomCharge DOUBLE, " +
                    "DoctorFee DOUBLE, " +
                    "TestCharge DOUBLE, " +
                    "TotalAmount DOUBLE, " +
                    "BillDate DATE, " +
                    "AdmissionID INT UNIQUE, " +
                    "CONSTRAINT pk_bill PRIMARY KEY (BillID), " +
                    "CONSTRAINT fk_bill_admission FOREIGN KEY (AdmissionID) REFERENCES Admission(AdmissionID)" +
                    ")");

            System.out.println("All 12 tables created successfully (or already existed)!");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}