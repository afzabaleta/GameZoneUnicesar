package com.gamezone.model;

/**
 * Represents the common information of a person in GameZoneUnicesar.
 */
public abstract class Person {

    private String name;
    private String identification;
    private String phone;

    /**
     * Creates a person with the specified information.
     *
     * @param name the person's name
     * @param identification the person's identification
     * @param phone the person's phone number
     */
    public Person(String name, String identification, String phone) {
        this.name = name;
        this.identification = identification;
        this.phone = phone;
    }

    /**
     * Returns the person's name.
     *
     * @return the person's name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the person's name.
     *
     * @param name the new person's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the person's identification.
     *
     * @return the person's identification
     */
    public String getIdentification() {
        return identification;
    }

    /**
     * Updates the person's identification.
     *
     * @param identification the new person's identification
     */
    public void setIdentification(String identification) {
        this.identification = identification;
    }

    /**
     * Returns the person's phone number.
     *
     * @return the person's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Updates the person's phone number.
     *
     * @param phone the new person's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}