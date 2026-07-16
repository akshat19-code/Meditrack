package service;

import dao.*;
import model.*;

public class AuthService {

    private HospitalDAO hospitalDAO = new HospitalDAO();
    private MasterAdminDAO masterAdminDAO = new MasterAdminDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private LabTechnicianDAO labTechDAO = new LabTechnicianDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private FileManager fileManager = new FileManager();

    // Master Admin login - no Hospital Code needed
    public MasterAdmin masterAdminLogin(String username, String password) {
        MasterAdmin ma = masterAdminDAO.getMasterAdminByUsername(username);

        if (ma == null) {
            System.out.println("No such Master Admin found.");
            fileManager.logLoginAttempt("MASTER_ADMIN", null, username, false);
            return null;
        }
        if (!ma.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            fileManager.logLoginAttempt("MASTER_ADMIN", null, username, false);
            return null;
        }
        System.out.println("Master Admin login successful!");
        fileManager.logLoginAttempt("MASTER_ADMIN", null, username, true);
        return ma;
    }

    // Shared first step for every other role - validates the Hospital Code exists
    // and that the hospital is ACTIVE (not SUSPENDED/REMOVED)
    private Hospital validateHospital(String hospitalCode) {
        Hospital h = hospitalDAO.getHospitalByCode(hospitalCode);

        if (h == null) {
            System.out.println("Invalid Hospital Code.");
            return null;
        }
        if (!h.getStatus().equalsIgnoreCase("ACTIVE")) {
            System.out.println("This hospital's account is currently " + h.getStatus() + ".");
            return null;
        }
        return h;
    }

    // Hospital Admin login
    public Admin adminLogin(String hospitalCode, String username, String password) {
        Hospital h = validateHospital(hospitalCode);
        if (h == null) {
            fileManager.logLoginAttempt("ADMIN", hospitalCode, username, false);
            return null;
        }

        Admin a = adminDAO.getAdminByUsername(username, h.getHospitalID());
        if (a == null) {
            System.out.println("No such Admin found in this hospital.");
            fileManager.logLoginAttempt("ADMIN", hospitalCode, username, false);
            return null;
        }
        if (!a.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            fileManager.logLoginAttempt("ADMIN", hospitalCode, username, false);
            return null;
        }
        System.out.println("Admin login successful!");
        fileManager.logLoginAttempt("ADMIN", hospitalCode, username, true);
        return a;
    }

    // Doctor login
    public Doctor doctorLogin(String hospitalCode, String username, String password) {
        Hospital h = validateHospital(hospitalCode);
        if (h == null) {
            fileManager.logLoginAttempt("DOCTOR", hospitalCode, username, false);
            return null;
        }

        Doctor d = doctorDAO.getDoctorByUsername(username, h.getHospitalID());
        if (d == null) {
            System.out.println("No such Doctor found in this hospital.");
            fileManager.logLoginAttempt("DOCTOR", hospitalCode, username, false);
            return null;
        }
        if (!d.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            fileManager.logLoginAttempt("DOCTOR", hospitalCode, username, false);
            return null;
        }
        System.out.println("Doctor login successful!");
        fileManager.logLoginAttempt("DOCTOR", hospitalCode, username, true);
        return d;
    }

    // Lab Technician login
    public LabTechnician labTechnicianLogin(String hospitalCode, String username, String password) {
        Hospital h = validateHospital(hospitalCode);
        if (h == null) {
            fileManager.logLoginAttempt("LAB_TECHNICIAN", hospitalCode, username, false);
            return null;
        }

        LabTechnician lt = labTechDAO.getLabTechnicianByUsername(username, h.getHospitalID());
        if (lt == null) {
            System.out.println("No such Lab Technician found in this hospital.");
            fileManager.logLoginAttempt("LAB_TECHNICIAN", hospitalCode, username, false);
            return null;
        }
        if (!lt.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            fileManager.logLoginAttempt("LAB_TECHNICIAN", hospitalCode, username, false);
            return null;
        }
        System.out.println("Lab Technician login successful!");
        fileManager.logLoginAttempt("LAB_TECHNICIAN", hospitalCode, username, true);
        return lt;
    }

    // Patient login
    public Patient patientLogin(String hospitalCode, String username, String password) {
        Hospital h = validateHospital(hospitalCode);
        if (h == null) {
            fileManager.logLoginAttempt("PATIENT", hospitalCode, username, false);
            return null;
        }

        Patient p = patientDAO.getPatientByUsername(username, h.getHospitalID());
        if (p == null) {
            System.out.println("No such Patient found in this hospital.");
            fileManager.logLoginAttempt("PATIENT", hospitalCode, username, false);
            return null;
        }
        if (!p.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            fileManager.logLoginAttempt("PATIENT", hospitalCode, username, false);
            return null;
        }
        System.out.println("Patient login successful!");
        fileManager.logLoginAttempt("PATIENT", hospitalCode, username, true);
        return p;
    }
}