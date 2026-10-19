package com.gamezone.model;

/**
 * Represents a console product in the GameZone catalog.
 * Extends the common attributes and behavior from Product.
 */

public class Console extends Product {
    /**
     * Keeps compatibility with the products already stored in
     * data/products.dat.
     */
    private static final long serialVersionUID = 7091003648521894664L;

    /**
     * Brand name of the console.
     */

    private String brand;
    /**
     * Model name of the console.
     */
    private String model;
    /**
     * Generation of the console.
     */
    private String generation;
    /**
     * Creates a new console product with the specified information.
     *
     * @param identifier unique product identifier
     * @param title console name
     * @param price product price
     * @param availableQuantity available stock quantity
     * @param brand console manufacturer brand
     * @param model console model
     * @param generation console generation
     */

    public Console(
            String identifier,
            String title,
            double price,
            int availableQuantity,
            String brand,
            String model,
            String generation) {

        super(identifier, title, price, availableQuantity);

        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    /**
     * Provides a detailed description of the console.
     *
     * @return formatted description containing console information
     */

    @Override
    public String getDescription() {
        return "Console: " + getTitle()
                + " Brand: " + brand
                + " Model: " + model
                + " Generation: " + generation;
    }
    /**
     * Returns the category of consoles.
     *
     * @return CONSOLE
     */
    @Override
    public String getCategory() {
        return "CONSOLE";
    }

    /**
     * Consoles always receive a warranty when they are sold.
     *
     * @return true
     */
    @Override
    public boolean isWarrantyEligible() {
        return true;
    }
}
