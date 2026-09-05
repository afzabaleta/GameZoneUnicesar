package com.gamezone.model;

import java.io.Serializable;

/**
 * Represents a brand associated with a product.
 */
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String country;

    /**
     * Creates a brand with its name and country.
     *
     * @param name the brand name
     * @param country the country of origin
     */
    public Brand(String name, String country) {
        this.name = name;
        this.country = country;
    }

    /**
     * Returns the brand name.
     *
     * @return the brand name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the brand name.
     *
     * @param name the new brand name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the country of the brand.
     *
     * @return the country of origin
     */
    public String getCountry() {
        return country;
    }

    /**
     * Updates the country of the brand.
     *
     * @param country the new country of origin
     */
    public void setCountry(String country) {
        this.country = country;
    }
}