package com.gamezone.model;

import java.util.List;
import java.util.ArrayList;
/**
 * Represents a generic accessory sold by the store.
 */
public abstract class Accessory extends Product {

    private List<Console> compatibleConsoles;

    /**
     * Creates an accessory with basic product information and compatible consoles.
     *
     * @param identifier unique identifier of the accessory
     * @param title accessory title
     * @param price accessory price
     * @param availableQuantity available quantity in stock
     * @param compatibleConsoles consoles compatible with this accessory
     */
    public Accessory(String identifier,
                     String title,
                     double price,
                     int availableQuantity,
                     List<Console> compatibleConsoles) {

        super(identifier, title, price, availableQuantity);
        this.compatibleConsoles = compatibleConsoles != null
                ? compatibleConsoles
                : new ArrayList<>();
    }


    /**
     * Returns the compatible consoles.
     *
     * @return list of compatible consoles
     */
    public List<Console> getCompatibleConsoles() {
        return compatibleConsoles;
    }


    /**
     * Updates the compatible consoles.
     *
     * @param compatibleConsoles new compatible consoles list
     */
    public void setCompatibleConsoles(List<Console> compatibleConsoles) {
        this.compatibleConsoles = compatibleConsoles;
    }


    /**
     * Returns the accessory description.
     *
     * @return accessory description
     */
    @Override
    public abstract String getDescription();
}
