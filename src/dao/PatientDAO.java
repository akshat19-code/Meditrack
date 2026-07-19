package dao;

import database.DatabaseConnection;
import model.Patient;

import java.sql.*;

public class PatientDAO {

    // Insert a new Patient - called when Admin registers a patient
    public boolean insertPatient(Patient p) {
        String query = "INSERT INTO Patient (FirstName, LastName, Name, Username, Password, Email, PhoneNo, " +
                "DOB, Gender, BloodGroup, Street, City, State, Pincode, HospitalID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, p.getFirstName());
            pstmt.setString(2, p.getLastName());
            pstmt.setString(3, p.getFirstName() + " " + p.getLastName());
            pstmt.setString(4, p.getUsername());
            pstmt.setString(5, p.getPassword());
            pstmt.setString(6, p.getEmail());
            pstmt.setString(7, p.getPhoneNo());
            pstmt.setString(8, p.getDob());
            pstmt.setString(9, p.getGender());
            pstmt.setString(10, p.getBloodGroup());
            pstmt.setString(11, p.getStreet());
            pstmt.setString(12, p.getCity());
            pstmt.setString(13, p.getState());
            pstmt.setString(14, p.getPincode());
            pstmt.setInt(15, p.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Patient: " + e.getMessage());
            return false;
        }
    }

    // Fetch a Patient by Username within a specific Hospital - used for login
    public Patient getPatientByUsername(String username, int hospitalId) {
        String query = "SELECT * FROM Patient WHERE Username = ? AND HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildPatientFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Patient: " + e.getMessage());
        }
        return null;
    }

    // Fetch a Patient by ID - used constantly wherever PatientID needs full details
    // (Admission, TestRequest chain, report history, etc.)
    public Patient getPatientById(int patientId) {
        String query = "SELECT * FROM Patient WHERE PatientID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildPatientFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Patient: " + e.getMessage());
        }
        return null;
    }

    // Find a Patient by Name + DOB within a Hospital - used by Admin during registration
    // to check "is this a returning patient", so the system can suggest their previous doctor
    public Patient findReturningPatient(String name, String dob, int hospitalId) {
        String query = "SELECT * FROM Patient WHERE Name = ? AND DOB = ? AND HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, dob);
            pstmt.setInt(3, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildPatientFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking returning Patient: " + e.getMessage());
        }
        return null;
    }

    // Calculates a Patient's current age from their DOB using the database's
    // CalculateAge(DOB) function - called wherever age needs to be displayed,
    // since age is dynamic and should never be stored.
    public int calculateAge(String dob) {
        String query = "{? = call CalculateAge(?)}";

        Connection con = DatabaseConnection.getConnection();

        try (CallableStatement cstmt = con.prepareCall(query)) {

            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setString(2, dob);
            cstmt.execute();

            return cstmt.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error calculating age: " + e.getMessage());
            return -1;
        }
    }

    // Helper method - builds a Patient object from a ResultSet row
    private Patient buildPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientID(rs.getInt("PatientID"));
        p.setFirstName(rs.getString("FirstName"));
        p.setLastName(rs.getString("LastName"));
        p.setName(rs.getString("Name"));
        p.setUsername(rs.getString("Username"));
        p.setPassword(rs.getString("Password"));
        p.setEmail(rs.getString("Email"));
        p.setPhoneNo(rs.getString("PhoneNo"));
        p.setDob(rs.getString("DOB"));
        p.setGender(rs.getString("Gender"));
        p.setBloodGroup(rs.getString("BloodGroup"));
        p.setStreet(rs.getString("Street"));
        p.setCity(rs.getString("City"));
        p.setState(rs.getString("State"));
        p.setPincode(rs.getString("Pincode"));
        p.setHospitalID(rs.getInt("HospitalID"));
        return p;
    }
}