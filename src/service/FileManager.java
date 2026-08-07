package service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileManager {

    private static final String BASE_DIR = "C:\\MediTrack";
    private static final String LOGIN_DIR = BASE_DIR + "\\Logs";
    private static final String PATIENTS_DIR = BASE_DIR + "\\Patients";
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ==================== Login Logs ====================

    public void logLoginAttempt(String role, String hospitalCode, String username, boolean success) {
        ensureFolder(LOGIN_DIR);

        String status = success ? "SUCCESS" : "FAILED";
        String hospitalPart = (hospitalCode == null) ? "N/A" : hospitalCode;

        String entry = "[" + getCurrentTimestamp() + "] " + status +
                " | Role: " + role +
                " | Username: " + username +
                " | Hospital Code: " + hospitalPart;

        String header = buildHeader("SYSTEM LOGIN LOG") + loginLogColumns();
        rewriteLogFile(LOGIN_DIR + "\\SystemLoginLog.txt", header, entry);

        if (hospitalCode != null) {
            String hospitalHeader = buildHeader("HOSPITAL LOGIN LOG - " + hospitalCode) + loginLogColumns();
            rewriteLogFile(LOGIN_DIR + "\\SystemLoginLog_" + hospitalCode + ".txt", hospitalHeader, entry);
        }
    }

    public String readSystemLoginLog() {
        return readFile(LOGIN_DIR + "\\SystemLoginLog.txt");
    }

    public String readHospitalLoginLog(String hospitalCode) {
        return readFile(LOGIN_DIR + "\\SystemLoginLog_" + hospitalCode + ".txt");
    }

    // ==================== Patient History ====================

    public void addAdmissionHistoryEntry(int patientId, String doctorName, String roomNumber,
                                         String roomType, String admissionDate) {
        String summary = "ADMITTED on " + admissionDate +
                " | Room: " + roomNumber + " (" + roomType + ")" +
                " | Doctor: " + doctorName;

        addPatientHistoryEntry(patientId, summary);
    }

    public void addDischargeHistoryEntry(int patientId, String dischargeDate, double totalBillAmount) {
        String summary = "DISCHARGED on " + dischargeDate +
                " | Final Bill: Rs." + String.format("%.2f", totalBillAmount);

        addPatientHistoryEntry(patientId, summary);
    }

    public void addReportHistoryEntry(int patientId, int reportId, String testName,
                                      String resultStatus, String analysisDate) {
        String summary = "REPORT ADDED on " + analysisDate +
                " | Test: " + testName +
                " | Status: " + resultStatus +
                " | See Report_" + reportId + ".txt for full details";

        addPatientHistoryEntry(patientId, summary);
    }

    public void addDiagnosisHistoryEntry(int patientId, int reportId, String doctorName) {
        String summary = "DIAGNOSIS ADDED on " + getCurrentDate() +
                " | Report ID: " + reportId +
                " | Reviewed by Dr. " + doctorName;

        addPatientHistoryEntry(patientId, summary);
    }

    public String readPatientHistory(int patientId) {
        return readFile(getPatientFilePath(patientId, "PatientHistory.txt"));
    }

    private void addPatientHistoryEntry(int patientId, String summary) {
        ensureFolder(PATIENTS_DIR + "\\Patient_" + patientId);

        String entry = "[" + getCurrentTimestamp() + "] " + summary;

        String columnHeader = "Timestamp              Event";
        String header = buildHeader("PATIENT HISTORY - PATIENT " + patientId)
                + columnHeader + "\n"
                + "-".repeat(columnHeader.length()) + "\n\n";

        rewriteLogFile(getPatientFilePath(patientId, "PatientHistory.txt"), header, entry);
    }

    // ==================== Report Files ====================

    public void writeReportFile(int patientId, int reportId, int testRequestId, String patientName,
                                String testName, double resultValue, String resultStatus,
                                String analysisDate, String labTechName) {
        ensureFolder(PATIENTS_DIR + "\\Patient_" + patientId);

        StringBuilder sb = new StringBuilder();
        writeTitle(sb, "PATIENT REPORT");
        sb.append("\n");
        writeField(sb, "Report ID", String.valueOf(reportId));
        writeField(sb, "Test Request ID", String.valueOf(testRequestId));
        writeField(sb, "Patient Name", patientName);
        writeField(sb, "Test Name", testName);
        writeField(sb, "Result Value", String.format("%.2f", resultValue));
        writeField(sb, "Result Status", resultStatus);
        writeField(sb, "Analysis Date", analysisDate);
        writeField(sb, "Lab Technician", labTechName);
        writeField(sb, "Doctor Notes", "(Pending review)");
        sb.append(border());

        writeFile(getPatientFilePath(patientId, "Report_" + reportId + ".txt"), sb.toString());
    }

    public void appendDiagnosisToReportFile(int patientId, int reportId, String doctorNotes) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(separator());
        sb.append("Diagnosis Notes Updated: ").append(getCurrentTimestamp()).append("\n");
        sb.append(separator());
        sb.append(doctorNotes).append("\n");

        appendFile(getPatientFilePath(patientId, "Report_" + reportId + ".txt"), sb.toString());
    }

    // ==================== Bill Files ====================

    public void writeBillFile(int patientId, int billId, int admissionId, String patientName,
                              String doctorName, String hospitalName, double roomCharge,
                              double doctorFee, double testCharge, double totalAmount, String billDate) {
        ensureFolder(PATIENTS_DIR + "\\Patient_" + patientId);

        StringBuilder sb = new StringBuilder();
        writeTitle(sb, "FINAL BILL");
        sb.append("\n");
        writeField(sb, "Bill ID", String.valueOf(billId));
        writeField(sb, "Admission ID", String.valueOf(admissionId));
        writeField(sb, "Hospital", hospitalName);
        writeField(sb, "Patient Name", patientName);
        writeField(sb, "Doctor", doctorName);
        sb.append(separator());
        writeField(sb, "Room Charge", "Rs." + String.format("%.2f", roomCharge));
        writeField(sb, "Doctor Fee", "Rs." + String.format("%.2f", doctorFee));
        writeField(sb, "Test Charge", "Rs." + String.format("%.2f", testCharge));
        sb.append(separator());
        writeField(sb, "TOTAL AMOUNT", "Rs." + String.format("%.2f", totalAmount));
        writeField(sb, "Bill Date", billDate);
        sb.append(border());

        writeFile(getPatientFilePath(patientId, "Bill_" + billId + ".txt"), sb.toString());
    }

    // ==================== Folder Helpers ====================

    private void ensureFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private String getPatientFilePath(int patientId, String fileName) {
        return PATIENTS_DIR + "\\Patient_" + patientId + "\\" + fileName;
    }

    // ==================== Formatting Helpers ====================

    private String border() {
        return "==============================================\n";
    }

    private String separator() {
        return "----------------------------------------------\n";
    }

    private void writeTitle(StringBuilder sb, String title) {
        int width = border().length() - 1;
        int padding = (width - title.length()) / 2;
        sb.append(border());
        sb.append(" ".repeat(padding));
        sb.append(title).append("\n");
        sb.append(border());
    }

    private void writeField(StringBuilder sb, String label, String value) {
        sb.append(String.format("%-20s", label)).append(": ").append(value).append("\n");
    }

    private String buildHeader(String title) {
        StringBuilder sb = new StringBuilder();
        writeTitle(sb, title);
        sb.append("\n");
        return sb.toString();
    }

    private String loginLogColumns() {
        String columnHeader = "Timestamp              Status     Role        Username        Hospital";
        return columnHeader + "\n" + "-".repeat(columnHeader.length()) + "\n\n";
    }

    private void rewriteLogFile(String path, String header, String newEntry) {
        String oldContent = readWholeFile(path);

        if (oldContent.startsWith(header)) {
            oldContent = oldContent.substring(header.length());
        }

        writeFile(path, header + newEntry + "\n" + oldContent);
    }

    // ==================== Low-level File Methods ====================

    private void writeFile(String path, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private void appendFile(String path, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error appending to file: " + e.getMessage());
        }
    }

    private String readWholeFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return "";
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

    private String readFile(String path) {
        String content = readWholeFile(path);
        return content.isEmpty() ? "No records found." : content;
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(formatter);
    }

    private String getCurrentDate() {
        return LocalDate.now().toString();
    }
}