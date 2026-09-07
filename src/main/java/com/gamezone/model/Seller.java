package com.gamezone.model;

/**
 * Represents a seller in the GameZone system.
 * A seller is a person who works for the store and has an employee code
 * and a defined work shift.
 */
public class Seller extends Person {

    private String employeeCode;
    private String workShift;

    /**
     * Creates a seller with the specified personal and employment information.
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
     * @return the seller's employee code
     */
    public String getEmployeeCode() {
        return employeeCode;
    }

    /**
     * Returns the seller's work shift.
     *
     * @return the seller's work shift
     */
    public String getWorkShift() {
        return workShift;
    }
}