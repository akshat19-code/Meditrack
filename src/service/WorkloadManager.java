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

        Doctor leastBusy = departmentDoctors.get(0);
        for (Doctor d : departmentDoctors) {
            if (d.getPatientCount() < leastBusy.getPatientCount()) {
                leastBusy = d;
            }
        }
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
            System.out.println("Returning patient detected - suggesting previous doctor: " + previousDoctor.getName());
            return previousDoctor;
        }

        Doctor leastBusy = findLeastBusyDoctor(hospitalId, department);
        System.out.println("New patient - assign least busy doctor: " +
                (leastBusy != null ? leastBusy.getName() : "none available"));
        return leastBusy;
    }
}