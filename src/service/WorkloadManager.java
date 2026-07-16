package service;

import dao.*;
import model.*;

import java.util.List;

public class WorkloadManager {

    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();

    // Finds the Doctor with the lowest PatientCount in a given Hospital.
    // Called when Admin admits a new (non-returning) patient.
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

    // Checks if this is a returning patient (by Name + DOB) and if so,
    // returns their most recent previous Doctor. Returns null if new patient.
    public Doctor suggestPreviousDoctor(String name, String dob, int hospitalId) {
        Patient existingPatient = patientDAO.findReturningPatient(name, dob, hospitalId);
        if (existingPatient == null) {
            return null;   // not a returning patient
        }

        List<Admission> pastAdmissions = admissionDAO.getAdmissionsByPatient(existingPatient.getPatientID());
        if (pastAdmissions.isEmpty()) {
            return null;
        }

        // Most recent admission is the last one in the list (assuming insertion order)
        Admission mostRecent = pastAdmissions.get(pastAdmissions.size() - 1);
        return doctorDAO.getDoctorById(mostRecent.getDoctorID());
    }

    // The single entry point Admin's "Assign Doctor" flow should call.
    // Checks returning patient first, falls back to least-busy doctor otherwise.
    public Doctor assignDoctor(String name, String dob, int hospitalId) {
        Doctor previousDoctor = suggestPreviousDoctor(name, dob, hospitalId);
        if (previousDoctor != null) {
            System.out.println("Returning patient detected - suggesting previous doctor: " + previousDoctor.getName());
            return previousDoctor;
        }

        Doctor leastBusy = findLeastBusyDoctor(hospitalId);
        System.out.println("New patient - assigning least busy doctor: " +
                (leastBusy != null ? leastBusy.getName() : "none available"));
        return leastBusy;
    }
}