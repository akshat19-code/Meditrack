package service;

import dao.*;
import model.*;

import java.util.List;

public class HealthScoreService {

    private ReportDAO reportDAO = new ReportDAO();

    // Calculates a health score out of 100, based on the proportion of NORMAL
    // reports vs the patient's total report history. CRITICAL results weigh
    // more heavily against the score than ABNORMAL ones.
    public double calculateHealthScore(int patientId) {
        List<Report> reports = reportDAO.getReportsByPatient(patientId);

        if (reports.isEmpty()) {
            System.out.println("No report history available - default health score assigned.");
            return 100.0;   // no data yet, assume healthy
        }

        double totalScore = 0;
        for (Report r : reports) {
            switch (r.getResultStatus().toUpperCase()) {
                case "NORMAL" -> totalScore += 100;
                case "ABNORMAL" -> totalScore += 60;
                case "CRITICAL" -> totalScore += 20;
                default -> totalScore += 50;
            }
        }

        double healthScore = totalScore / reports.size();
        return Math.round(healthScore * 100.0) / 100.0;   // round to 2 decimal places
    }
}