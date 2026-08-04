package menu;

import dao.*;
import ds.*;
import model.*;
import service.*;
import util.*;
import java.util.*;

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
    private AdminDAO adminDAO = new AdminDAO();
    private WorkloadManager workloadManager = new WorkloadManager();
    private BillingService billingService = new BillingService();
    private FileManager fileManager = new FileManager();

    private static final String[] ROOM_TYPE_LABELS = {"General", "Semi Private", "Private", "ICU"};
    private static final String[] ROOM_TYPE_VALUES = {"GENERAL", "SEMI_PRIVATE", "PRIVATE", "ICU"};
    private static final double[] ROOM_TYPE_CHARGES = {1000.0, 1800.0, 3000.0, 5000.0};

    private static final String[] GENERAL_ROOMS =
            {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110"};
    private static final String[] SEMI_PRIVATE_ROOMS =
            {"201", "202", "203", "204", "205", "206", "207", "208", "209", "210"};
    private static final String[] PRIVATE_ROOMS =
            {"301", "302", "303", "304", "305", "306", "307", "308", "309", "310"};
    private static final String[] ICU_ROOMS =
            {"401", "402", "403", "404", "405", "406", "407", "408", "409", "410"};

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
            System.out.println("12. Change Password");
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
                case 12 -> changePassword();
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
        String email = InputValidator.readEmail(sc, "Email: ");

        if (doctorDAO.getDoctorByEmail(email) != null) {
            System.out.println("A doctor with this email already exists.");
            navStack.pop();
            return;
        }

        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");

        if (doctorDAO.getDoctorByPhone(phone) != null) {
            System.out.println("A doctor with this phone number already exists.");
            navStack.pop();
            return;
        }

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

        if (labTechDAO.getLabTechnicianByEmail(email) != null) {
            System.out.println("A lab technician with this email already exists.");
            navStack.pop();
            return;
        }

        String phone = InputValidator.readPhoneNumber(sc, "Phone No: ");

        if (labTechDAO.getLabTechnicianByPhone(phone) != null) {
            System.out.println("A lab technician with this phone number already exists.");
            navStack.pop();
            return;
        }

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
        String email = InputValidator.readEmail(sc, "Email: ");
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

        String street = InputValidator.readAddressString(sc, "Street: ");
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

            if (patientDAO.getPatientByEmail(email) != null) {
                System.out.println("A patient with this email already exists.");
                navStack.pop();
                return;
            }

            if (patientDAO.getPatientByPhone(phone) != null) {
                System.out.println("A patient with this phone number already exists.");
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

        // ---- Requirement 5: Room Allocation Improvement ----
        // Step 1: show available Room Types with their fixed charges.
        printRoomTypeMenu();
        int roomTypeChoice = InputValidator.readInt(sc, "Select Room Type: ");
        sc.nextLine();

        if (roomTypeChoice < 1 || roomTypeChoice > ROOM_TYPE_VALUES.length) {
            System.out.println("Invalid room type selection. Admission cancelled.");
            navStack.pop();
            return;
        }

        String roomType = ROOM_TYPE_VALUES[roomTypeChoice - 1];
        double roomCharge = ROOM_TYPE_CHARGES[roomTypeChoice - 1];

        // Step 2: show only the available room numbers of the selected type.
        List<String> availableRooms = getAvailableRoomNumbers(roomType);
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms of this type right now. Admission cancelled.");
            navStack.pop();
            return;
        }

        printAvailableRoomNumbers(availableRooms);
        int roomChoice = InputValidator.readInt(sc, "Select Room Number (enter list no.): ");
        sc.nextLine();

        if (roomChoice < 1 || roomChoice > availableRooms.size()) {
            System.out.println("Invalid room selection. Admission cancelled.");
            navStack.pop();
            return;
        }

        String roomNumber = availableRooms.get(roomChoice - 1);
        // Step 3: charge is auto-assigned from the selected Room Type - no manual entry.

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

        // Requirement 2: show available admissions before asking for an Admission ID.
        List<Admission> activeAdmissions = getActiveAdmissionsForHospital();
        if (activeAdmissions.isEmpty()) {
            System.out.println("No currently admitted patients found.");
            navStack.pop();
            return;
        }
        printAdmissionTable(activeAdmissions);

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
            printDoctorTable(doctors);
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
            printLabTechnicianTable(labTechs);
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
            printTestTypeTable(testTypes);
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
            printEquipmentTable(equipmentList);
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
            printPatientTable(patients);
        }

        navStack.pop();
    }

    private void changePassword() {
        navStack.push("ChangePassword");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Enter Current Password: ");
        String currentPassword = sc.nextLine();

        if (!loggedInAdmin.getPassword().equals(currentPassword)) {
            System.out.println("Incorrect current password.");
            navStack.pop();
            return;
        }

        String newPassword = InputValidator.readNonEmptyString(sc, "Enter New Password: ");
        String confirmPassword = InputValidator.readNonEmptyString(sc, "Confirm New Password: ");

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match. Password not changed.");
            navStack.pop();
            return;
        }

        boolean success = adminDAO.updatePassword(loggedInAdmin.getAdminID(), newPassword);
        if (success) {
            loggedInAdmin.setPassword(newPassword);
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }

        navStack.pop();
    }

    // ==================== Private Table / Display Helpers ====================

    private void printDoctorTable(List<Doctor> doctors) {
        System.out.println("-".repeat(95));
        System.out.printf("%-5s %-25s %-18s %-18s %-10s %-10s%n",
                "ID", "Name", "Specialization", "Department", "Patients", "Fee");
        System.out.println("-".repeat(95));

        for (Doctor d : doctors) {
            System.out.printf("%-5d %-25s %-18s %-18s %-10d Rs.%-8.2f%n",
                    d.getDoctorID(),
                    d.getName(),
                    d.getSpecialization(),
                    d.getDepartment(),
                    d.getPatientCount(),
                    d.getConsultationFee());
        }

        System.out.println("-".repeat(95));
    }

    private void printLabTechnicianTable(List<LabTechnician> labTechs) {
        System.out.println("-".repeat(75));
        System.out.printf("%-5s %-25s %-20s %-15s%n",
                "ID", "Name", "Qualification", "Phone");
        System.out.println("-".repeat(75));

        for (LabTechnician lt : labTechs) {
            System.out.printf("%-5d %-25s %-20s %-15s%n",
                    lt.getLabTechID(),
                    lt.getName(),
                    lt.getQualification(),
                    lt.getPhoneNo());
        }

        System.out.println("-".repeat(75));
    }

    private void printTestTypeTable(List<TestType> testTypes) {
        System.out.println("-".repeat(90));
        System.out.printf("%-5s %-30s %-20s %-10s%n",
                "ID", "Test Name", "Normal Range", "Charge");
        System.out.println("-".repeat(90));

        for (TestType tt : testTypes) {
            String range = String.format("%.2f - %.2f %s", tt.getNormalMin(), tt.getNormalMax(), tt.getUnit());
            System.out.printf("%-5d %-30s %-20s Rs.%-8.2f%n",
                    tt.getTestTypeID(),
                    tt.getTestName(),
                    range,
                    tt.getTestCharge());
        }

        System.out.println("-".repeat(90));
    }

    private void printEquipmentTable(List<Equipment> equipmentList) {
        System.out.println("-".repeat(80));
        System.out.printf("%-5s %-30s %-15s %-15s%n",
                "ID", "Equipment Name", "Status", "Purchase Date");
        System.out.println("-".repeat(80));

        for (Equipment eq : equipmentList) {
            System.out.printf("%-5d %-30s %-15s %-15s%n",
                    eq.getEquipmentID(),
                    eq.getEquipmentName(),
                    eq.getStatus(),
                    eq.getPurchaseDate());
        }

        System.out.println("-".repeat(80));
    }

    private void printPatientTable(List<Patient> patients) {
        System.out.println("-".repeat(90));
        System.out.printf("%-5s %-25s %-12s %-8s %-10s %-15s%n",
                "ID", "Name", "DOB", "Gender", "Blood Grp", "City");
        System.out.println("-".repeat(90));

        for (Patient p : patients) {
            System.out.printf("%-5d %-25s %-12s %-8s %-10s %-15s%n",
                    p.getPatientID(),
                    p.getName(),
                    p.getDob(),
                    p.getGender(),
                    p.getBloodGroup(),
                    p.getCity());
        }

        System.out.println("-".repeat(90));
    }

    private void printAdmissionTable(List<Admission> admissions) {
        System.out.println("-".repeat(90));
        System.out.printf("%-14s %-25s %-12s %-15s %-12s%n",
                "Admission ID", "Patient Name", "Room No.", "Room Type", "Status");
        System.out.println("-".repeat(90));

        for (Admission ad : admissions) {
            Patient p = patientDAO.getPatientById(ad.getPatientID());
            String patientName = (p != null) ? p.getName() : "Unknown";

            System.out.printf("%-14d %-25s %-12s %-15s %-12s%n",
                    ad.getAdmissionID(),
                    patientName,
                    ad.getRoomNumber(),
                    ad.getRoomType(),
                    ad.getStatus());
        }

        System.out.println("-".repeat(90));
    }

    private void printRoomTypeMenu() {
        System.out.println("-".repeat(40));
        System.out.printf("%-5s %-15s %-10s%n", "No.", "Room Type", "Charge");
        System.out.println("-".repeat(40));

        for (int i = 0; i < ROOM_TYPE_LABELS.length; i++) {
            System.out.printf("%-5d %-15s Rs.%-8.2f%n",
                    (i + 1), ROOM_TYPE_LABELS[i], ROOM_TYPE_CHARGES[i]);
        }

        System.out.println("-".repeat(40));
    }

    private void printAvailableRoomNumbers(List<String> rooms) {
        System.out.println("-".repeat(30));
        System.out.printf("%-5s %-10s%n", "No.", "Room No.");
        System.out.println("-".repeat(30));

        for (int i = 0; i < rooms.size(); i++) {
            System.out.printf("%-5d %-10s%n", (i + 1), rooms.get(i));
        }

        System.out.println("-".repeat(30));
    }

    // ==================== Private Data Helpers (composition only, no DAO changes) ====================

    private List<Admission> getActiveAdmissionsForHospital() {
        List<Admission> result = new ArrayList<>();
        List<Patient> patients = patientDAO.getAllPatientsByHospital(loggedInAdmin.getHospitalID());

        for (Patient p : patients) {
            List<Admission> admissions = admissionDAO.getAdmissionsByPatient(p.getPatientID());
            for (Admission ad : admissions) {
                if (ad.getStatus().equalsIgnoreCase("ADMITTED")) {
                    result.add(ad);
                }
            }
        }

        return result;
    }

    private String[] getRoomPoolForType(String roomType) {
        return switch (roomType) {
            case "GENERAL" -> GENERAL_ROOMS;
            case "SEMI_PRIVATE" -> SEMI_PRIVATE_ROOMS;
            case "PRIVATE" -> PRIVATE_ROOMS;
            case "ICU" -> ICU_ROOMS;
            default -> new String[0];
        };
    }

    private List<String> getAvailableRoomNumbers(String roomType) {
        String[] pool = getRoomPoolForType(roomType);
        List<Admission> activeAdmissions = getActiveAdmissionsForHospital();

        Set<String> occupied = new HashSet<>();
        for (Admission ad : activeAdmissions) {
            if (ad.getRoomType() != null && ad.getRoomType().equalsIgnoreCase(roomType)) {
                occupied.add(ad.getRoomNumber());
            }
        }

        List<String> available = new ArrayList<>();
        for (String room : pool) {
            if (!occupied.contains(room)) {
                available.add(room);
            }
        }

        return available;
    }
}