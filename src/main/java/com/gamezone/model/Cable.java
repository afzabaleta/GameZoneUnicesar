package com.gamezone.model;

/**
 * Represents a cable accessory.
 */
public class Cable extends Accessory {

    private double length;

    private String connectorType;


    /**
     * Creates a cable accessory.
     *
     * @param identifier unique identifier
     * @param title cable title
     * @param price cable price
     * @param availableQuantity available quantity
     * @param length cable length in meters
     * @param connectorType connector type
     */
    public Cable(String identifier,
                 String title,
                 double price,
                 int availableQuantity,
                 double length,
                 String connectorType) {

        super(identifier, title, price, availableQuantity, null);
        this.length = length;
        this.connectorType = connectorType;
    }


    /**
     * Returns the cable length.
     *
     * @return cable length
     */
    public double getLength() {
        return length;
    }


    /**
     * Updates the cable length.
     *
     * @param length new cable length
     */
    public void setLength(double length) {
        this.length = length;
    }


    /**
     * Returns the connector type.
     *
     * @return connector type
     */
    public String getConnectorType() {
        return connectorType;
    }


    /**
     * Updates the connector type.
     *
     * @param connectorType new connector type
     */
    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }


    /**
     * Returns the cable description.
     *
     * @return cable description
     */
    @Override
    public String getDescription() {
        return "Cable: " + getTitle()
                + ", length: " + length + " m"
                + ", connector: " + connectorType;
    }
}
