package com.gamezone.model;

/**
 * Represents the common information shared by people in the GameZone system.
 * This class provides the basic personal data inherited by customers and sellers.
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
     * @throws IllegalArgumentException if any required field is null or blank
     */
    public Person(String name, String identification, String phone) {
        validateField(name, "name");
        validateField(identification, "identification");
        validateField(phone, "phone");

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
     * @param name the new name
     * @throws IllegalArgumentException if the name is null or blank
     */
    public void setName(String name) {
        validateField(name, "name");
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
     * @param phone the new phone number
     * @throws IllegalArgumentException if the phone is null or blank
     */
    public void setPhone(String phone) {
        validateField(phone, "phone");
        this.phone = phone;
    }

    /**
     * Validates a required person field.
     *
     * @param value the value to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if the value is null or blank
     */
    private void validateField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null or blank"
            );
        }
    }
}