package service;

import dao.*;
import model.*;
import java.util.List;

public class HealthScoreService {

    private ReportDAO reportDAO = new ReportDAO();

    public double calculateHealthScore(int patientId) {
        List<Report> reports = reportDAO.getReportsByPatient(patientId);

        if (reports.isEmpty()) {
            System.out.println("No report history available - default health score assigned.");
            return 100.0;
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
        return Math.round(healthScore * 100.0) / 100.0;
    }
}