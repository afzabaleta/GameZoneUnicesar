package com.gamezone.model;
import java.io.Serializable;

/**
 * Represents a generic product available in the GameZone store.
 * It contains the common attributes and behaviors shared by all product types.
 */

public abstract class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private double price;
    private int stock;

    /**
     * Creates a product with its common information.
     *
     * @param id unique identifier of the product
     * @param title title of the product
     * @param price price of the product
     * @param stock available quantity in inventory
     */

    public Product(String id, String title, double price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Returns the unique identifier of the product.
     *
     * @return the product identifier
     */

    public String getId() {
        return id;
    }

    /**
     * Returns the title of the product.
     *
     * @return the product title
     */

    public String getTitle() {
        return title;
    }

    /**
     * Returns the price of the product.
     *
     * @return the product price
     */

    public double getPrice() {
        return price;
    }

    /**
     * Returns the available stock of the product.
     *
     * @return the available quantity in inventory
     */

    public int getStock() {
        return stock;
    }

    /**
     * Updates the title of the product.
     *
     * @param title the new title of the product
     */

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Updates the price of the product.
     *
     * @param price the new price of the product
     */

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Updates the available stock of the product.
     *
     * @param stock the new quantity available in inventory
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Returns a complete description of the product.
     *
     * @return the product description
     */
    public abstract String getDescription();
}
