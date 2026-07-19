-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 18, 2026 at 06:24 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `meditrack`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `GenerateBillAndDischarge` (IN `AID` INT)   BEGIN

    DECLARE RoomChargeValue DOUBLE;
    DECLARE DoctorFeeValue DOUBLE;
    DECLARE TestChargeValue DOUBLE;
    DECLARE TotalBill DOUBLE;
    DECLARE DoctorIDValue INT;
    DECLARE CountBill INT;

    -- Check if Bill already exists
    SELECT COUNT(*)
    INTO CountBill
    FROM Bill
    WHERE AdmissionID = AID;

    IF CountBill = 0 THEN

        -- Get Room Charge and Doctor ID
        SELECT RoomCharge, DoctorID
        INTO RoomChargeValue, DoctorIDValue
        FROM Admission
        WHERE AdmissionID = AID;

        -- Get Doctor Fee
        SELECT ConsultationFee
        INTO DoctorFeeValue
        FROM Doctor
        WHERE DoctorID = DoctorIDValue;

        -- Calculate Total Bill using Function
        SET TotalBill = CalculateBill(AID);

        -- Calculate Test Charge
        SET TestChargeValue = TotalBill - RoomChargeValue - DoctorFeeValue;

        -- Start Transaction
        START TRANSACTION;

        -- Insert Bill
        INSERT INTO Bill
        (
            RoomCharge,
            DoctorFee,
            TestCharge,
            TotalAmount,
            BillDate,
            AdmissionID
        )
        VALUES
        (
            RoomChargeValue,
            DoctorFeeValue,
            TestChargeValue,
            TotalBill,
            CURDATE(),
            AID
        );

        -- Update Admission
        UPDATE Admission
        SET
            Status = 'DISCHARGED',
            DischargeDate = CURDATE()
        WHERE AdmissionID = AID;

        -- Save Changes
        COMMIT;

    END IF;

END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `CalculateAge` (`p_DOB` DATE) RETURNS INT(11)  BEGIN
    DECLARE v_Age INT;

    SET v_Age = TIMESTAMPDIFF(YEAR, p_DOB, CURDATE());

    RETURN v_Age;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `CalculateBill` (`AID` INT) RETURNS DOUBLE  BEGIN

    DECLARE RoomChargeValue DOUBLE;
    DECLARE DoctorFeeValue DOUBLE;
    DECLARE TotalTestCharge DOUBLE;
    DECLARE DoctorIDValue INT;

    SELECT RoomCharge, DoctorID
    INTO RoomChargeValue, DoctorIDValue
    FROM Admission
    WHERE AdmissionID = AID;

    SELECT ConsultationFee
    INTO DoctorFeeValue
    FROM Doctor
    WHERE DoctorID = DoctorIDValue;

    SELECT COALESCE(SUM(TestCharge), 0)
    INTO TotalTestCharge
    FROM TestRequest
    JOIN TestType
        ON TestRequest.TestTypeID = TestType.TestTypeID
    WHERE AdmissionID = AID
      AND TestRequest.Status = 'COMPLETED';

    RETURN COALESCE(RoomChargeValue, 0)
         + COALESCE(DoctorFeeValue, 0)
         + COALESCE(TotalTestCharge, 0);

END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `AdminID` int(11) NOT NULL,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `PhoneNo` varchar(15) DEFAULT NULL,
  `HospitalID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`AdminID`, `FirstName`, `LastName`, `Name`, `Username`, `Password`, `Email`, `PhoneNo`, `HospitalID`) VALUES
(1, 'Rakesh', 'Sharma', 'Rakesh Sharma', 'rakesh.admin', 'admin@123', 'rakesh.sharma@citycare.com', '9820011001', 1),
(2, 'Sunita', 'Patil', 'Sunita Patil', 'sunita.admin', 'admin@123', 'sunita.patil@sunrisehosp.com', '9820011002', 2),
(3, 'Karthik', 'Raman', 'Karthik Raman', 'karthik.admin', 'admin@123', 'karthik.raman@lifelinemed.com', '9820011003', 3);

-- --------------------------------------------------------

--
-- Table structure for table `admission`
--

CREATE TABLE `admission` (
  `AdmissionID` int(11) NOT NULL,
  `AdmissionDate` date DEFAULT NULL,
  `DischargeDate` date DEFAULT NULL,
  `RoomNumber` varchar(10) DEFAULT NULL,
  `RoomType` varchar(20) DEFAULT NULL,
  `RoomCharge` double DEFAULT NULL,
  `Status` varchar(20) DEFAULT NULL,
  `PatientID` int(11) DEFAULT NULL,
  `DoctorID` int(11) DEFAULT NULL,
  `AdminID` int(11) DEFAULT NULL,
  `HospitalID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admission`
--

INSERT INTO `admission` (`AdmissionID`, `AdmissionDate`, `DischargeDate`, `RoomNumber`, `RoomType`, `RoomCharge`, `Status`, `PatientID`, `DoctorID`, `AdminID`, `HospitalID`) VALUES
(1, '2026-01-05', '2026-01-10', '101', 'GENERAL', 1000, 'DISCHARGED', 1, 1, 1, 1),
(2, '2026-01-08', '2026-01-14', '102', 'SEMI_PRIVATE', 1800, 'DISCHARGED', 2, 1, 1, 1),
(3, '2026-01-12', '2026-01-18', '201', 'PRIVATE', 3000, 'DISCHARGED', 3, 2, 1, 1),
(4, '2026-01-15', '2026-01-20', '202', 'GENERAL', 1000, 'DISCHARGED', 4, 2, 1, 1),
(5, '2026-02-01', NULL, '103', 'ICU', 5000, 'ADMITTED', 5, 3, 1, 1),
(6, '2026-02-05', NULL, '104', 'SEMI_PRIVATE', 1800, 'ADMITTED', 6, 3, 1, 1),
(7, '2026-01-06', '2026-01-11', '301', 'GENERAL', 1100, 'DISCHARGED', 7, 4, 2, 2),
(8, '2026-01-10', '2026-01-16', '302', 'SEMI_PRIVATE', 1900, 'DISCHARGED', 8, 4, 2, 2),
(9, '2026-01-14', '2026-01-21', '303', 'PRIVATE', 3200, 'DISCHARGED', 9, 5, 2, 2),
(10, '2026-02-02', NULL, '304', 'GENERAL', 1100, 'ADMITTED', 10, 5, 2, 2),
(11, '2026-02-06', NULL, '305', 'ICU', 5200, 'ADMITTED', 11, 6, 2, 2),
(12, '2026-01-07', '2026-01-13', '401', 'GENERAL', 950, 'DISCHARGED', 12, 7, 3, 3),
(13, '2026-01-11', '2026-01-17', '402', 'SEMI_PRIVATE', 1700, 'DISCHARGED', 13, 7, 3, 3),
(14, '2026-01-16', '2026-01-23', '403', 'PRIVATE', 2900, 'DISCHARGED', 14, 7, 3, 3),
(15, '2026-02-03', NULL, '404', 'GENERAL', 950, 'ADMITTED', 15, 8, 3, 3),
(16, '2026-02-07', NULL, '405', 'ICU', 5100, 'ADMITTED', 16, 8, 3, 3),
(17, '2026-07-17', NULL, 'A-203', 'PRIVATE', 3500, 'ADMITTED', 17, 9, 1, 1);

--
-- Triggers `admission`
--
DELIMITER $$
CREATE TRIGGER `UpdateDoctorPatientCount` AFTER INSERT ON `admission` FOR EACH ROW BEGIN

    UPDATE Doctor
    SET PatientCount = PatientCount + 1
    WHERE DoctorID = NEW.DoctorID;

END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `UpdateDoctorPatientCountOnDischarge` AFTER UPDATE ON `admission` FOR EACH ROW BEGIN

    IF OLD.Status <> 'DISCHARGED' AND NEW.Status = 'DISCHARGED' THEN

        UPDATE Doctor
        SET PatientCount = GREATEST(PatientCount - 1, 0)
        WHERE DoctorID = NEW.DoctorID;

    END IF;

END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `bill`
--

CREATE TABLE `bill` (
  `BillID` int(11) NOT NULL,
  `RoomCharge` double DEFAULT NULL,
  `DoctorFee` double DEFAULT NULL,
  `TestCharge` double DEFAULT NULL,
  `TotalAmount` double DEFAULT NULL,
  `BillDate` date DEFAULT NULL,
  `AdmissionID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bill`
--

INSERT INTO `bill` (`BillID`, `RoomCharge`, `DoctorFee`, `TestCharge`, `TotalAmount`, `BillDate`, `AdmissionID`) VALUES
(1, 1000, 500, 270, 1770, '2026-01-10', 1),
(2, 1800, 500, 390, 2690, '2026-01-14', 2),
(3, 3000, 800, 470, 4270, '2026-01-18', 3),
(4, 1000, 800, 480, 2280, '2026-01-20', 4),
(5, 1100, 600, 285, 1985, '2026-01-11', 7),
(6, 1900, 600, 650, 3150, '2026-01-16', 8),
(7, 3200, 900, 420, 4520, '2026-01-21', 9),
(8, 950, 750, 270, 1970, '2026-01-13', 12),
(9, 1700, 750, 500, 2950, '2026-01-17', 13),
(10, 2900, 750, 440, 4090, '2026-01-23', 14);

-- --------------------------------------------------------

--
-- Table structure for table `doctor`
--

CREATE TABLE `doctor` (
  `DoctorID` int(11) NOT NULL,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `PhoneNo` varchar(15) DEFAULT NULL,
  `Specialization` varchar(100) DEFAULT NULL,
  `Department` varchar(100) DEFAULT NULL,
  `Qualification` varchar(100) DEFAULT NULL,
  `ConsultationFee` double DEFAULT NULL,
  `PatientCount` int(11) DEFAULT 0,
  `HospitalID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `doctor`
--

INSERT INTO `doctor` (`DoctorID`, `FirstName`, `LastName`, `Name`, `Username`, `Password`, `Email`, `PhoneNo`, `Specialization`, `Department`, `Qualification`, `ConsultationFee`, `PatientCount`, `HospitalID`) VALUES
(1, 'Anil', 'Deshmukh', 'Anil Deshmukh', 'dr.anil', 'doc@123', 'anil.deshmukh@citycare.com', '9900011001', 'General Medicine', 'General Medicine', 'MBBS, MD', 500, 2, 1),
(2, 'Priya', 'Nair', 'Priya Nair', 'dr.priya', 'doc@123', 'priya.nair@citycare.com', '9900011002', 'Cardiology', 'Cardiology', 'MBBS, DM Cardiology', 800, 2, 1),
(3, 'Suresh', 'Iyer', 'Suresh Iyer', 'dr.suresh', 'doc@123', 'suresh.iyer@citycare.com', '9900011003', 'Orthopedics', 'Orthopedics', 'MBBS, MS Ortho', 700, 2, 1),
(4, 'Neha', 'Kulkarni', 'Neha Kulkarni', 'dr.neha', 'doc@123', 'neha.kulkarni@sunrisehosp.com', '9900011004', 'Pediatrics', 'Pediatrics', 'MBBS, MD Peds', 600, 2, 2),
(5, 'Rajesh', 'Joshi', 'Rajesh Joshi', 'dr.rajesh', 'doc@123', 'rajesh.joshi@sunrisehosp.com', '9900011005', 'Nephrology', 'Nephrology', 'MBBS, DM Nephro', 900, 2, 2),
(6, 'Meera', 'Bhatt', 'Meera Bhatt', 'dr.meera', 'doc@123', 'meera.bhatt@sunrisehosp.com', '9900011006', 'Endocrinology', 'Endocrinology', 'MBBS, DM Endo', 850, 1, 2),
(7, 'Vikram', 'Menon', 'Vikram Menon', 'dr.vikram', 'doc@123', 'vikram.menon@lifelinemed.com', '9900011007', 'General Surgery', 'Surgery', 'MBBS, MS Surgery', 750, 3, 3),
(8, 'Divya', 'Subramaniam', 'Divya Subramaniam', 'dr.divya', 'doc@123', 'divya.subramaniam@lifelinemed.com', '9900011008', 'Pulmonology', 'Pulmonology', 'MBBS, MD Pulmo', 650, 2, 3),
(9, 'Rahul', 'Sharma', 'Rahul Sharma', 'rahul.doc', 'doc@123', 'rahul.sharma@gmail.com', '9876543210', 'Cardiologist', 'Cardiologist', 'MBBS, MD, DM (Cardiology)', 1200, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `equipment`
--

CREATE TABLE `equipment` (
  `EquipmentID` int(11) NOT NULL,
  `EquipmentName` varchar(100) DEFAULT NULL,
  `Status` varchar(30) DEFAULT NULL,
  `PurchaseDate` date DEFAULT NULL,
  `HospitalID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `equipment`
--

INSERT INTO `equipment` (`EquipmentID`, `EquipmentName`, `Status`, `PurchaseDate`, `HospitalID`) VALUES
(1, 'Hematology Analyzer', 'AVAILABLE', '2022-01-15', 1),
(2, 'Biochemistry Analyzer', 'AVAILABLE', '2022-02-10', 1),
(3, 'Glucometer', 'AVAILABLE', '2021-11-05', 1),
(4, 'Lipid Profile Analyzer', 'IN USE', '2022-03-20', 1),
(5, 'Urine Analyzer', 'AVAILABLE', '2022-04-18', 1),
(6, 'ECG Machine', 'AVAILABLE', '2021-09-12', 1),
(7, 'Hematology Analyzer', 'AVAILABLE', '2022-01-22', 2),
(8, 'Biochemistry Analyzer', 'IN USE', '2022-02-14', 2),
(9, 'Glucometer', 'AVAILABLE', '2021-10-08', 2),
(10, 'Renal Function Analyzer', 'AVAILABLE', '2022-05-01', 2),
(11, 'Thyroid Analyzer', 'AVAILABLE', '2022-06-11', 2),
(12, 'X-Ray Machine', 'AVAILABLE', '2021-08-25', 2),
(13, 'Hematology Analyzer', 'AVAILABLE', '2022-01-30', 3),
(14, 'Biochemistry Analyzer', 'AVAILABLE', '2022-02-25', 3),
(15, 'Glucometer', 'IN USE', '2021-12-03', 3),
(16, 'Liver Function Analyzer', 'AVAILABLE', '2022-04-09', 3),
(17, 'Pulmonary Function Analyzer', 'AVAILABLE', '2022-07-14', 3),
(18, 'CT Scan Machine', 'AVAILABLE', '2021-07-19', 3);

-- --------------------------------------------------------

--
-- Table structure for table `hospital`
--

CREATE TABLE `hospital` (
  `HospitalID` int(11) NOT NULL,
  `HospitalCode` varchar(20) DEFAULT NULL,
  `HospitalName` varchar(150) DEFAULT NULL,
  `Street` varchar(100) DEFAULT NULL,
  `City` varchar(50) DEFAULT NULL,
  `State` varchar(50) DEFAULT NULL,
  `Pincode` varchar(10) DEFAULT NULL,
  `PhoneNo` varchar(15) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `Status` varchar(20) DEFAULT NULL,
  `MasterAdminID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hospital`
--

INSERT INTO `hospital` (`HospitalID`, `HospitalCode`, `HospitalName`, `Street`, `City`, `State`, `Pincode`, `PhoneNo`, `Email`, `Status`, `MasterAdminID`) VALUES
(1, 'HSP001', 'City Care Hospital', 'MG Road', 'Ahmedabad', 'Gujarat', '380001', '07912345001', 'info@citycare.com', 'ACTIVE', 1),
(2, 'HSP002', 'Sunrise Multispeciality Hospital', 'FC Road', 'Pune', 'Maharashtra', '411005', '02026785002', 'contact@sunrisehosp.com', 'ACTIVE', 1),
(3, 'HSP003', 'Lifeline Medical Center', 'Anna Salai', 'Chennai', 'Tamil Nadu', '600002', '04428345003', 'care@lifelinemed.com', 'ACTIVE', 1),
(4, 'HSP004', 'MediPlus Hospital', 'SG Highway', 'Ahmedabad', 'Gujarat', '380060', '07940012345', 'mediplus@gmail.com', 'ACTIVE', 1);

-- --------------------------------------------------------

--
-- Table structure for table `labtechnician`
--

CREATE TABLE `labtechnician` (
  `LabTechID` int(11) NOT NULL,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `PhoneNo` varchar(15) DEFAULT NULL,
  `Qualification` varchar(100) DEFAULT NULL,
  `HospitalID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `labtechnician`
--

INSERT INTO `labtechnician` (`LabTechID`, `FirstName`, `LastName`, `Name`, `Username`, `Password`, `Email`, `PhoneNo`, `Qualification`, `HospitalID`) VALUES
(1, 'Ramesh', 'Kadam', 'Ramesh Kadam', 'lt.ramesh', 'lab@123', 'ramesh.kadam@citycare.com', '9911100001', 'B.Sc MLT', 1),
(2, 'Snehal', 'Gupta', 'Snehal Gupta', 'lt.snehal', 'lab@123', 'snehal.gupta@citycare.com', '9911100002', 'DMLT', 1),
(3, 'Manoj', 'Pawar', 'Manoj Pawar', 'lt.manoj', 'lab@123', 'manoj.pawar@sunrisehosp.com', '9911100003', 'B.Sc MLT', 2),
(4, 'Kavita', 'Salunkhe', 'Kavita Salunkhe', 'lt.kavita', 'lab@123', 'kavita.salunkhe@sunrisehosp.com', '9911100004', 'DMLT', 2),
(5, 'Arun', 'Pillai', 'Arun Pillai', 'lt.arun', 'lab@123', 'arun.pillai@lifelinemed.com', '9911100005', 'B.Sc MLT', 3),
(6, 'Deepa', 'Krishnan', 'Deepa Krishnan', 'lt.deepa', 'lab@123', 'deepa.krishnan@lifelinemed.com', '9911100006', 'DMLT', 3),
(7, 'Priya', 'Patel', 'Priya Patel', 'priya.lab', 'lab@123', 'priya.patel@gmail.com', '9988776655', 'B.Sc MLT', 1);

-- --------------------------------------------------------

--
-- Table structure for table `masteradmin`
--

CREATE TABLE `masteradmin` (
  `MasterAdminID` int(11) NOT NULL,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `PhoneNo` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `masteradmin`
--

INSERT INTO `masteradmin` (`MasterAdminID`, `FirstName`, `LastName`, `Name`, `Username`, `Password`, `Email`, `PhoneNo`) VALUES
(1, 'Arvind', 'Mehta', 'Arvind Mehta', 'arvind.admin', 'master@123', 'arvind.mehta@meditrack.com', '9876500001');

-- --------------------------------------------------------

--
-- Table structure for table `patient`
--

CREATE TABLE `patient` (
  `PatientID` int(11) NOT NULL,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `PhoneNo` varchar(15) DEFAULT NULL,
  `DOB` date DEFAULT NULL,
  `Gender` varchar(10) DEFAULT NULL,
  `BloodGroup` varchar(5) DEFAULT NULL,
  `Street` varchar(100) DEFAULT NULL,
  `City` varchar(50) DEFAULT NULL,
  `State` varchar(50) DEFAULT NULL,
  `Pincode` varchar(10) DEFAULT NULL,
  `FilePath` varchar(255) DEFAULT NULL,
  `HospitalID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `patient`
--

INSERT INTO `patient` (`PatientID`, `FirstName`, `LastName`, `Name`, `Username`, `Password`, `Email`, `PhoneNo`, `DOB`, `Gender`, `BloodGroup`, `Street`, `City`, `State`, `Pincode`, `FilePath`, `HospitalID`) VALUES
(1, 'Rohan', 'Shah', 'Rohan Shah', 'rohan.shah', 'pat@123', 'rohan.shah@gmail.com', '9822011001', '1990-05-14', 'Male', 'B+', 'Satellite Road', 'Ahmedabad', 'Gujarat', '380015', NULL, 1),
(2, 'Kavya', 'Trivedi', 'Kavya Trivedi', 'kavya.trivedi', 'pat@123', 'kavya.trivedi@gmail.com', '9822011002', '1985-11-30', 'Female', 'O+', 'Navrangpura', 'Ahmedabad', 'Gujarat', '380009', NULL, 1),
(3, 'Aditya', 'Rao', 'Aditya Rao', 'aditya.rao', 'pat@123', 'aditya.rao@gmail.com', '9822011003', '1978-02-20', 'Male', 'A+', 'Vastrapur', 'Ahmedabad', 'Gujarat', '380054', NULL, 1),
(4, 'Isha', 'Chokshi', 'Isha Chokshi', 'isha.chokshi', 'pat@123', 'isha.chokshi@gmail.com', '9822011004', '1995-07-08', 'Female', 'AB+', 'Bopal', 'Ahmedabad', 'Gujarat', '380058', NULL, 1),
(5, 'Manav', 'Joshi', 'Manav Joshi', 'manav.joshi', 'pat@123', 'manav.joshi@gmail.com', '9822011005', '2000-01-25', 'Male', 'B-', 'Maninagar', 'Ahmedabad', 'Gujarat', '380008', NULL, 1),
(6, 'Pooja', 'Desai', 'Pooja Desai', 'pooja.desai', 'pat@123', 'pooja.desai@gmail.com', '9822011006', '1988-09-17', 'Female', 'O-', 'Paldi', 'Ahmedabad', 'Gujarat', '380007', NULL, 1),
(7, 'Siddharth', 'Kulkarni', 'Siddharth Kulkarni', 'siddharth.k', 'pat@123', 'siddharth.kulkarni@gmail.com', '9822011007', '1992-03-11', 'Male', 'A-', 'Kothrud', 'Pune', 'Maharashtra', '411038', NULL, 2),
(8, 'Ananya', 'Deshpande', 'Ananya Deshpande', 'ananya.d', 'pat@123', 'ananya.deshpande@gmail.com', '9822011008', '1983-06-27', 'Female', 'B+', 'Aundh', 'Pune', 'Maharashtra', '411007', NULL, 2),
(9, 'Yash', 'Bhosale', 'Yash Bhosale', 'yash.bhosale', 'pat@123', 'yash.bhosale@gmail.com', '9822011009', '1975-12-05', 'Male', 'O+', 'Baner', 'Pune', 'Maharashtra', '411045', NULL, 2),
(10, 'Riya', 'Mane', 'Riya Mane', 'riya.mane', 'pat@123', 'riya.mane@gmail.com', '9822011010', '1998-04-19', 'Female', 'AB-', 'Hadapsar', 'Pune', 'Maharashtra', '411028', NULL, 2),
(11, 'Om', 'Kale', 'Om Kale', 'om.kale', 'pat@123', 'om.kale@gmail.com', '9822011011', '1990-10-02', 'Male', 'A+', 'Wakad', 'Pune', 'Maharashtra', '411057', NULL, 2),
(12, 'Lakshmi', 'Narayanan', 'Lakshmi Narayanan', 'lakshmi.n', 'pat@123', 'lakshmi.narayanan@gmail.com', '9822011012', '1980-08-14', 'Female', 'B+', 'T Nagar', 'Chennai', 'Tamil Nadu', '600017', NULL, 3),
(13, 'Karthikeyan', 'Subramani', 'Karthikeyan Subramani', 'karthik.s', 'pat@123', 'karthikeyan.s@gmail.com', '9822011013', '1993-02-28', 'Male', 'O+', 'Adyar', 'Chennai', 'Tamil Nadu', '600020', NULL, 3),
(14, 'Divya', 'Ramesh', 'Divya Ramesh', 'divya.ramesh', 'pat@123', 'divya.ramesh@gmail.com', '9822011014', '1997-11-09', 'Female', 'A+', 'Velachery', 'Chennai', 'Tamil Nadu', '600042', NULL, 3),
(15, 'Arjun', 'Pillai', 'Arjun Pillai', 'arjun.pillai', 'pat@123', 'arjun.pillai@gmail.com', '9822011015', '1986-05-22', 'Male', 'B-', 'Mylapore', 'Chennai', 'Tamil Nadu', '600004', NULL, 3),
(16, 'Swathi', 'Krishnan', 'Swathi Krishnan', 'swathi.k', 'pat@123', 'swathi.krishnan@gmail.com', '9822011016', '1991-07-30', 'Female', 'O-', 'Nungambakkam', 'Chennai', 'Tamil Nadu', '600034', NULL, 3),
(17, 'Akash', 'Verma', 'Akash Verma', 'akash.patient', 'patient@123', 'akash.verma@gmail.com', '9123456789', '2002-09-18', 'Male', 'O+', 'Satellite Road', 'Ahmedabad', 'Gujarat', '380015', NULL, 1);

-- --------------------------------------------------------

--
-- Stand-in structure for view `patientsummaryview`
-- (See below for the actual view)
--
CREATE TABLE `patientsummaryview` (
`PatientID` int(11)
,`FirstName` varchar(50)
,`LastName` varchar(50)
,`PatientName` varchar(100)
,`Age` int(11)
,`DOB` date
,`Gender` varchar(10)
,`BloodGroup` varchar(5)
,`PhoneNo` varchar(15)
,`Email` varchar(100)
,`Street` varchar(100)
,`City` varchar(50)
,`State` varchar(50)
,`Pincode` varchar(10)
,`HospitalID` int(11)
,`HospitalCode` varchar(20)
,`HospitalName` varchar(150)
,`AdmissionID` int(11)
,`AdmissionDate` date
,`DischargeDate` date
,`RoomNumber` varchar(10)
,`RoomType` varchar(20)
,`RoomCharge` double
,`Status` varchar(20)
,`DoctorID` int(11)
,`DoctorName` varchar(100)
,`Specialization` varchar(100)
,`AdminID` int(11)
);

-- --------------------------------------------------------

--
-- Table structure for table `report`
--

CREATE TABLE `report` (
  `ReportID` int(11) NOT NULL,
  `ResultValue` double DEFAULT NULL,
  `ResultStatus` varchar(20) DEFAULT NULL,
  `AnalysisDate` date DEFAULT NULL,
  `DoctorNotes` varchar(1000) DEFAULT NULL,
  `TestRequestID` int(11) DEFAULT NULL,
  `LabTechID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `report`
--

INSERT INTO `report` (`ReportID`, `ResultValue`, `ResultStatus`, `AnalysisDate`, `DoctorNotes`, `TestRequestID`, `LabTechID`) VALUES
(1, 14.5, 'NORMAL', '2026-01-07', 'Hemoglobin within normal range.', 1, 1),
(2, 92, 'NORMAL', '2026-01-07', 'Fasting blood sugar normal.', 2, 1),
(3, 2.1, 'CRITICAL', '2026-01-10', 'Significantly elevated creatinine, kidney function concern.', 3, 2),
(4, 260000, 'NORMAL', '2026-01-10', 'Platelet count normal.', 4, 2),
(5, 230, 'ABNORMAL', '2026-01-14', 'Cholesterol mildly elevated, dietary changes advised.', 5, 1),
(6, 178, 'ABNORMAL', '2026-01-14', 'Triglycerides slightly high.', 6, 1),
(7, 15200, 'CRITICAL', '2026-01-16', 'Very high WBC count, possible infection.', 7, 2),
(8, 78, 'NORMAL', '2026-01-17', 'ECG within normal limits.', 8, 2),
(9, 8, 'ABNORMAL', '2026-02-03', 'Pus cells slightly elevated, possible UTI.', 9, 1),
(10, 14, 'NORMAL', '2026-01-08', 'Hemoglobin normal.', 12, 3),
(11, 88, 'NORMAL', '2026-01-08', 'Fasting sugar normal.', 13, 3),
(12, 6.8, 'ABNORMAL', '2026-01-12', 'HbA1c elevated, indicates poor glycemic control.', 14, 4),
(13, 95, 'CRITICAL', '2026-01-12', 'Markedly elevated liver enzymes.', 15, 4),
(14, 15, 'NORMAL', '2026-01-16', 'BUN within normal range.', 16, 3),
(15, 1.6, 'ABNORMAL', '2026-01-16', 'Creatinine mildly elevated.', 17, 3),
(16, 2.5, 'NORMAL', '2026-02-04', 'Thyroid function normal.', 18, 4),
(17, 13.8, 'NORMAL', '2026-01-09', 'Hemoglobin normal.', 21, 5),
(18, 118, 'ABNORMAL', '2026-01-09', 'Fasting sugar mildly elevated, monitor diet.', 22, 5),
(19, 3.5, 'CRITICAL', '2026-01-13', 'Severely elevated bilirubin, urgent evaluation needed.', 23, 6),
(20, 28, 'NORMAL', '2026-01-13', 'SGOT within normal limits.', 24, 6),
(21, 175, 'NORMAL', '2026-01-18', 'Cholesterol normal.', 25, 5),
(22, 320000, 'NORMAL', '2026-01-18', 'Platelet count normal.', 26, 5),
(23, 12500, 'ABNORMAL', '2026-02-05', 'WBC count mildly elevated.', 27, 6),
(24, 130, 'NORMAL', '2026-02-09', 'Random blood sugar within acceptable range.', 29, 6);

-- --------------------------------------------------------

--
-- Table structure for table `testrequest`
--

CREATE TABLE `testrequest` (
  `TestRequestID` int(11) NOT NULL,
  `RequestDate` date DEFAULT NULL,
  `EquipmentUsageDate` date DEFAULT NULL,
  `Priority` varchar(20) DEFAULT NULL,
  `Status` varchar(20) DEFAULT NULL,
  `AdmissionID` int(11) DEFAULT NULL,
  `DoctorID` int(11) DEFAULT NULL,
  `TestTypeID` int(11) DEFAULT NULL,
  `EquipmentID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `testrequest`
--

INSERT INTO `testrequest` (`TestRequestID`, `RequestDate`, `EquipmentUsageDate`, `Priority`, `Status`, `AdmissionID`, `DoctorID`, `TestTypeID`, `EquipmentID`) VALUES
(1, '2026-01-05', '2026-01-06', 'NORMAL', 'COMPLETED', 1, 1, 1, 1),
(2, '2026-01-06', '2026-01-06', 'NORMAL', 'COMPLETED', 1, 1, 3, 3),
(3, '2026-01-08', '2026-01-09', 'EMERGENCY', 'COMPLETED', 2, 1, 5, 2),
(4, '2026-01-09', '2026-01-09', 'NORMAL', 'COMPLETED', 2, 1, 9, 1),
(5, '2026-01-12', '2026-01-13', 'NORMAL', 'PENDING', 3, 2, 6, 4),
(6, '2026-01-13', '2026-01-13', 'NORMAL', 'COMPLETED', 3, 2, 7, 4),
(7, '2026-01-15', '2026-01-15', 'EMERGENCY', 'COMPLETED', 4, 2, 2, 1),
(8, '2026-01-16', '2026-01-16', 'NORMAL', 'COMPLETED', 4, 2, 10, 6),
(9, '2026-02-01', '2026-02-02', 'NORMAL', 'COMPLETED', 5, 3, 8, 5),
(10, '2026-02-02', '2026-02-03', 'NORMAL', 'PROCESSING', 5, 3, 4, 3),
(11, '2026-02-05', '2026-02-06', 'EMERGENCY', 'PENDING', 6, 3, 1, 1),
(12, '2026-01-06', '2026-01-07', 'NORMAL', 'COMPLETED', 7, 4, 11, 7),
(13, '2026-01-07', '2026-01-07', 'NORMAL', 'COMPLETED', 7, 4, 13, 9),
(14, '2026-01-10', '2026-01-11', 'NORMAL', 'COMPLETED', 8, 4, 14, 9),
(15, '2026-01-11', '2026-01-11', 'EMERGENCY', 'COMPLETED', 8, 4, 19, 8),
(16, '2026-01-14', '2026-01-15', 'NORMAL', 'COMPLETED', 9, 5, 15, 10),
(17, '2026-01-15', '2026-01-15', 'EMERGENCY', 'COMPLETED', 9, 5, 16, 10),
(18, '2026-02-02', '2026-02-03', 'NORMAL', 'COMPLETED', 10, 5, 17, 11),
(19, '2026-02-03', '2026-02-04', 'NORMAL', 'PENDING', 10, 5, 18, 11),
(20, '2026-02-06', '2026-02-07', 'EMERGENCY', 'PROCESSING', 11, 6, 20, 12),
(21, '2026-01-07', '2026-01-08', 'NORMAL', 'COMPLETED', 12, 7, 21, 13),
(22, '2026-01-08', '2026-01-08', 'NORMAL', 'COMPLETED', 12, 7, 23, 15),
(23, '2026-01-11', '2026-01-12', 'EMERGENCY', 'COMPLETED', 13, 7, 25, 16),
(24, '2026-01-12', '2026-01-12', 'NORMAL', 'COMPLETED', 13, 7, 26, 16),
(25, '2026-01-16', '2026-01-17', 'NORMAL', 'COMPLETED', 14, 7, 27, 14),
(26, '2026-01-17', '2026-01-17', 'NORMAL', 'COMPLETED', 14, 7, 29, 13),
(27, '2026-02-03', '2026-02-04', 'NORMAL', 'COMPLETED', 15, 8, 22, 13),
(28, '2026-02-04', '2026-02-05', 'NORMAL', 'PENDING', 15, 8, 28, 17),
(29, '2026-02-07', '2026-02-08', 'EMERGENCY', 'COMPLETED', 16, 8, 24, 15),
(30, '2026-02-08', '2026-02-09', 'EMERGENCY', 'PROCESSING', 16, 8, 30, 18);

--
-- Triggers `testrequest`
--
DELIMITER $$
CREATE TRIGGER `UpdateEquipmentStatus` AFTER UPDATE ON `testrequest` FOR EACH ROW BEGIN

    IF NEW.Status = 'PROCESSING' THEN

        UPDATE Equipment
        SET Status = 'IN USE'
        WHERE EquipmentID = NEW.EquipmentID;

    END IF;

    IF NEW.Status = 'COMPLETED' THEN

        UPDATE Equipment
        SET Status = 'AVAILABLE'
        WHERE EquipmentID = NEW.EquipmentID;

    END IF;

END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `testtype`
--

CREATE TABLE `testtype` (
  `TestTypeID` int(11) NOT NULL,
  `TestName` varchar(100) DEFAULT NULL,
  `NormalMin` double DEFAULT NULL,
  `NormalMax` double DEFAULT NULL,
  `Unit` varchar(20) DEFAULT NULL,
  `TestCharge` double DEFAULT NULL,
  `HospitalID` int(11) DEFAULT NULL,
  `EquipmentID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `testtype`
--

INSERT INTO `testtype` (`TestTypeID`, `TestName`, `NormalMin`, `NormalMax`, `Unit`, `TestCharge`, `HospitalID`, `EquipmentID`) VALUES
(1, 'Hemoglobin', 13, 17, 'g/dL', 150, 1, 1),
(2, 'Total WBC Count', 4000, 11000, 'cells/mcL', 180, 1, 1),
(3, 'Fasting Blood Sugar', 70, 100, 'mg/dL', 120, 1, 3),
(4, 'Postprandial Blood Sugar', 70, 140, 'mg/dL', 130, 1, 3),
(5, 'Serum Creatinine', 0.6, 1.3, 'mg/dL', 200, 1, 2),
(6, 'Total Cholesterol', 125, 200, 'mg/dL', 250, 1, 4),
(7, 'Triglycerides', 40, 160, 'mg/dL', 220, 1, 4),
(8, 'Urine Routine Examination', 0, 5, 'pus cells/hpf', 100, 1, 5),
(9, 'Platelet Count', 150000, 450000, 'cells/mcL', 190, 1, 1),
(10, 'ECG Test', 60, 100, 'bpm', 300, 1, 6),
(11, 'Hemoglobin', 13, 17, 'g/dL', 160, 2, 7),
(12, 'Total WBC Count', 4000, 11000, 'cells/mcL', 190, 2, 7),
(13, 'Fasting Blood Sugar', 70, 100, 'mg/dL', 125, 2, 9),
(14, 'HbA1c', 4, 5.6, '%', 350, 2, 9),
(15, 'Blood Urea Nitrogen', 7, 20, 'mg/dL', 210, 2, 10),
(16, 'Serum Creatinine', 0.6, 1.3, 'mg/dL', 210, 2, 10),
(17, 'TSH (Thyroid)', 0.4, 4, 'uIU/mL', 400, 2, 11),
(18, 'T3', 80, 200, 'ng/dL', 380, 2, 11),
(19, 'Liver Function Test (SGPT)', 7, 56, 'U/L', 300, 2, 8),
(20, 'Chest X-Ray', 0, 1, 'finding', 450, 2, 12),
(21, 'Hemoglobin', 13, 17, 'g/dL', 150, 3, 13),
(22, 'Total WBC Count', 4000, 11000, 'cells/mcL', 180, 3, 13),
(23, 'Fasting Blood Sugar', 70, 100, 'mg/dL', 120, 3, 15),
(24, 'Random Blood Sugar', 70, 140, 'mg/dL', 120, 3, 15),
(25, 'Serum Bilirubin', 0.3, 1.2, 'mg/dL', 220, 3, 16),
(26, 'SGOT (AST)', 8, 40, 'U/L', 280, 3, 16),
(27, 'Total Cholesterol', 125, 200, 'mg/dL', 250, 3, 14),
(28, 'Pulmonary Function Test', 80, 120, '% predicted', 500, 3, 17),
(29, 'Platelet Count', 150000, 450000, 'cells/mcL', 190, 3, 13),
(30, 'CT Scan - Chest', 0, 1, 'finding', 2500, 3, 18);

-- --------------------------------------------------------

--
-- Structure for view `patientsummaryview`
--
DROP TABLE IF EXISTS `patientsummaryview`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `patientsummaryview`  AS SELECT `p`.`PatientID` AS `PatientID`, `p`.`FirstName` AS `FirstName`, `p`.`LastName` AS `LastName`, `p`.`Name` AS `PatientName`, `CalculateAge`(`p`.`DOB`) AS `Age`, `p`.`DOB` AS `DOB`, `p`.`Gender` AS `Gender`, `p`.`BloodGroup` AS `BloodGroup`, `p`.`PhoneNo` AS `PhoneNo`, `p`.`Email` AS `Email`, `p`.`Street` AS `Street`, `p`.`City` AS `City`, `p`.`State` AS `State`, `p`.`Pincode` AS `Pincode`, `h`.`HospitalID` AS `HospitalID`, `h`.`HospitalCode` AS `HospitalCode`, `h`.`HospitalName` AS `HospitalName`, `a`.`AdmissionID` AS `AdmissionID`, `a`.`AdmissionDate` AS `AdmissionDate`, `a`.`DischargeDate` AS `DischargeDate`, `a`.`RoomNumber` AS `RoomNumber`, `a`.`RoomType` AS `RoomType`, `a`.`RoomCharge` AS `RoomCharge`, `a`.`Status` AS `Status`, `d`.`DoctorID` AS `DoctorID`, `d`.`Name` AS `DoctorName`, `d`.`Specialization` AS `Specialization`, `a`.`AdminID` AS `AdminID` FROM (((`patient` `p` join `admission` `a` on(`p`.`PatientID` = `a`.`PatientID`)) join `doctor` `d` on(`a`.`DoctorID` = `d`.`DoctorID`)) join `hospital` `h` on(`a`.`HospitalID` = `h`.`HospitalID`)) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`AdminID`),
  ADD KEY `fk_admin_hospital` (`HospitalID`);

--
-- Indexes for table `admission`
--
ALTER TABLE `admission`
  ADD PRIMARY KEY (`AdmissionID`),
  ADD KEY `fk_admission_patient` (`PatientID`),
  ADD KEY `fk_admission_doctor` (`DoctorID`),
  ADD KEY `fk_admission_admin` (`AdminID`),
  ADD KEY `fk_admission_hospital` (`HospitalID`);

--
-- Indexes for table `bill`
--
ALTER TABLE `bill`
  ADD PRIMARY KEY (`BillID`),
  ADD UNIQUE KEY `AdmissionID` (`AdmissionID`);

--
-- Indexes for table `doctor`
--
ALTER TABLE `doctor`
  ADD PRIMARY KEY (`DoctorID`),
  ADD KEY `fk_doctor_hospital` (`HospitalID`);

--
-- Indexes for table `equipment`
--
ALTER TABLE `equipment`
  ADD PRIMARY KEY (`EquipmentID`),
  ADD KEY `fk_equipment_hospital` (`HospitalID`);

--
-- Indexes for table `hospital`
--
ALTER TABLE `hospital`
  ADD PRIMARY KEY (`HospitalID`),
  ADD KEY `fk_hospital_masteradmin` (`MasterAdminID`);

--
-- Indexes for table `labtechnician`
--
ALTER TABLE `labtechnician`
  ADD PRIMARY KEY (`LabTechID`),
  ADD KEY `fk_labtech_hospital` (`HospitalID`);

--
-- Indexes for table `masteradmin`
--
ALTER TABLE `masteradmin`
  ADD PRIMARY KEY (`MasterAdminID`);

--
-- Indexes for table `patient`
--
ALTER TABLE `patient`
  ADD PRIMARY KEY (`PatientID`),
  ADD KEY `fk_patient_hospital` (`HospitalID`);

--
-- Indexes for table `report`
--
ALTER TABLE `report`
  ADD PRIMARY KEY (`ReportID`),
  ADD UNIQUE KEY `TestRequestID` (`TestRequestID`),
  ADD KEY `fk_report_labtech` (`LabTechID`);

--
-- Indexes for table `testrequest`
--
ALTER TABLE `testrequest`
  ADD PRIMARY KEY (`TestRequestID`),
  ADD KEY `fk_testrequest_admission` (`AdmissionID`),
  ADD KEY `fk_testrequest_doctor` (`DoctorID`),
  ADD KEY `fk_testrequest_testtype` (`TestTypeID`),
  ADD KEY `fk_testrequest_equipment` (`EquipmentID`);

--
-- Indexes for table `testtype`
--
ALTER TABLE `testtype`
  ADD PRIMARY KEY (`TestTypeID`),
  ADD KEY `fk_testtype_hospital` (`HospitalID`),
  ADD KEY `fk_testtype_equipment` (`EquipmentID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `AdminID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `admission`
--
ALTER TABLE `admission`
  MODIFY `AdmissionID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `bill`
--
ALTER TABLE `bill`
  MODIFY `BillID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `doctor`
--
ALTER TABLE `doctor`
  MODIFY `DoctorID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `equipment`
--
ALTER TABLE `equipment`
  MODIFY `EquipmentID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `hospital`
--
ALTER TABLE `hospital`
  MODIFY `HospitalID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `labtechnician`
--
ALTER TABLE `labtechnician`
  MODIFY `LabTechID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `masteradmin`
--
ALTER TABLE `masteradmin`
  MODIFY `MasterAdminID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `patient`
--
ALTER TABLE `patient`
  MODIFY `PatientID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `report`
--
ALTER TABLE `report`
  MODIFY `ReportID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `testrequest`
--
ALTER TABLE `testrequest`
  MODIFY `TestRequestID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `testtype`
--
ALTER TABLE `testtype`
  MODIFY `TestTypeID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `fk_admin_hospital` FOREIGN KEY (`HospitalID`) REFERENCES `hospital` (`HospitalID`);

--
-- Constraints for table `admission`
--
ALTER TABLE `admission`
  ADD CONSTRAINT `fk_admission_admin` FOREIGN KEY (`AdminID`) REFERENCES `admin` (`AdminID`),
  ADD CONSTRAINT `fk_admission_doctor` FOREIGN KEY (`DoctorID`) REFERENCES `doctor` (`DoctorID`),
  ADD CONSTRAINT `fk_admission_hospital` FOREIGN KEY (`HospitalID`) REFERENCES `hospital` (`HospitalID`),
  ADD CONSTRAINT `fk_admission_patient` FOREIGN KEY (`PatientID`) REFERENCES `patient` (`PatientID`);

--
-- Constraints for table `bill`
--
ALTER TABLE `bill`
  ADD CONSTRAINT `fk_bill_admission` FOREIGN KEY (`AdmissionID`) REFERENCES `admission` (`AdmissionID`);

--
-- Constraints for table `doctor`
--
ALTER TABLE `doctor`
  ADD CONSTRAINT `fk_doctor_hospital` FOREIGN KEY (`HospitalID`) REFERENCES `hospital` (`HospitalID`);

--
-- Constraints for table `equipment`
--
ALTER TABLE `equipment`
  ADD CONSTRAINT `fk_equipment_hospital` FOREIGN KEY (`HospitalID`) REFERENCES `hospital` (`HospitalID`);

--
-- Constraints for table `hospital`
--
ALTER TABLE `hospital`
  ADD CONSTRAINT `fk_hospital_masteradmin` FOREIGN KEY (`MasterAdminID`) REFERENCES `masteradmin` (`MasterAdminID`);

--
-- Constraints for table `labtechnician`
--
ALTER TABLE `labtechnician`
  ADD CONSTRAINT `fk_labtech_hospital` FOREIGN KEY (`HospitalID`) REFERENCES `hospital` (`HospitalID`);

--
-- Constraints for table `patient`
--
ALTER TABLE `patient`
  ADD CONSTRAINT `fk_patient_hospital` FOREIGN KEY (`HospitalID`) REFERENCES `hospital` (`HospitalID`);

--
-- Constraints for table `report`
--
ALTER TABLE `report`
  ADD CONSTRAINT `fk_report_labtech` FOREIGN KEY (`LabTechID`) REFERENCES `labtechnician` (`LabTechID`),
  ADD CONSTRAINT `fk_report_testrequest` FOREIGN KEY (`TestRequestID`) REFERENCES `testrequest` (`TestRequestID`);

--
-- Constraints for table `testrequest`
--
ALTER TABLE `testrequest`
  ADD CONSTRAINT `fk_testrequest_admission` FOREIGN KEY (`AdmissionID`) REFERENCES `admission` (`AdmissionID`),
  ADD CONSTRAINT `fk_testrequest_doctor` FOREIGN KEY (`DoctorID`) REFERENCES `doctor` (`DoctorID`),
  ADD CONSTRAINT `fk_testrequest_equipment` FOREIGN KEY (`EquipmentID`) REFERENCES `equipment` (`EquipmentID`),
  ADD CONSTRAINT `fk_testrequest_testtype` FOREIGN KEY (`TestTypeID`) REFERENCES `testtype` (`TestTypeID`);

--
-- Constraints for table `testtype`
--
ALTER TABLE `testtype`
  ADD CONSTRAINT `fk_testtype_equipment` FOREIGN KEY (`EquipmentID`) REFERENCES `equipment` (`EquipmentID`),
  ADD CONSTRAINT `fk_testtype_hospital` FOREIGN KEY (`HospitalID`) REFERENCES `hospital` (`HospitalID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
