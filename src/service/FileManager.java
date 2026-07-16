package service;

import java.io.*;

public class FileManager {

    // ================================================================
    // SYSTEM LOGIN LOG  (SystemLoginLog.txt)
    // Single shared file for every role's login attempts, newest on top.
    // ================================================================

    // Logs one login attempt (success or failure) for ANY role into SystemLoginLog.txt.
    // CALL THIS FROM: AuthService.java - at the end of masterAdminLogin(), adminLogin(),
    // doctorLogin(), labTechnicianLogin(), and patientLogin() - once for the success
    // path and once for every failure path (wrong password, hospital not found, etc.).
    // For Master Admin, pass hospitalCode as null (there is no Hospital Code for that role).
    public void logLoginAttempt(String role, String hospitalCode, String username, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        String hospitalPart = (hospitalCode == null) ? "N/A" : hospitalCode;

        String entry = "[" + getCurrentTimestamp() + "] " +
                status +
                " | Role: " + role +
                " | Username: " + username +
                " | Hospital Code: " + hospitalPart;

        prependToFile("SystemLoginLog.txt", entry);
    }

    // Reads the full SystemLoginLog.txt for display (e.g. an Admin/Master Admin audit screen).
    // CALL THIS FROM: MasterAdminMenu.java or AdminMenu.java, if a "View Login Log" option is added.
    public String readSystemLoginLog() {
        return readFile("SystemLoginLog.txt");
    }

    // ================================================================
    // PATIENT HISTORY  (Patient_<PatientID>.txt)
    // Acts as a timeline - every entry is a SHORT summary line only.
    // Newest entry always goes on top. Full details live in the
    // separate Report_<ID>.txt / Bill_<ID>.txt files.
    // ================================================================

    // Adds a short "admitted" summary to the patient's history file.
    // CALL THIS FROM: AdminMenu.registerPatientAndAdmit() - right after
    // admissionDAO.insertAdmission() returns true.
    public void addAdmissionHistoryEntry(int patientId, String doctorName, String roomNumber,
                                         String roomType, String admissionDate) {
        String summary = "ADMITTED on " + admissionDate +
                " | Room: " + roomNumber + " (" + roomType + ")" +
                " | Doctor: " + doctorName;

        appendToPatientHistory(patientId, summary);
    }

    // Adds a short "discharged" summary to the patient's history file.
    // CALL THIS FROM: BillingService.dischargeAndGenerateBill() - right after
    // both the Bill insert and the Admission status update succeed.
    public void addDischargeHistoryEntry(int patientId, String dischargeDate, double totalBillAmount) {
        String summary = "DISCHARGED on " + dischargeDate +
                " | Final Bill: Rs." + totalBillAmount;

        appendToPatientHistory(patientId, summary);
    }

    // Adds a short "new report" summary to the patient's history file, pointing to the
    // full Report_<ID>.txt for details.
    // CALL THIS FROM: ReportAnalyser.generateReport() - right after reportDAO.insertReport()
    // returns true (you will need the PatientID from the Admission tied to the TestRequest).
    public void addReportHistoryEntry(int patientId, int reportId, String testName,
                                      String resultStatus, String analysisDate) {
        String summary = "REPORT ADDED on " + analysisDate +
                " | Test: " + testName +
                " | Status: " + resultStatus +
                " | See Report_" + reportId + ".txt for full details";

        appendToPatientHistory(patientId, summary);
    }

    // Adds a short "diagnosis reviewed" summary to the patient's history file.
    // CALL THIS FROM: DoctorMenu.reviewReport() - right after reportDAO.updateDoctorNotes()
    // returns true (you will need the PatientID from the Report -> TestRequest -> Admission chain).
    public void addDiagnosisHistoryEntry(int patientId, int reportId, String doctorName) {
        String summary = "DIAGNOSIS ADDED on " + getCurrentDate() +
                " | Report ID: " + reportId +
                " | Reviewed by Dr. " + doctorName;

        appendToPatientHistory(patientId, summary);
    }

    // Reads a patient's full history timeline for display.
    // CALL THIS FROM: PatientMenu.viewMyDetails() or a new "View My History" option.
    public String readPatientHistory(int patientId) {
        return readFile("Patient_" + patientId + ".txt");
    }

    // Shared helper behind all four addXHistoryEntry() methods above - every history
    // entry is timestamped and placed at the top of the patient's file the same way.
    private void appendToPatientHistory(int patientId, String summary) {
        String entry = "[" + getCurrentTimestamp() + "] " + summary;
        prependToFile("Patient_" + patientId + ".txt", entry);
    }

    // ================================================================
    // REPORT FILE  (Report_<ReportID>.txt)
    // Holds the COMPLETE formatted report - not a timeline, one file per report.
    // ================================================================

    // Writes the full, formatted lab report to Report_<ReportID>.txt.
    // CALL THIS FROM: ReportAnalyser.generateReport() - right after reportDAO.insertReport()
    // returns true. Pass the same Report object that was just inserted, plus the
    // patient/test/lab-tech names looked up by the caller (this class does no DB lookups).
    public void writeReportFile(int reportId, int testRequestId, String patientName, String testName,
                                double resultValue, String resultStatus, String analysisDate,
                                String labTechName) {
        StringBuilder content = new StringBuilder();
        content.append("====================================\n");
        content.append("              LAB REPORT\n");
        content.append("====================================\n");
        content.append("Report ID        : ").append(reportId).append("\n");
        content.append("Test Request ID  : ").append(testRequestId).append("\n");
        content.append("Patient Name     : ").append(patientName).append("\n");
        content.append("Test Name        : ").append(testName).append("\n");
        content.append("Result Value     : ").append(resultValue).append("\n");
        content.append("Result Status    : ").append(resultStatus).append("\n");
        content.append("Analysis Date    : ").append(analysisDate).append("\n");
        content.append("Lab Technician   : ").append(labTechName).append("\n");
        content.append("Doctor Notes     : (Pending review)\n");
        content.append("====================================\n");

        writeNewFile("Report_" + reportId + ".txt", content.toString());
    }

    // Appends the doctor's diagnosis notes to the bottom of an existing Report_<ID>.txt.
    // CALL THIS FROM: DoctorMenu.reviewReport() - right after reportDAO.updateDoctorNotes()
    // returns true.
    public void appendDiagnosisToReportFile(int reportId, String doctorNotes) {
        String block = "\n------------------------------------\n" +
                "Diagnosis Notes Updated: " + getCurrentTimestamp() + "\n" +
                "------------------------------------\n" +
                doctorNotes + "\n";

        appendToFile("Report_" + reportId + ".txt", block);
    }

    // Reads a single report file for display.
    // CALL THIS FROM: DoctorMenu.reviewReport() or PatientMenu.viewReportHistory(),
    // if you want to show the full report text instead of just the DB fields.
    public String readReportFile(int reportId) {
        return readFile("Report_" + reportId + ".txt");
    }

    // ================================================================
    // BILL FILE  (Bill_<BillID>.txt)
    // Holds the COMPLETE formatted bill - one file per bill.
    // ================================================================

    // Writes the full, formatted bill to Bill_<BillID>.txt.
    // CALL THIS FROM: BillingService.dischargeAndGenerateBill() - right after
    // billDAO.insertBill() returns true. Pass the same Bill object just inserted,
    // plus patient/doctor/hospital names looked up by the caller.
    public void writeBillFile(int billId, int admissionId, String patientName, String doctorName,
                              String hospitalName, double roomCharge, double doctorFee,
                              double testCharge, double totalAmount, String billDate) {
        StringBuilder content = new StringBuilder();
        content.append("====================================\n");
        content.append("               FINAL BILL\n");
        content.append("====================================\n");
        content.append("Bill ID          : ").append(billId).append("\n");
        content.append("Admission ID     : ").append(admissionId).append("\n");
        content.append("Hospital         : ").append(hospitalName).append("\n");
        content.append("Patient Name     : ").append(patientName).append("\n");
        content.append("Doctor           : ").append(doctorName).append("\n");
        content.append("------------------------------------\n");
        content.append("Room Charge      : Rs.").append(roomCharge).append("\n");
        content.append("Doctor Fee       : Rs.").append(doctorFee).append("\n");
        content.append("Test Charge      : Rs.").append(testCharge).append("\n");
        content.append("------------------------------------\n");
        content.append("TOTAL AMOUNT     : Rs.").append(totalAmount).append("\n");
        content.append("Bill Date        : ").append(billDate).append("\n");
        content.append("====================================\n");

        writeNewFile("Bill_" + billId + ".txt", content.toString());
    }

    // Reads a single bill file for display.
    // CALL THIS FROM: PatientMenu.viewAdmissionAndBill(), if you want to show the
    // full bill text instead of just the DB fields.
    public String readBillFile(int billId) {
        return readFile("Bill_" + billId + ".txt");
    }

    // ================================================================
    // PRIVATE FILE I/O HELPERS
    // Every public method above goes through one of these three -
    // keeps the actual File/FileReader/FileWriter logic in one place.
    // ================================================================

    // Puts newEntry at the very TOP of the file, followed by whatever was already there.
    // Used for timeline-style files: Patient history and the System Login Log.
    private void prependToFile(String fileName, String newEntry) {
        File file = new File(fileName);
        StringBuilder oldContent = new StringBuilder();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    oldContent.append(line).append("\n");
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(newEntry);
            writer.newLine();
            writer.write(oldContent.toString());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Adds newContent at the BOTTOM of the file, after whatever was already there.
    // Used when completing an existing document, e.g. adding diagnosis notes
    // onto the bottom of an already-written Report file.
    private void appendToFile(String fileName, String newContent) {
        File file = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(newContent);
        } catch (IOException e) {
            System.out.println("Error appending to file: " + e.getMessage());
        }
    }

    // Creates (or completely overwrites) a file with the given content.
    // Used for Report and Bill files, which are a single complete record,
    // not a running timeline.
    private void writeNewFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Reads and returns the full content of any of the above files, for display.
    private String readFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            return "No records found.";
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return content.toString();
    }

    // Returns the current date + time as "yyyy-MM-dd HH:mm:ss", used to timestamp
    // every history entry and every login log entry.
    private String getCurrentTimestamp() {
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return java.time.LocalDateTime.now().format(formatter);
    }

    // Returns just the current date - used where only a date (not a full timestamp) is needed.
    private String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }
}