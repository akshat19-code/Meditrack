-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 02, 2026 at 03:39 PM
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
(2, 'Sunita', 'Patil', 'Sunita Patil', 'sunita.admin', 'admin@123', 'sunita.patil@sunrisehosp.com', '9820011002', 2);

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
(1, '2026-06-01', NULL, '101', 'GENERAL', 1000, 'ADMITTED', 1, 1, 1, 1),
(2, '2026-06-03', NULL, '102', 'GENERAL', 1000, 'ADMITTED', 2, 1, 1, 1),
(3, '2026-06-05', NULL, '103', 'SEMI_PRIVATE', 1800, 'ADMITTED', 3, 2, 1, 1),
(4, '2026-06-07', NULL, '104', 'SEMI_PRIVATE', 1800, 'ADMITTED', 4, 2, 1, 1),
(5, '2026-06-10', NULL, '201', 'PRIVATE', 3000, 'ADMITTED', 5, 3, 1, 1),
(6, '2026-06-12', NULL, '202', 'PRIVATE', 3000, 'ADMITTED', 6, 3, 1, 1),
(7, '2026-06-15', NULL, '301', 'ICU', 5000, 'ADMITTED', 7, 4, 1, 1),
(8, '2026-06-18', NULL, '105', 'GENERAL', 1000, 'ADMITTED', 8, 5, 1, 1),
(9, '2026-01-05', '2026-01-10', '106', 'GENERAL', 1000, 'DISCHARGED', 9, 1, 1, 1),
(10, '2026-01-08', '2026-01-14', '203', 'PRIVATE', 3000, 'DISCHARGED', 10, 2, 1, 1),
(11, '2026-06-02', NULL, '401', 'GENERAL', 1100, 'ADMITTED', 11, 6, 2, 2),
(12, '2026-06-04', NULL, '402', 'GENERAL', 1100, 'ADMITTED', 12, 6, 2, 2),
(13, '2026-06-06', NULL, '403', 'SEMI_PRIVATE', 1900, 'ADMITTED', 13, 7, 2, 2),
(14, '2026-06-09', NULL, '404', 'SEMI_PRIVATE', 1900, 'ADMITTED', 14, 7, 2, 2),
(15, '2026-06-11', NULL, '501', 'PRIVATE', 3200, 'ADMITTED', 15, 8, 2, 2),
(16, '2026-06-13', NULL, '502', 'PRIVATE', 3200, 'ADMITTED', 16, 8, 2, 2),
(17, '2026-06-16', NULL, '601', 'ICU', 5200, 'ADMITTED', 17, 9, 2, 2),
(18, '2026-06-19', NULL, '405', 'GENERAL', 1100, 'ADMITTED', 18, 10, 2, 2),
(19, '2026-01-06', '2026-01-11', '406', 'GENERAL', 1100, 'DISCHARGED', 19, 6, 2, 2),
(20, '2026-01-10', '2026-01-16', '503', 'PRIVATE', 3200, 'DISCHARGED', 20, 7, 2, 2);

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
(1, 1000, 500, 270, 1770, '2026-01-10', 9),
(2, 3000, 550, 550, 4100, '2026-01-14', 10),
(3, 1100, 500, 285, 1885, '2026-01-11', 19),
(4, 3200, 520, 580, 4300, '2026-01-16', 20);

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
(2, 'Kavita', 'Rane', 'Kavita Rane', 'dr.kavita', 'doc@123', 'kavita.rane@citycare.com', '9900011002', 'General Medicine', 'General Medicine', 'MBBS, MD', 550, 2, 1),
(3, 'Priya', 'Nair', 'Priya Nair', 'dr.priya', 'doc@123', 'priya.nair@citycare.com', '9900011003', 'Cardiology', 'Cardiology', 'MBBS, DM Cardiology', 800, 2, 1),
(4, 'Rohit', 'Bhatt', 'Rohit Bhatt', 'dr.rohit', 'doc@123', 'rohit.bhatt@citycare.com', '9900011004', 'Cardiology', 'Cardiology', 'MBBS, DM Cardiology', 850, 1, 1),
(5, 'Suresh', 'Iyer', 'Suresh Iyer', 'dr.suresh', 'doc@123', 'suresh.iyer@citycare.com', '9900011005', 'Neurology', 'Neurology', 'MBBS, DM Neurology', 900, 1, 1),
(6, 'Neha', 'Kulkarni', 'Neha Kulkarni', 'dr.neha', 'doc@123', 'neha.kulkarni@sunrisehosp.com', '9900022001', 'General Medicine', 'General Medicine', 'MBBS, MD', 500, 2, 2),
(7, 'Amit', 'Deshpande', 'Amit Deshpande', 'dr.amit', 'doc@123', 'amit.deshpande@sunrisehosp.com', '9900022002', 'General Medicine', 'General Medicine', 'MBBS, MD', 520, 2, 2),
(8, 'Rajesh', 'Joshi', 'Rajesh Joshi', 'dr.rajesh', 'doc@123', 'rajesh.joshi@sunrisehosp.com', '9900022003', 'Orthopedics', 'Orthopedics', 'MBBS, MS Ortho', 700, 2, 2),
(9, 'Meera', 'Bhatt', 'Meera Bhatt', 'dr.meera', 'doc@123', 'meera.bhatt@sunrisehosp.com', '9900022004', 'Orthopedics', 'Orthopedics', 'MBBS, MS Ortho', 750, 1, 2),
(10, 'Sanjay', 'Kale', 'Sanjay Kale', 'dr.sanjay', 'doc@123', 'sanjay.kale@sunrisehosp.com', '9900022005', 'Pediatrics', 'Pediatrics', 'MBBS, MD Peds', 600, 1, 2);

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
(1, 'Hematology Analyzer', 'IN USE', '2023-01-15', 1),
(2, 'Blood Sugar Analyzer', 'AVAILABLE', '2023-02-10', 1),
(3, 'ECG Machine', 'AVAILABLE', '2022-11-05', 1),
(4, 'Lipid Analyzer', 'AVAILABLE', '2023-03-20', 1),
(5, 'Urine Analyzer', 'AVAILABLE', '2023-04-18', 1),
(6, 'X-Ray Machine', 'IN USE', '2022-09-12', 1),
(7, 'Renal Function Analyzer', 'AVAILABLE', '2023-05-01', 1),
(8, 'Hematology Analyzer', 'IN USE', '2023-01-22', 2),
(9, 'Blood Sugar Analyzer', 'AVAILABLE', '2023-02-14', 2),
(10, 'ECG Machine', 'AVAILABLE', '2022-10-08', 2),
(11, 'Lipid Analyzer', 'AVAILABLE', '2023-05-11', 2),
(12, 'Urine Analyzer', 'AVAILABLE', '2023-06-01', 2),
(13, 'X-Ray Machine', 'IN USE', '2022-08-25', 2),
(14, 'Renal Function Analyzer', 'AVAILABLE', '2023-04-09', 2);

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
(2, 'HSP002', 'Sunrise Multispeciality Hospital', 'FC Road', 'Pune', 'Maharashtra', '411005', '02026785002', 'contact@sunrisehosp.com', 'ACTIVE', 1);

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
(4, 'Kavita', 'Salunkhe', 'Kavita Salunkhe', 'lt.kavita', 'lab@123', 'kavita.salunkhe@sunrisehosp.com', '9911100004', 'DMLT', 2);

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
(1, 'Rohan', 'Shah', 'Rohan Shah', 'rohan.shah', 'pat@123', 'rohan.shah@gmail.com', '9822011001', '1990-05-14', 'MALE', 'B+', 'Satellite Road', 'Ahmedabad', 'Gujarat', '380015', NULL, 1),
(2, 'Kavya', 'Trivedi', 'Kavya Trivedi', 'kavya.trivedi', 'pat@123', 'kavya.trivedi@gmail.com', '9822011002', '1985-11-30', 'FEMALE', 'O+', 'Navrangpura', 'Ahmedabad', 'Gujarat', '380009', NULL, 1),
(3, 'Aditya', 'Rao', 'Aditya Rao', 'aditya.rao', 'pat@123', 'aditya.rao@gmail.com', '9822011003', '1978-02-20', 'MALE', 'A+', 'Vastrapur', 'Ahmedabad', 'Gujarat', '380054', NULL, 1),
(4, 'Isha', 'Chokshi', 'Isha Chokshi', 'isha.chokshi', 'pat@123', 'isha.chokshi@gmail.com', '9822011004', '1995-07-08', 'FEMALE', 'AB+', 'Bopal', 'Ahmedabad', 'Gujarat', '380058', NULL, 1),
(5, 'Manav', 'Joshi', 'Manav Joshi', 'manav.joshi', 'pat@123', 'manav.joshi@gmail.com', '9822011005', '2000-01-25', 'MALE', 'B-', 'Maninagar', 'Ahmedabad', 'Gujarat', '380008', NULL, 1),
(6, 'Pooja', 'Desai', 'Pooja Desai', 'pooja.desai', 'pat@123', 'pooja.desai@gmail.com', '9822011006', '1988-09-17', 'FEMALE', 'O-', 'Paldi', 'Ahmedabad', 'Gujarat', '380007', NULL, 1),
(7, 'Akash', 'Verma', 'Akash Verma', 'akash.patient', 'patient@123', 'akash.verma@gmail.com', '9123456789', '2002-09-18', 'MALE', 'O+', 'Satellite Road', 'Ahmedabad', 'Gujarat', '380015', NULL, 1),
(8, 'Neha', 'Thakkar', 'Neha Thakkar', 'neha.thakkar', 'pat@123', 'neha.thakkar@gmail.com', '9822011008', '1993-03-12', 'FEMALE', 'A-', 'Chandkheda', 'Ahmedabad', 'Gujarat', '382424', NULL, 1),
(9, 'Vivek', 'Mehta', 'Vivek Mehta', 'vivek.mehta', 'pat@123', 'vivek.mehta@gmail.com', '9822011009', '1982-12-01', 'MALE', 'B+', 'Naranpura', 'Ahmedabad', 'Gujarat', '380013', NULL, 1),
(10, 'Riya', 'Patel', 'Riya Patel', 'riya.patel', 'pat@123', 'riya.patel@gmail.com', '9822011010', '1999-06-22', 'FEMALE', 'AB-', 'Thaltej', 'Ahmedabad', 'Gujarat', '380059', NULL, 1),
(11, 'Siddharth', 'Kulkarni', 'Siddharth Kulkarni', 'siddharth.k', 'pat@123', 'siddharth.kulkarni@gmail.com', '9822011011', '1992-03-11', 'MALE', 'A-', 'Kothrud', 'Pune', 'Maharashtra', '411038', NULL, 2),
(12, 'Ananya', 'Deshpande', 'Ananya Deshpande', 'ananya.d', 'pat@123', 'ananya.deshpande@gmail.com', '9822011012', '1983-06-27', 'FEMALE', 'B+', 'Aundh', 'Pune', 'Maharashtra', '411007', NULL, 2),
(13, 'Yash', 'Bhosale', 'Yash Bhosale', 'yash.bhosale', 'pat@123', 'yash.bhosale@gmail.com', '9822011013', '1975-12-05', 'MALE', 'O+', 'Baner', 'Pune', 'Maharashtra', '411045', NULL, 2),
(14, 'Riya', 'Mane', 'Riya Mane', 'riya.mane', 'pat@123', 'riya.mane@gmail.com', '9822011014', '1998-04-19', 'FEMALE', 'AB-', 'Hadapsar', 'Pune', 'Maharashtra', '411028', NULL, 2),
(15, 'Om', 'Kale', 'Om Kale', 'om.kale', 'pat@123', 'om.kale@gmail.com', '9822011015', '1990-10-02', 'MALE', 'A+', 'Wakad', 'Pune', 'Maharashtra', '411057', NULL, 2),
(16, 'Sneha', 'Jadhav', 'Sneha Jadhav', 'sneha.jadhav', 'pat@123', 'sneha.jadhav@gmail.com', '9822011016', '1987-08-15', 'FEMALE', 'B-', 'Kharadi', 'Pune', 'Maharashtra', '411014', NULL, 2),
(17, 'Rahul', 'Pawar', 'Rahul Pawar', 'rahul.pawar', 'pat@123', 'rahul.pawar@gmail.com', '9822011017', '1994-01-09', 'MALE', 'O-', 'Viman Nagar', 'Pune', 'Maharashtra', '411014', NULL, 2),
(18, 'Pallavi', 'More', 'Pallavi More', 'pallavi.more', 'pat@123', 'pallavi.more@gmail.com', '9822011018', '1996-11-27', 'FEMALE', 'A+', 'Shivaji Nagar', 'Pune', 'Maharashtra', '411005', NULL, 2),
(19, 'Nikhil', 'Gaikwad', 'Nikhil Gaikwad', 'nikhil.gaikwad', 'pat@123', 'nikhil.gaikwad@gmail.com', '9822011019', '1980-05-03', 'MALE', 'B+', 'Camp', 'Pune', 'Maharashtra', '411001', NULL, 2),
(20, 'Swati', 'Kulkarni', 'Swati Kulkarni', 'swati.kulkarni', 'pat@123', 'swati.kulkarni@gmail.com', '9822011020', '1991-07-19', 'FEMALE', 'O+', 'Deccan', 'Pune', 'Maharashtra', '411004', NULL, 2);

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
  `DoctorNotes` varchar(2000) DEFAULT NULL,
  `TestRequestID` int(11) DEFAULT NULL,
  `LabTechID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `report`
--

INSERT INTO `report` (`ReportID`, `ResultValue`, `ResultStatus`, `AnalysisDate`, `DoctorNotes`, `TestRequestID`, `LabTechID`) VALUES
(1, 14.2, 'NORMAL', '2026-06-02', 'Hemoglobin within normal range.', 1, 1),
(2, 95, 'NORMAL', '2026-06-03', NULL, 2, 1),
(3, 105, 'ABNORMAL', '2026-06-04', NULL, 3, 2),
(4, 210, 'ABNORMAL', '2026-06-07', 'Cholesterol mildly elevated, dietary changes advised.', 4, 1),
(5, 9.5, 'CRITICAL', '2026-06-11', 'Severely low hemoglobin, urgent evaluation required.', 7, 2),
(6, 3, 'NORMAL', '2026-06-12', NULL, 8, 1),
(7, 130, 'CRITICAL', '2026-06-16', 'Markedly abnormal heart rate, immediate attention needed.', 10, 2),
(8, 9500, 'NORMAL', '2026-06-19', NULL, 12, 1),
(9, 13.8, 'NORMAL', '2026-01-06', 'Hemoglobin normal.', 13, 1),
(10, 92, 'NORMAL', '2026-01-07', 'Fasting sugar normal.', 14, 1),
(11, 205, 'ABNORMAL', '2026-01-09', 'Cholesterol mildly elevated, discussed diet plan.', 15, 2),
(12, 78, 'NORMAL', '2026-01-09', 'ECG within normal limits.', 16, 2),
(13, 15, 'NORMAL', '2026-06-03', NULL, 17, 3),
(14, 88, 'NORMAL', '2026-06-04', NULL, 18, 3),
(15, 60, 'NORMAL', '2026-06-05', NULL, 19, 4),
(16, 190, 'NORMAL', '2026-06-07', NULL, 20, 3),
(17, 8.9, 'CRITICAL', '2026-06-12', 'Severely low hemoglobin, urgent transfusion evaluation.', 23, 4),
(18, 6, 'ABNORMAL', '2026-06-13', 'Pus cells elevated, possible urinary infection.', 24, 3),
(19, 45, 'CRITICAL', '2026-06-17', 'Critically low heart rate, urgent cardiology review.', 26, 4),
(20, 10800, 'NORMAL', '2026-06-20', NULL, 28, 3),
(21, 13.5, 'NORMAL', '2026-01-07', 'Hemoglobin normal.', 29, 3),
(22, 99, 'NORMAL', '2026-01-08', 'Fasting sugar normal.', 30, 3),
(23, 198, 'NORMAL', '2026-01-11', 'Cholesterol normal.', 31, 4),
(24, 92, 'NORMAL', '2026-01-11', 'ECG within normal limits.', 32, 4);

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
(1, '2026-06-01', '2026-06-02', 'NORMAL', 'COMPLETED', 1, 1, 1, 1),
(2, '2026-06-02', '2026-06-02', 'NORMAL', 'COMPLETED', 1, 1, 3, 2),
(3, '2026-06-03', '2026-06-03', 'EMERGENCY', 'COMPLETED', 2, 1, 4, 3),
(4, '2026-06-05', '2026-06-06', 'NORMAL', 'COMPLETED', 3, 2, 5, 4),
(5, '2026-06-06', '2026-06-07', 'NORMAL', 'PENDING', 3, 2, 8, 7),
(6, '2026-06-07', '2026-06-08', 'NORMAL', 'PROCESSING', 4, 2, 2, 1),
(7, '2026-06-10', '2026-06-10', 'EMERGENCY', 'COMPLETED', 5, 3, 1, 1),
(8, '2026-06-11', '2026-06-11', 'NORMAL', 'COMPLETED', 5, 3, 6, 5),
(9, '2026-06-12', '2026-06-13', 'NORMAL', 'PENDING', 6, 3, 3, 2),
(10, '2026-06-15', '2026-06-15', 'EMERGENCY', 'COMPLETED', 7, 4, 4, 3),
(11, '2026-06-16', '2026-06-16', 'EMERGENCY', 'PROCESSING', 7, 4, 7, 6),
(12, '2026-06-18', '2026-06-19', 'NORMAL', 'COMPLETED', 8, 5, 2, 1),
(13, '2026-01-05', '2026-01-06', 'NORMAL', 'COMPLETED', 9, 1, 1, 1),
(14, '2026-01-06', '2026-01-07', 'NORMAL', 'COMPLETED', 9, 1, 3, 2),
(15, '2026-01-08', '2026-01-09', 'NORMAL', 'COMPLETED', 10, 2, 5, 4),
(16, '2026-01-09', '2026-01-09', 'EMERGENCY', 'COMPLETED', 10, 2, 4, 3),
(17, '2026-06-02', '2026-06-03', 'NORMAL', 'COMPLETED', 11, 6, 9, 8),
(18, '2026-06-03', '2026-06-03', 'NORMAL', 'COMPLETED', 11, 6, 11, 9),
(19, '2026-06-04', '2026-06-04', 'EMERGENCY', 'COMPLETED', 12, 6, 12, 10),
(20, '2026-06-06', '2026-06-07', 'NORMAL', 'COMPLETED', 13, 7, 13, 11),
(21, '2026-06-07', '2026-06-08', 'NORMAL', 'PENDING', 13, 7, 16, 14),
(22, '2026-06-09', '2026-06-10', 'NORMAL', 'PROCESSING', 14, 7, 10, 8),
(23, '2026-06-11', '2026-06-11', 'EMERGENCY', 'COMPLETED', 15, 8, 9, 8),
(24, '2026-06-12', '2026-06-12', 'NORMAL', 'COMPLETED', 15, 8, 14, 12),
(25, '2026-06-13', '2026-06-14', 'NORMAL', 'PENDING', 16, 8, 11, 9),
(26, '2026-06-16', '2026-06-16', 'EMERGENCY', 'COMPLETED', 17, 9, 12, 10),
(27, '2026-06-17', '2026-06-17', 'EMERGENCY', 'PROCESSING', 17, 9, 15, 13),
(28, '2026-06-19', '2026-06-20', 'NORMAL', 'COMPLETED', 18, 10, 10, 8),
(29, '2026-01-06', '2026-01-07', 'NORMAL', 'COMPLETED', 19, 6, 9, 8),
(30, '2026-01-07', '2026-01-08', 'NORMAL', 'COMPLETED', 19, 6, 11, 9),
(31, '2026-01-10', '2026-01-11', 'NORMAL', 'COMPLETED', 20, 7, 13, 11),
(32, '2026-01-11', '2026-01-11', 'EMERGENCY', 'COMPLETED', 20, 7, 12, 10);

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
(2, 'Complete Blood Count (CBC)', 4000, 11000, 'cells/mcL', 200, 1, 1),
(3, 'Fasting Blood Sugar', 70, 100, 'mg/dL', 120, 1, 2),
(4, 'ECG Test', 60, 100, 'bpm', 300, 1, 3),
(5, 'Lipid Profile', 125, 200, 'mg/dL', 250, 1, 4),
(6, 'Urine Routine Examination', 0, 5, 'pus cells/hpf', 100, 1, 5),
(7, 'Chest X-Ray', 0, 1, 'finding', 450, 1, 6),
(8, 'Kidney Function Test (Creatinine)', 0.6, 1.3, 'mg/dL', 200, 1, 7),
(9, 'Hemoglobin', 13, 17, 'g/dL', 160, 2, 8),
(10, 'Complete Blood Count (CBC)', 4000, 11000, 'cells/mcL', 210, 2, 8),
(11, 'Fasting Blood Sugar', 70, 100, 'mg/dL', 125, 2, 9),
(12, 'ECG Test', 60, 100, 'bpm', 320, 2, 10),
(13, 'Lipid Profile', 125, 200, 'mg/dL', 260, 2, 11),
(14, 'Urine Routine Examination', 0, 5, 'pus cells/hpf', 110, 2, 12),
(15, 'Chest X-Ray', 0, 1, 'finding', 470, 2, 13),
(16, 'Kidney Function Test (Creatinine)', 0.6, 1.3, 'mg/dL', 210, 2, 14);

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
  MODIFY `AdminID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `admission`
--
ALTER TABLE `admission`
  MODIFY `AdmissionID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `bill`
--
ALTER TABLE `bill`
  MODIFY `BillID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `doctor`
--
ALTER TABLE `doctor`
  MODIFY `DoctorID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `equipment`
--
ALTER TABLE `equipment`
  MODIFY `EquipmentID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `hospital`
--
ALTER TABLE `hospital`
  MODIFY `HospitalID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `labtechnician`
--
ALTER TABLE `labtechnician`
  MODIFY `LabTechID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `masteradmin`
--
ALTER TABLE `masteradmin`
  MODIFY `MasterAdminID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `patient`
--
ALTER TABLE `patient`
  MODIFY `PatientID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `report`
--
ALTER TABLE `report`
  MODIFY `ReportID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `testrequest`
--
ALTER TABLE `testrequest`
  MODIFY `TestRequestID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `testtype`
--
ALTER TABLE `testtype`
  MODIFY `TestTypeID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

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
