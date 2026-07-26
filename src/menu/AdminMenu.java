package menu;

import dao.*;
import ds.MenuStack;
import model.*;
import service.WorkloadManager;
import service.BillingService;
import service.FileManager;
import util.InputValidator;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private Scanner sc;
    private MenuStack navStack;
    private Admin loggedInAdmin;

    private DoctorDAO doctorDAO = new DoctorDAO();
    private LabTechnicianDAO labTechDAO = new LabTechnicianDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private AdmissionDAO admissionDAO = new AdmissionDAO();
    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private TestTypeDAO testTypeDAO = new TestTypeDAO();
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private WorkloadManager workloadManager = new WorkloadManager();
    private BillingService billingService = new BillingService();
    private FileManager fileManager = new FileManager();

    public AdminMenu(Scanner sc, MenuStack navStack, Admin a) {
        this.sc = sc;
        this.navStack = navStack;
        this.loggedInAdmin = a;
    }

    public void show() {
        navStack.push("AdminMenu");
        boolean flag = true;

        while (flag) {
            System.out.println("\nPath: " + navStack.getPath());
            System.out.println("===== Hospital Admin Menu =====");
            System.out.println("1. Add Doctor");
            System.out.println("2. Add Lab Technician");
            System.out.println("3. Register Patient & Create Admission");
            System.out.println("4. Add Test Type");
            System.out.println("5. Discharge Patient");
            System.out.println("6. View Doctors");
            System.out.println("7. View Lab Technicians");
            System.out.println("8. View Test Types");
            System.out.println("9. View Equipment");
            System.out.println("10. View Login Log");
            System.out.println("11. View Patients");
            System.out.println("0. Back");
            System.out.println("99. Exit Application");
            int choice = InputValidator.readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> addDoctor();
                case 2 -> addLabTechnician();
                case 3 -> registerPatientAndAdmit();
                case 4 -> addTestType();
                case 5 -> dischargePatient();
                case 6 -> viewDoctors();
                case 7 -> viewLabTechnicians();
                case 8 -> viewTestTypes();
                case 9 -> viewEquipment();
                case 10 -> viewLoginLog();
                case 11 -> viewPatients();
                case 0 -> {
                    navStack.pop();
                    flag = false;
                }
                case 99 -> {
                    System.out.println("Exiting MediTrack. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addDoctor() {
        navStack.push("AddDoctor");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        String firstName = InputValidator.readNonEmptyString(sc, "First Name: ");
        String lastName = InputValidator.readNonEmptyString(sc, "Last Name: ");
        String username = InputValidator.readNonEmptyString(sc, "Username: ");

        if (doctorDAO.getDoctorByUsername(username, loggedInAdmin.getHospitalID()) != null) {
            System.out.println("A doctor with this username already exists in your hospital.");
            navStack.pop();
            return;
        }

        String password = InputValidator.readNonEmptyString(sc, "Password: ");
        String email = InputValidator.readNonEmptyString(sc, "Email: ");
        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");
        String specialization = InputValidator.readNonEmptyString(sc, "Specialization: ");
        String department = InputValidator.readNonEmptyString(sc, "Department: ");
        String qualification = InputValidator.readNonEmptyString(sc, "Qualification: ");
        double fee = InputValidator.readPositiveDouble(sc, "Consultation Fee: ");

        Doctor d = new Doctor();
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setUsername(username);
        d.setPassword(password);
        d.setEmail(email);
        d.setPhoneNo(phone);
        d.setSpecialization(specialization);
        d.setDepartment(department);
        d.setQualification(qualification);
        d.setConsultationFee(fee);
        d.setHospitalID(loggedInAdmin.getHospitalID());

        boolean success = doctorDAO.insertDoctor(d);
        System.out.println(success ? "Doctor added successfully!" : "Failed to add doctor.");

        navStack.pop();
    }

    private void addLabTechnician() {
        navStack.push("AddLabTechnician");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        String firstName = InputValidator.readNonEmptyString(sc, "First Name: ");
        String lastName = InputValidator.readNonEmptyString(sc, "Last Name: ");
        String username = InputValidator.readNonEmptyString(sc, "Username: ");

        if (labTechDAO.getLabTechnicianByUsername(username, loggedInAdmin.getHospitalID()) != null) {
            System.out.println("A lab technician with this username already exists in your hospital.");
            navStack.pop();
            return;
        }

        String password = InputValidator.readNonEmptyString(sc, "Password: ");
        String email = InputValidator.readNonEmptyString(sc, "Email: ");
        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");
        String qualification = InputValidator.readNonEmptyString(sc, "Qualification: ");

        LabTechnician lt = new LabTechnician();
        lt.setFirstName(firstName);
        lt.setLastName(lastName);
        lt.setUsername(username);
        lt.setPassword(password);
        lt.setEmail(email);
        lt.setPhoneNo(phone);
        lt.setQualification(qualification);
        lt.setHospitalID(loggedInAdmin.getHospitalID());

        boolean success = labTechDAO.insertLabTechnician(lt);
        System.out.println(success ? "Lab Technician added successfully!" : "Failed to add lab technician.");

        navStack.pop();
    }

    private void registerPatientAndAdmit() {
        navStack.push("RegisterPatient");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        String firstName = InputValidator.readNonEmptyString(sc, "First Name: ");
        String lastName = InputValidator.readNonEmptyString(sc, "Last Name: ");
        String username = InputValidator.readNonEmptyString(sc, "Username: ");
        String password = InputValidator.readNonEmptyString(sc, "Password: ");
        String email = InputValidator.readNonEmptyString(sc, "Email: ");
        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");
        String dob = InputValidator.readDate(sc, "DOB (YYYY-MM-DD): ", true);

        String gender = InputValidator.readMenuChoice(sc, "Gender:",
                new String[]{"MALE", "FEMALE", "OTHER"},
                new String[]{"MALE", "FEMALE", "OTHER"});
        sc.nextLine();

        String bloodGroup = InputValidator.readMenuChoice(sc, "Blood Group:",
                new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"},
                new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        sc.nextLine();

        String street = InputValidator.readNonEmptyString(sc, "Street: ");
        String city = InputValidator.readAlphabeticString(sc, "City: ");
        String state = InputValidator.readAlphabeticString(sc, "State: ");
        String pincode = InputValidator.readPincode(sc, "Pincode: ");

        String fullName = firstName + " " + lastName;

        Patient existingPatient = patientDAO.findReturningPatient(fullName, dob, loggedInAdmin.getHospitalID());
        Patient newPatient;

        if (existingPatient != null) {
            System.out.println("Returning patient detected - reusing existing patient record.");
            newPatient = existingPatient;
        } else {
            if (patientDAO.getPatientByUsername(username, loggedInAdmin.getHospitalID()) != null) {
                System.out.println("A patient with this username already exists in your hospital.");
                navStack.pop();
                return;
            }

            Patient p = new Patient();
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setUsername(username);
            p.setPassword(password);
            p.setEmail(email);
            p.setPhoneNo(phone);
            p.setDob(dob);
            p.setGender(gender);
            p.setBloodGroup(bloodGroup);
            p.setStreet(street);
            p.setCity(city);
            p.setState(state);
            p.setPincode(pincode);
            p.setHospitalID(loggedInAdmin.getHospitalID());

            boolean patientAdded = patientDAO.insertPatient(p);
            if (!patientAdded) {
                System.out.println("Failed to register patient.");
                navStack.pop();
                return;
            }
            System.out.println("Patient registered successfully!");

            newPatient = patientDAO.getPatientByUsername(username, loggedInAdmin.getHospitalID());
        }

        Doctor assignedDoctor = workloadManager.assignDoctor(
                fullName, dob, loggedInAdmin.getHospitalID());

        if (assignedDoctor == null) {
            System.out.println("No doctor available to assign. Admission cancelled.");
            navStack.pop();
            return;
        }

        String roomNumber = InputValidator.readNonEmptyString(sc, "Room Number: ");

        String roomType = InputValidator.readMenuChoice(sc, "Room Type:",
                new String[]{"GENERAL", "SEMI_PRIVATE", "PRIVATE", "ICU"},
                new String[]{"GENERAL", "SEMI_PRIVATE", "PRIVATE", "ICU"});
        sc.nextLine();

        double roomCharge = InputValidator.readPositiveDouble(sc, "Room Charge: ");
        sc.nextLine();
        String admissionDate = InputValidator.readDate(sc, "Admission Date (YYYY-MM-DD): ", false);

        Admission ad = new Admission();
        ad.setAdmissionDate(admissionDate);
        ad.setDischargeDate(null);
        ad.setRoomNumber(roomNumber);
        ad.setRoomType(roomType);
        ad.setRoomCharge(roomCharge);
        ad.setStatus("ADMITTED");
        ad.setPatientID(newPatient.getPatientID());
        ad.setDoctorID(assignedDoctor.getDoctorID());
        ad.setAdminID(loggedInAdmin.getAdminID());
        ad.setHospitalID(loggedInAdmin.getHospitalID());

        boolean admissionAdded = admissionDAO.insertAdmission(ad);
        if (admissionAdded) {
            System.out.println("Admission created successfully! Assigned Doctor: " + assignedDoctor.getName());
            fileManager.addAdmissionHistoryEntry(newPatient.getPatientID(), assignedDoctor.getName(),
                    roomNumber, roomType, admissionDate);
        } else {
            System.out.println("Failed to create admission.");
        }

        navStack.pop();
    }

    private void addTestType() {
        navStack.push("AddTestType");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        String testName = InputValidator.readNonEmptyString(sc, "Test Name: ");

        int hospitalId = loggedInAdmin.getHospitalID();

        List<TestType> existingTestTypes = testTypeDAO.getAllTestTypesByHospital(hospitalId);
        for (TestType existing : existingTestTypes) {
            if (existing.getTestName().equalsIgnoreCase(testName)) {
                System.out.println("A test type with this name already exists in your hospital.");
                navStack.pop();
                return;
            }
        }

        double normalMin = InputValidator.readNonNegativeDouble(sc, "Normal Min: ");
        double normalMax;
        while (true) {
            normalMax = InputValidator.readNonNegativeDouble(sc, "Normal Max: ");
            if (normalMax > normalMin) {
                break;
            }
            System.out.println("Normal Max must be greater than Normal Min. Please try again.");
        }
        sc.nextLine();
        String unit = InputValidator.readNonEmptyString(sc, "Unit (e.g. g/dL): ");
        double testCharge = InputValidator.readPositiveDouble(sc, "Test Charge: ");
        sc.nextLine();
        String equipmentName = InputValidator.readNonEmptyString(sc, "Equipment Name Required: ");

        Equipment existingEquipment = equipmentDAO.findByEquipmentName(equipmentName, hospitalId);
        int equipmentId;

        if (existingEquipment != null) {
            System.out.println("Equipment already exists - reusing it.");
            equipmentId = existingEquipment.getEquipmentID();
        } else {
            Equipment newEquipment = new Equipment();
            newEquipment.setEquipmentName(equipmentName);
            newEquipment.setStatus("AVAILABLE");
            newEquipment.setPurchaseDate(java.time.LocalDate.now().toString());
            newEquipment.setHospitalID(hospitalId);

            equipmentDAO.insertEquipment(newEquipment);
            Equipment created = equipmentDAO.findByEquipmentName(equipmentName, hospitalId);
            equipmentId = created.getEquipmentID();
            System.out.println("New equipment created.");
        }

        TestType tt = new TestType();
        tt.setTestName(testName);
        tt.setNormalMin(normalMin);
        tt.setNormalMax(normalMax);
        tt.setUnit(unit);
        tt.setTestCharge(testCharge);
        tt.setHospitalID(hospitalId);
        tt.setEquipmentID(equipmentId);

        boolean success = testTypeDAO.insertTestType(tt);
        System.out.println(success ? "Test Type added successfully!" : "Failed to add test type.");

        navStack.pop();
    }

    private void dischargePatient() {
        navStack.push("DischargePatient");
        System.out.println("\nPath: " + navStack.getPath());

        int admissionId = InputValidator.readInt(sc, "Enter Admission ID: ");

        Admission ad = admissionDAO.getAdmissionById(admissionId);
        if (ad == null) {
            System.out.println("Admission not found.");
            navStack.pop();
            return;
        }

        if (ad.getHospitalID() != loggedInAdmin.getHospitalID()) {
            System.out.println("This admission does not belong to your hospital.");
            navStack.pop();
            return;
        }

        if (ad.getStatus().equalsIgnoreCase("DISCHARGED")) {
            System.out.println("This patient has already been discharged.");
            navStack.pop();
            return;
        }

        sc.nextLine();
        System.out.print("Confirm discharge for Admission ID " + admissionId + "? (Y/N): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Discharge cancelled.");
            navStack.pop();
            return;
        }

        billingService.dischargeAndGenerateBill(admissionId);

        navStack.pop();
    }

    private void viewDoctors() {
        navStack.push("ViewDoctors");
        System.out.println("\nPath: " + navStack.getPath());

        List<Doctor> doctors = doctorDAO.getAllDoctorsByHospital(loggedInAdmin.getHospitalID());
        if (doctors.isEmpty()) {
            System.out.println("No doctors found.");
        } else {
            for (Doctor d : doctors) {
                System.out.println(d);
            }
        }

        navStack.pop();
    }

    private void viewLabTechnicians() {
        navStack.push("ViewLabTechnicians");
        System.out.println("\nPath: " + navStack.getPath());

        List<LabTechnician> labTechs = labTechDAO.getAllLabTechniciansByHospital(loggedInAdmin.getHospitalID());
        if (labTechs.isEmpty()) {
            System.out.println("No lab technicians found.");
        } else {
            for (LabTechnician lt : labTechs) {
                System.out.println(lt);
            }
        }

        navStack.pop();
    }

    private void viewTestTypes() {
        navStack.push("ViewTestTypes");
        System.out.println("\nPath: " + navStack.getPath());

        List<TestType> testTypes = testTypeDAO.getAllTestTypesByHospital(loggedInAdmin.getHospitalID());
        if (testTypes.isEmpty()) {
            System.out.println("No test types found.");
        } else {
            for (TestType tt : testTypes) {
                System.out.println(tt);
            }
        }

        navStack.pop();
    }

    private void viewEquipment() {
        navStack.push("ViewEquipment");
        System.out.println("\nPath: " + navStack.getPath());

        List<Equipment> equipmentList = equipmentDAO.getAllEquipmentByHospital(loggedInAdmin.getHospitalID());
        if (equipmentList.isEmpty()) {
            System.out.println("No equipment found.");
        } else {
            for (Equipment eq : equipmentList) {
                System.out.println(eq);
            }
        }

        navStack.pop();
    }

    private void viewLoginLog() {
        navStack.push("ViewLoginLog");
        System.out.println("\nPath: " + navStack.getPath());

        Hospital h = hospitalDAO.getHospitalById(loggedInAdmin.getHospitalID());
        if (h == null) {
            System.out.println("Could not determine your hospital.");
            navStack.pop();
            return;
        }

        String log = fileManager.readHospitalLoginLog(h.getHospitalCode());
        System.out.println(log);

        navStack.pop();
    }

    private void viewPatients() {
        navStack.push("ViewPatients");
        System.out.println("\nPath: " + navStack.getPath());

        List<Patient> patients = patientDAO.getAllPatientsByHospital(loggedInAdmin.getHospitalID());
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
        } else {
            for (Patient p : patients) {
                System.out.println(p);
            }
        }

        navStack.pop();
    }
}