package menu;

import dao.*;
import ds.MenuStack;
import model.*;
import service.WorkloadManager;
import service.BillingService;
import service.FileManager;

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
            System.out.println("0. Back");
            System.out.println("9. Exit Application");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> addDoctor();
                case 2 -> addLabTechnician();
                case 3 -> registerPatientAndAdmit();
                case 4 -> addTestType();
                case 5 -> dischargePatient();
                case 0 -> {
                    navStack.pop();
                    flag = false;
                }
                case 9 -> {
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
        System.out.print("First Name: ");
        String firstName = sc.nextLine();
        System.out.print("Last Name: ");
        String lastName = sc.nextLine();
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Phone No: ");
        String phone = sc.nextLine();
        System.out.print("Specialization: ");
        String specialization = sc.nextLine();
        System.out.print("Department: ");
        String department = sc.nextLine();
        System.out.print("Qualification: ");
        String qualification = sc.nextLine();
        System.out.print("Consultation Fee: ");
        double fee = sc.nextDouble();

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
        System.out.print("First Name: ");
        String firstName = sc.nextLine();
        System.out.print("Last Name: ");
        String lastName = sc.nextLine();
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Phone No: ");
        String phone = sc.nextLine();
        System.out.print("Qualification: ");
        String qualification = sc.nextLine();

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
        System.out.print("First Name: ");
        String firstName = sc.nextLine();
        System.out.print("Last Name: ");
        String lastName = sc.nextLine();
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Phone No: ");
        String phone = sc.nextLine();
        System.out.print("DOB (YYYY-MM-DD): ");
        String dob = sc.nextLine();
        System.out.print("Gender: ");
        String gender = sc.nextLine();
        System.out.print("Blood Group: ");
        String bloodGroup = sc.nextLine();
        System.out.print("Street: ");
        String street = sc.nextLine();
        System.out.print("City: ");
        String city = sc.nextLine();
        System.out.print("State: ");
        String state = sc.nextLine();
        System.out.print("Pincode: ");
        String pincode = sc.nextLine();

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

        // Assign a doctor - returning patient gets previous doctor, else least busy
        Doctor assignedDoctor = workloadManager.assignDoctor(
                firstName + " " + lastName, dob, loggedInAdmin.getHospitalID());

        if (assignedDoctor == null) {
            System.out.println("No doctor available to assign. Admission cancelled.");
            navStack.pop();
            return;
        }

        System.out.print("Room Number: ");
        String roomNumber = sc.nextLine();
        System.out.print("Room Type (GENERAL/SEMI_PRIVATE/PRIVATE/ICU): ");
        String roomType = sc.nextLine();
        System.out.print("Room Charge: ");
        double roomCharge = sc.nextDouble();
        sc.nextLine();
        System.out.print("Admission Date (YYYY-MM-DD): ");
        String admissionDate = sc.nextLine();

        Patient newPatient = patientDAO.getPatientByUsername(username, loggedInAdmin.getHospitalID());

        Admission ad = new Admission();
        ad.setAdmissionDate(admissionDate);
        ad.setDischargeDate(null);
        ad.setRoomNumber(roomNumber);
        ad.setRoomType(roomType.toUpperCase());
        ad.setRoomCharge(roomCharge);
        ad.setStatus("ADMITTED");
        ad.setPatientID(newPatient.getPatientID());
        ad.setDoctorID(assignedDoctor.getDoctorID());
        ad.setAdminID(loggedInAdmin.getAdminID());
        ad.setHospitalID(loggedInAdmin.getHospitalID());

        boolean admissionAdded = admissionDAO.insertAdmission(ad);
        if (admissionAdded) {
            doctorDAO.incrementPatientCount(assignedDoctor.getDoctorID());
            System.out.println("Admission created successfully! Assigned Doctor: " + assignedDoctor.getName());
            fileManager.addAdmissionHistoryEntry(newPatient.getPatientID(), assignedDoctor.getName(),
                    roomNumber, roomType.toUpperCase(), admissionDate);
        } else {
            System.out.println("Failed to create admission.");
        }

        navStack.pop();
    }

    private void addTestType() {
        navStack.push("AddTestType");
        System.out.println("\nPath: " + navStack.getPath());

        sc.nextLine();
        System.out.print("Test Name: ");
        String testName = sc.nextLine();
        System.out.print("Normal Min: ");
        double normalMin = sc.nextDouble();
        System.out.print("Normal Max: ");
        double normalMax = sc.nextDouble();
        sc.nextLine();
        System.out.print("Unit (e.g. g/dL): ");
        String unit = sc.nextLine();
        System.out.print("Test Charge: ");
        double testCharge = sc.nextDouble();
        sc.nextLine();
        System.out.print("Equipment Name Required: ");
        String equipmentName = sc.nextLine();

        int hospitalId = loggedInAdmin.getHospitalID();

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

        System.out.print("Enter Admission ID: ");
        int admissionId = sc.nextInt();
        sc.nextLine();
        System.out.print("Discharge Date (YYYY-MM-DD): ");
        String dischargeDate = sc.nextLine();

        billingService.dischargeAndGenerateBill(admissionId, dischargeDate);

        navStack.pop();
    }
}