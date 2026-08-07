package service;

import dao.*;
import model.*;
import java.util.*;

public class WorkloadManager {

    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();

    public Doctor findLeastBusyDoctor(int hospitalId, String department) {

        List<Doctor> doctors = doctorDAO.getAllDoctorsByHospital(hospitalId);

        List<Doctor> departmentDoctors = new ArrayList<>();

        for (Doctor d : doctors) {
            if (d.getDepartment().equalsIgnoreCase(department)) {
                departmentDoctors.add(d);
            }
        }

        if (departmentDoctors.isEmpty()) {
            System.out.println("No doctors available in this department.");
            return null;
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-10s %-25s %-18s%n",
                "Doctor Code", "Doctor Name", "Current Workload");
        System.out.println("------------------------------------------------------------");

        Doctor leastBusy = departmentDoctors.get(0);

        for (Doctor d : departmentDoctors) {

            System.out.printf("%-10d %-25s %-18d%n",
                    d.getDoctorCode(),
                    d.getName(),
                    d.getPatientCount());

            if (d.getPatientCount() < leastBusy.getPatientCount()) {
                leastBusy = d;
            }
        }

        System.out.println("------------------------------------------------------------");

        return leastBusy;
    }
    public Doctor suggestPreviousDoctor(String name, String dob, int hospitalId, String department) {
        Patient existingPatient = patientDAO.findReturningPatient(name, dob, hospitalId);
        if (existingPatient == null) {
            return null;
        }

        List<Admission> pastAdmissions = admissionDAO.getAdmissionsByPatient(existingPatient.getPatientID());
        if (pastAdmissions.isEmpty()) {
            return null;
        }

        Admission mostRecent = pastAdmissions.get(pastAdmissions.size() - 1);
        Doctor previousDoctor = doctorDAO.getDoctorById(mostRecent.getDoctorID());

        if (previousDoctor != null && previousDoctor.getDepartment().equalsIgnoreCase(department)) {
            return previousDoctor;
        }
        return null;
    }

    public Doctor assignDoctor(String name, String dob, int hospitalId, String department) {

        Doctor previousDoctor = suggestPreviousDoctor(name, dob, hospitalId, department);

        if (previousDoctor != null) {
            System.out.println("Returning patient detected.");
            System.out.println("Previous Doctor: " + previousDoctor.getName());
            return previousDoctor;
        }

        System.out.println("\nDepartment Selected : " + department);
        System.out.println("Searching available doctors...\n");

        Doctor leastBusy = findLeastBusyDoctor(hospitalId, department);

        if (leastBusy != null) {
            System.out.println("\nLeast Busy Doctor Selected:");
            System.out.println("Doctor : " + leastBusy.getName());
            System.out.println("Current Workload : " + leastBusy.getPatientCount());

            System.out.println("\nAssigning Doctor...");
        } else {
            System.out.println("No doctor available.");
        }

        return leastBusy;
    }
}