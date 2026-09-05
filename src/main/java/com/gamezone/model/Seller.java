package com.gamezone.model;

/**
 * Represents a seller in GameZoneUnicesar.
 */
public class Seller extends Person {

    private String employeeCode;
    private String workShift;

    /**
     * Creates a seller with the specified information.
     *
     * @param name the seller's name
     * @param identification the seller's identification
     * @param phone the seller's phone number
     * @param employeeCode the seller's employee code
     * @param workShift the seller's work shift
     */
    public Seller(String name, String identification, String phone,
                  String employeeCode, String workShift) {
        super(name, identification, phone);
        this.employeeCode = employeeCode;
        this.workShift = workShift;
    }

    /**
     * Returns the seller's employee code.
     *
     * @return the employee code
     */
    public String getEmployeeCode() {
        return employeeCode;
    }

    /**
     * Updates the seller's employee code.
     *
     * @param employeeCode the new employee code
     */
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    /**
     * Returns the seller's work shift.
     *
     * @return the work shift
     */
    public String getWorkShift() {
        return workShift;
    }

    /**
     * Updates the seller's work shift.
     *
     * @param workShift the new work shift
     */
    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }
}