package com.gamezone.model;

import java.util.List;

/**
 * Represents a controller accessory.
 */
public class Controller extends Accessory {

    private String connectionType;

    /**
     * Creates a controller accessory.
     *
     * @param identifier unique identifier
     * @param title controller title
     * @param price controller price
     * @param availableQuantity available quantity
     * @param connectionType connection type (wireless/wired)
     * @param compatibleConsoles compatible consoles
     */
    public Controller(String identifier,
                      String title,
                      double price,
                      int availableQuantity,
                      String connectionType,
                      List<Console> compatibleConsoles) {

        super(identifier, title, price, availableQuantity, compatibleConsoles);
        this.connectionType = connectionType;
    }


    /**
     * Returns the connection type.
     *
     * @return connection type
     */
    public String getConnectionType() {
        return connectionType;
    }


    /**
     * Updates the connection type.
     *
     * @param connectionType new connection type
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }


    /**
     * Returns the controller description.
     *
     * @return controller description
     */
    @Override
    public String getDescription() {
        return "Controller: " + getTitle()
                + ", connection: " + connectionType;
    }
}
