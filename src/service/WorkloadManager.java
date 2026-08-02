package service;

import dao.*;
import model.*;
import java.util.*;

public class WorkloadManager {

    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();

    public Doctor findLeastBusyDoctor(int hospitalId) {
        List<Doctor> doctors = doctorDAO.getAllDoctorsByHospital(hospitalId);

        if (doctors.isEmpty()) {
            System.out.println("No doctors available in this hospital.");
            return null;
        }

        Doctor leastBusy = doctors.get(0);
        for (Doctor d : doctors) {
            if (d.getPatientCount() < leastBusy.getPatientCount()) {
                leastBusy = d;
            }
        }
        return leastBusy;
    }

    public Doctor suggestPreviousDoctor(String name, String dob, int hospitalId) {
        Patient existingPatient = patientDAO.findReturningPatient(name, dob, hospitalId);
        if (existingPatient == null) {
            return null;
        }

        List<Admission> pastAdmissions = admissionDAO.getAdmissionsByPatient(existingPatient.getPatientID());
        if (pastAdmissions.isEmpty()) {
            return null;
        }

        Admission mostRecent = pastAdmissions.get(pastAdmissions.size() - 1);
        return doctorDAO.getDoctorById(mostRecent.getDoctorID());
    }

    public Doctor assignDoctor(String name, String dob, int hospitalId) {
        Doctor previousDoctor = suggestPreviousDoctor(name, dob, hospitalId);
        if (previousDoctor != null) {
            System.out.println("Returning patient detected - suggesting previous doctor: " + previousDoctor.getName());
            return previousDoctor;
        }

        Doctor leastBusy = findLeastBusyDoctor(hospitalId);
        System.out.println("New patient - assign least busy doctor: " +
                (leastBusy != null ? leastBusy.getName() : "none available"));
        return leastBusy;
    }
}