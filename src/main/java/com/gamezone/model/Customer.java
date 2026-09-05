package com.gamezone.model;

/**
 * Represents a customer in GameZoneUnicesar.
 */
public class Customer extends Person {

    private String email;

    /**
     * Creates a customer with the specified information.
     *
     * @param name the customer's name
     * @param identification the customer's identification
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public Customer(String name, String identification, String phone, String email) {
        super(name, identification, phone);
        this.email = email;
    }

    /**
     * Returns the customer's email address.
     *
     * @return the customer's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the customer's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
}