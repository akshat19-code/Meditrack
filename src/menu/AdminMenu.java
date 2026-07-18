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

        // Duplicate username check - must be unique within this hospital
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

        // Duplicate username check - must be unique within this hospital
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
        sc.nextLine(); // clear leftover newline from readMenuChoice's internal nextInt()

        String bloodGroup = InputValidator.readMenuChoice(sc, "Blood Group:",
                new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"},
                new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        sc.nextLine(); // clear leftover newline from readMenuChoice's internal nextInt()

        String street = InputValidator.readNonEmptyString(sc, "Street: ");
        String city = InputValidator.readAlphabeticString(sc, "City: ");
        String state = InputValidator.readAlphabeticString(sc, "State: ");
        String pincode = InputValidator.readPincode(sc, "Pincode: ");

        String fullName = firstName + " " + lastName;

        // Check if this is a returning patient (same Name + DOB in this hospital)
        // BEFORE inserting, so we reuse the existing Patient row instead of
        // creating a duplicate one.
        Patient existingPatient = patientDAO.findReturningPatient(fullName, dob, loggedInAdmin.getHospitalID());
        Patient newPatient;

        if (existingPatient != null) {
            System.out.println("Returning patient detected - reusing existing patient record.");
            newPatient = existingPatient;
        } else {
            // Duplicate username check - only relevant when we're actually inserting a new Patient row
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

        // Assign a doctor - returning patient gets previous doctor, else least busy
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
        sc.nextLine(); // clear leftover newline from readMenuChoice's internal nextInt()

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

        // NOTE: Doctor.PatientCount is now incremented automatically by the
        // UpdateDoctorPatientCount trigger (fires AFTER INSERT ON Admission) -
        // no manual increment needed here anymore.
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

        // Duplicate check - same TestName should not exist twice in the same hospital
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

        // Case-insensitive, hospital-scoped check - reuse existing Equipment if it exists
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
}