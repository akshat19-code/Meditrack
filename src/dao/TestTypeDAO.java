package dao;

import database.DatabaseConnection;
import model.TestType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestTypeDAO {

    // Insert a new TestType - called by TestTypeService after Equipment has been
    // resolved (either reused or newly created via EquipmentDAO)
    public boolean insertTestType(TestType tt) {
        String query = "INSERT INTO TestType (TestName, NormalMin, NormalMax, Unit, TestCharge, HospitalID, EquipmentID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, tt.getTestName());
            pstmt.setDouble(2, tt.getNormalMin());
            pstmt.setDouble(3, tt.getNormalMax());
            pstmt.setString(4, tt.getUnit());
            pstmt.setDouble(5, tt.getTestCharge());
            pstmt.setInt(6, tt.getHospitalID());
            pstmt.setInt(7, tt.getEquipmentID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting TestType: " + e.getMessage());
            return false;
        }
    }

    // Fetch a TestType by ID - used whenever TestRequest needs full test details
    // (e.g. Lab Tech checking NormalMin/NormalMax to analyse a result)
    public TestType getTestTypeById(int testTypeId) {
        String query = "SELECT * FROM TestType WHERE TestTypeID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, testTypeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildTestTypeFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching TestType: " + e.getMessage());
        }
        return null;
    }

    // Fetch all TestTypes in a Hospital - used when Doctor requests a test
    // (shown as a list to pick from) and when Admin views existing test types
    public List<TestType> getAllTestTypesByHospital(int hospitalId) {
        List<TestType> testTypeList = new ArrayList<>();
        String query = "SELECT * FROM TestType WHERE HospitalID = ?";

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                testTypeList.add(buildTestTypeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching TestTypes: " + e.getMessage());
        }
        return testTypeList;
    }

    // Helper method - builds a TestType object from a ResultSet row
    private TestType buildTestTypeFromResultSet(ResultSet rs) throws SQLException {
        TestType tt = new TestType();
        tt.setTestTypeID(rs.getInt("TestTypeID"));
        tt.setTestName(rs.getString("TestName"));
        tt.setNormalMin(rs.getDouble("NormalMin"));
        tt.setNormalMax(rs.getDouble("NormalMax"));
        tt.setUnit(rs.getString("Unit"));
        tt.setTestCharge(rs.getDouble("TestCharge"));
        tt.setHospitalID(rs.getInt("HospitalID"));
        tt.setEquipmentID(rs.getInt("EquipmentID"));
        return tt;
    }
}