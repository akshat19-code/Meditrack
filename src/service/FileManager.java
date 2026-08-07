package service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class FileManager {

    private static final String BASE_FOLDER = "C:\\MediTrack";
    private static final String LOGS_FOLDER = BASE_FOLDER + "\\Logs";
    private static final String PATIENTS_FOLDER = BASE_FOLDER + "\\Patients";
    private static final String LINE = "=".repeat(90) + "\n";
    private static final String DASH = "-".repeat(90) + "\n";
    private static final String SYSTEM_LOG_HEADER =
            LINE +
                    "SYSTEM LOGIN LOG\n" +
                    LINE +
                    String.format("%-20s%-11s%-18s%-22s%s",
                            "Timestamp", "Status", "Role", "Username", "Hospital Code") + "\n" +
                    DASH;

    private static String getHospitalLogHeader(String hospitalCode) {
        return LINE +
                "HOSPITAL LOGIN LOG - " + hospitalCode + "\n" +
                LINE +
                String.format("%-20s%-11s%-18s%-22s%s",
                        "Timestamp", "Status", "Role", "Username", "Hospital Code") + "\n" +
                DASH;
    }
    public FileManager() {
        File base = new File(BASE_FOLDER);
        if (!base.exists()) {
            base.mkdirs();
        }
        File logs = new File(LOGS_FOLDER);
        if (!logs.exists()) {
            logs.mkdirs();
        }
        File patients = new File(PATIENTS_FOLDER);
        if (!patients.exists()) {
            patients.mkdirs();
        }
    }

    public void logLoginAttempt(String role, String hospitalCode, String username, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        String hospitalPart = (hospitalCode == null) ? "N/A" : hospitalCode;

        String row = String.format("%-20s%-11s%-18s%-22s%s",
                getCurrentTimestamp(), status, role, username, hospitalPart);

        prependLoginEntry(LOGS_FOLDER + "\\SystemLoginLog.txt", SYSTEM_LOG_HEADER, row);

        if (hospitalCode != null) {
            prependLoginEntry(LOGS_FOLDER + "\\SystemLoginLog_" + hospitalCode + ".txt",
                    getHospitalLogHeader(hospitalCode), row);
        }
    }

    private void prependLoginEntry(String fileName, String header, String newRow) {
        File file = new File(fileName);
        StringBuilder oldData = new StringBuilder();

        int headerLineCount = countLines(header);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (lineNumber > headerLineCount) {
                        oldData.append(line).append("\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(header);
            writer.write(newRow);
            writer.newLine();
            writer.write(oldData.toString());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private int countLines(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }

    public String readSystemLoginLog() {
        return readFile(LOGS_FOLDER + "\\SystemLoginLog.txt");
    }

    public String readHospitalLoginLog(String hospitalCode) {
        return readFile(LOGS_FOLDER + "\\SystemLoginLog_" + hospitalCode + ".txt");
    }

    public void addAdmissionHistoryEntry(int patientId, String doctorName, String roomNumber,
                                         String roomType, String admissionDate) {
        String summary = "ADMITTED on " + admissionDate +
                " | Room: " + roomNumber + " (" + roomType + ")" +
                " | Doctor: " + doctorName;

        appendToPatientHistory(patientId, summary);
    }

    public void addDischargeHistoryEntry(int patientId, String dischargeDate, double totalBillAmount) {
        String summary = "DISCHARGED on " + dischargeDate +
                " | Final Bill: Rs." + String.format("%.2f", totalBillAmount);

        appendToPatientHistory(patientId, summary);
    }

    public void addReportHistoryEntry(int patientId, int reportId, String testName,
                                      String resultStatus, String analysisDate) {
        String summary = "REPORT ADDED on " + analysisDate +
                " | Test: " + testName +
                " | Status: " + resultStatus +
                " | See Report_" + reportId + ".txt for full details";

        appendToPatientHistory(patientId, summary);
    }

    public void addDiagnosisHistoryEntry(int patientId, int reportId, String doctorName) {
        String summary = "DIAGNOSIS ADDED on " + getCurrentDate() +
                " | Report ID: " + reportId +
                " | Reviewed by Dr. " + doctorName;

        appendToPatientHistory(patientId, summary);
    }

    public String readPatientHistory(int patientId) {
        String folderPath = getPatientFolder(patientId);
        return readFile(folderPath + "\\PatientHistory.txt");
    }

    private void appendToPatientHistory(int patientId, String summary) {
        String entry = "[" + getCurrentTimestamp() + "] " + summary;
        String folderPath = getPatientFolder(patientId);
        prependToFile(folderPath + "\\PatientHistory.txt", entry);
    }

    public void writeReportFile(int patientId, int reportId, int testRequestId, String patientName,
                                String testName, double resultValue, String resultStatus,
                                String analysisDate, String labTechName) {
        StringBuilder content = new StringBuilder();
        content.append("====================================\n");
        content.append("              LAB REPORT\n");
        content.append("====================================\n");
        content.append("Report ID        : ").append(reportId).append("\n");
        content.append("Test Request ID  : ").append(testRequestId).append("\n");
        content.append("Patient Name     : ").append(patientName).append("\n");
        content.append("Test Name        : ").append(testName).append("\n");
        content.append("Result Value     : ").append(String.format("%.2f", resultValue)).append("\n");
        content.append("Result Status    : ").append(resultStatus).append("\n");
        content.append("Analysis Date    : ").append(analysisDate).append("\n");
        content.append("Lab Technician   : ").append(labTechName).append("\n");
        content.append("Doctor Notes     : (Pending review)\n");
        content.append("====================================\n");

        String folderPath = getPatientFolder(patientId);
        writeNewFile(folderPath + "\\Report_" + reportId + ".txt", content.toString());
    }

    public void appendDiagnosisToReportFile(int patientId, int reportId, String doctorNotes) {
        String block = "\n------------------------------------\n" +
                "Diagnosis Notes Updated: " + getCurrentTimestamp() + "\n" +
                "------------------------------------\n" +
                doctorNotes + "\n";

        String folderPath = getPatientFolder(patientId);
        appendToFile(folderPath + "\\Report_" + reportId + ".txt", block);
    }

    public void writeBillFile(int patientId, int billId, int admissionId, String patientName, String doctorName,
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
        content.append("Room Charge      : Rs.").append(String.format("%.2f", roomCharge)).append("\n");
        content.append("Doctor Fee       : Rs.").append(String.format("%.2f", doctorFee)).append("\n");
        content.append("Test Charge      : Rs.").append(String.format("%.2f", testCharge)).append("\n");
        content.append("------------------------------------\n");
        content.append("TOTAL AMOUNT     : Rs.").append(String.format("%.2f", totalAmount)).append("\n");
        content.append("Bill Date        : ").append(billDate).append("\n");
        content.append("====================================\n");

        String folderPath = getPatientFolder(patientId);
        writeNewFile(folderPath + "\\Bill_" + billId + ".txt", content.toString());
    }

    private String getPatientFolder(int patientId) {
        String folderPath = PATIENTS_FOLDER + "\\Patient_" + patientId;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath;
    }

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

    private void appendToFile(String fileName, String newContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(newContent);
        } catch (IOException e) {
            System.out.println("Error appending to file: " + e.getMessage());
        }
    }

    private void writeNewFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

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

    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    private String getCurrentDate() {
        return LocalDate.now().toString();
    }
}