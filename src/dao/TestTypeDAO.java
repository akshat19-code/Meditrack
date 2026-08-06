package dao;

import database.DatabaseConnection;
import model.TestType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestTypeDAO {

    Connection con = DatabaseConnection.getConnection();

    public boolean insertTestType(TestType tt) {
        String query = "INSERT INTO TestType (TestTypeCode, TestName, NormalMin, NormalMax, Unit, TestCharge, HospitalID, EquipmentID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, tt.getTestTypeCode());
            pstmt.setString(2, tt.getTestName());
            pstmt.setDouble(3, tt.getNormalMin());
            pstmt.setDouble(4, tt.getNormalMax());
            pstmt.setString(5, tt.getUnit());
            pstmt.setDouble(6, tt.getTestCharge());
            pstmt.setInt(7, tt.getHospitalID());
            pstmt.setInt(8, tt.getEquipmentID());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting TestType: " + e.getMessage());
            return false;
        }
    }

    public TestType getTestTypeById(int testTypeId) {
        String query = "SELECT * FROM TestType WHERE TestTypeID = ?";

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

    public List<TestType> getAllTestTypesByHospital(int hospitalId) {
        List<TestType> testTypeList = new ArrayList<>();
        String query = "SELECT * FROM TestType WHERE HospitalID = ?";

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

    public String generateTestTypeCode(int hospitalID){
        String query = "SELECT COUNT(*) FROM TestType WHERE HospitalID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, hospitalID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("TST%03d", nextId);
            }

        } catch (SQLException e) {
            System.out.println("Error generating TestType Code: " + e.getMessage());
        }

        return null;
    }

    private TestType buildTestTypeFromResultSet(ResultSet rs) throws SQLException {
        TestType tt = new TestType();
        tt.setTestTypeID(rs.getInt("TestTypeID"));
        tt.setTestTypeCode(rs.getString("TestTypeCode"));
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