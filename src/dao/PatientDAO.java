package dao;

import database.DatabaseConnection;
import model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    Connection con = DatabaseConnection.getConnection();

    public boolean insertPatient(Patient p) {
        String query = "INSERT INTO Patient (PatientCode, FirstName, LastName, Name, Username, Password, Email, PhoneNo, " +
                "DOB, Gender, BloodGroup, Street, City, State, Pincode, HospitalID) " +
                "VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, p.getPatientCode());
            pstmt.setString(2, p.getFirstName());
            pstmt.setString(3, p.getLastName());
            pstmt.setString(4, p.getFirstName() + " " + p.getLastName());
            pstmt.setString(5, p.getUsername());
            pstmt.setString(6, p.getPassword());
            pstmt.setString(7, p.getEmail());
            pstmt.setString(8, p.getPhoneNo());
            pstmt.setString(9, p.getDob());
            pstmt.setString(10, p.getGender());
            pstmt.setString(11, p.getBloodGroup());
            pstmt.setString(12, p.getStreet());
            pstmt.setString(13, p.getCity());
            pstmt.setString(14, p.getState());
            pstmt.setString(15, p.getPincode());
            pstmt.setInt(16, p.getHospitalID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Patient: " + e.getMessage());
            return false;
        }
    }

    public Patient getPatientByUsername(String username, int hospitalId) {
        String query = "SELECT * FROM Patient WHERE Username = ? AND HospitalID = ?";

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

    public Patient getPatientById(int patientId) {
        String query = "SELECT * FROM Patient WHERE PatientID = ?";

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

    public Patient findReturningPatient(String name, String dob, int hospitalId) {
        String query = "SELECT * FROM Patient WHERE Name = ? AND DOB = ? AND HospitalID = ?";

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

    public List<Patient> getAllPatientsByHospital(int hospitalId) {
        List<Patient> patientList = new ArrayList<>();
        String query = "SELECT * FROM Patient WHERE HospitalID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                patientList.add(buildPatientFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Patients: " + e.getMessage());
        }
        return patientList;
    }

    public int calculateAge(String dob) {
        String query = "{? = call CalculateAge(?)}";

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

    public boolean updatePassword(int patientId, String newPassword) {
        String query = "UPDATE Patient SET Password = ? WHERE PatientID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, patientId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Patient password: " + e.getMessage());
            return false;
        }
    }

    public Patient getPatientByPhone(String phone) {
        String query = "SELECT * FROM Patient WHERE PhoneNo = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildPatientFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking Patient phone number: " + e.getMessage());
        }
        return null;
    }

    public Patient getPatientByEmail(String email) {
        String query = "SELECT * FROM Patient WHERE Email = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildPatientFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error checking Patient email: " + e.getMessage());
        }
        return null;
    }

    public String generatePatientCode(int hospitalID){
        String query = "SELECT COUNT(*) FROM Patient WHERE HospitalID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("PAT%03d", nextId);
            }

        } catch (SQLException e) {
            System.out.println("Error generating Patient Code: " + e.getMessage());
        }

        return null;
    }

    private Patient buildPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientID(rs.getInt("PatientID"));
        p.setPatientCode(rs.getString("PatientCode"));
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