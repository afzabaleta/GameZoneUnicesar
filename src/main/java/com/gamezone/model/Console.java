package com.gamezone.model;

/**
 * Represents a console product available in the GameZone store.
 * It includes specific information such as brand, model, and generation.
 */

public class Console extends Product {

    private String brand;
    private String model;
    private String generation;


    /**
     * Creates a console with its common and specific information.
     *
     * @param id unique identifier of the console
     * @param title title of the console
     * @param price price of the console
     * @param stock available quantity in inventory
     * @param brand brand of the console
     * @param model model of the console
     * @param generation generation of the console
     */

    public Console(String id, String title, double price, int stock,
                   String brand, String model, String generation) {

        super(id, title, price, stock);

        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    /**
     * Returns a complete description of the console.
     *
     * @return the console description
     */

    @Override
    public String getDescription() {
        return "Console: " + getTitle()
                + " | Brand: " + brand
                + " | Model: " + model
                + " | Generation: " + generation
                + " | Price: " + getPrice()
                + " | Stock: " + getStock();
    }
}