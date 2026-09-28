package com.gamezone.model;

import java.util.List;

/**
 * Represents a memory accessory.
 */
public class Memory extends Accessory {

    private double capacityGB;

    private String memoryType;


    /**
     * Creates a memory accessory.
     *
     * @param identifier unique identifier
     * @param title memory title
     * @param price memory price
     * @param availableQuantity available quantity
     * @param capacityGB memory capacity in GB
     * @param memoryType memory type
     * @param compatibleConsoles compatible consoles
     */
    public Memory(String identifier,
                  String title,
                  double price,
                  int availableQuantity,
                  double capacityGB,
                  String memoryType,
                  List<Console> compatibleConsoles) {

        super(identifier, title, price, availableQuantity, compatibleConsoles);
        this.capacityGB = capacityGB;
        this.memoryType = memoryType;
    }


    /**
     * Returns the memory capacity.
     *
     * @return capacity in GB
     */
    public double getCapacityGB() {
        return capacityGB;
    }


    /**
     * Updates the memory capacity.
     *
     * @param capacityGB new capacity
     */
    public void setCapacityGB(double capacityGB) {
        this.capacityGB = capacityGB;
    }


    /**
     * Returns the memory type.
     *
     * @return memory type
     */
    public String getMemoryType() {
        return memoryType;
    }


    /**
     * Updates the memory type.
     *
     * @param memoryType new memory type
     */
    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }


    /**
     * Returns the memory description.
     *
     * @return memory description
     */
    @Override
    public String getDescription() {
        return "Memory: " + getTitle()
                + ", capacity: " + capacityGB + " GB"
                + ", type: " + memoryType;
    }
}
