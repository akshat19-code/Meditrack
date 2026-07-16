package model;

public class TestType {

    private int testTypeID;
    private String testName;
    private double normalMin;
    private double normalMax;
    private String unit;
    private double testCharge;
    private int hospitalID;
    private int equipmentID;

    public TestType() {}

    public TestType(int testTypeID, String testName, double normalMin,
                    double normalMax, String unit, double testCharge,
                    int hospitalID, int equipmentID) {
        this.testTypeID = testTypeID;
        this.testName = testName;
        this.normalMin = normalMin;
        this.normalMax = normalMax;
        this.unit = unit;
        this.testCharge = testCharge;
        this.hospitalID = hospitalID;
        this.equipmentID = equipmentID;
    }

    public int getTestTypeID() { return testTypeID; }
    public String getTestName() { return testName; }
    public double getNormalMin() { return normalMin; }
    public double getNormalMax() { return normalMax; }
    public String getUnit() { return unit; }
    public double getTestCharge() { return testCharge; }
    public int getHospitalID() { return hospitalID; }
    public int getEquipmentID() { return equipmentID; }

    public void setTestTypeID(int testTypeID) { this.testTypeID = testTypeID; }
    public void setTestName(String testName) { this.testName = testName; }
    public void setNormalMin(double normalMin) { this.normalMin = normalMin; }
    public void setNormalMax(double normalMax) { this.normalMax = normalMax; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setTestCharge(double testCharge) { this.testCharge = testCharge; }
    public void setHospitalID(int hospitalID) { this.hospitalID = hospitalID; }
    public void setEquipmentID(int equipmentID) { this.equipmentID = equipmentID; }

    public String toString() {
        return "Test ID: " + testTypeID +
                " | Test: " + testName +
                " | Normal Range: " + normalMin + " - " + normalMax +
                " " + unit +
                " | Charge: Rs." + testCharge;
    }
}