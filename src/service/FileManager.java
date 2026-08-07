package service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class FileManager {

    public void logLoginAttempt(String role, String hospitalCode, String username, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        String hospitalPart = (hospitalCode == null) ? "N/A" : hospitalCode;

        String entry = "[" + getCurrentTimestamp() + "] " +
                status +
                " | Role: " + role +
                " | Username: " + username +
                " | Hospital Code: " + hospitalPart;

        prependToFile("SystemLoginLog.txt", entry);

        if (hospitalCode != null) {
            prependToFile("SystemLoginLog_" + hospitalCode + ".txt", entry);
        }
    }

    public String readSystemLoginLog() {
        return readFile("SystemLoginLog.txt");
    }

    public String readHospitalLoginLog(String hospitalCode) {
        return readFile("SystemLoginLog_" + hospitalCode + ".txt");
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
        return readFile("Patient_" + patientId + ".txt");
    }

    private void appendToPatientHistory(int patientId, String summary) {
        String entry = "[" + getCurrentTimestamp() + "] " + summary;
        prependToFile("Patient_" + patientId + ".txt", entry);
    }

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
        content.append("Result Value     : ").append(String.format("%.2f", resultValue)).append("\n");
        content.append("Result Status    : ").append(resultStatus).append("\n");
        content.append("Analysis Date    : ").append(analysisDate).append("\n");
        content.append("Lab Technician   : ").append(labTechName).append("\n");
        content.append("Doctor Notes     : (Pending review)\n");
        content.append("====================================\n");

        writeNewFile("Report_" + reportId + ".txt", content.toString());
    }

    public void appendDiagnosisToReportFile(int reportId, String doctorNotes) {
        String block = "\n------------------------------------\n" +
                "Diagnosis Notes Updated: " + getCurrentTimestamp() + "\n" +
                "------------------------------------\n" +
                doctorNotes + "\n";

        appendToFile("Report_" + reportId + ".txt", block);
    }

    public void writeBillFile(String billId, int admissionId, String patientName, String doctorName,
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

        writeNewFile("Bill_" + billId + ".txt", content.toString());
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
        File file = new File(fileName);

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