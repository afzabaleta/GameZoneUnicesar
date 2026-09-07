package com.gamezone.model;
import java.io.Serializable;

/**
 * Abstract base class representing a product in the GameZone system.
 *
 * <p>This class defines the common attributes and operations
 * shared by all product types.</p>
 */
public abstract class Product implements Serializable {

    private String identifier;
    private String title;
    private double price;
    private int availableQuantity;
    private static final long serialVersionUID = 1L;

    /**
     * Creates a product with its basic information.
     *
     * @param identifier unique identifier of the product
     * @param title product title
     * @param price product price
     * @param availableQuantity available quantity in stock
     */
    public Product(String identifier, String title, double price, int availableQuantity) {
        // Keep Sherlys' existing constructor and make sure
        // these four assignments are present:
        this.identifier = identifier;
        this.title = title;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Returns the product identifier.
     *
     * @return the product identifier
     */
    public String getIdentifier() {
        // Keep her existing return statement:
        return identifier;
    }

    /**
     * Returns the product title.
     *
     * @return the product title
     */
    public String getTitle() {
        // Keep her existing return statement:
        return title;
    }

    /**
     * Returns the product price.
     *
     * @return the product price
     */
    public double getPrice() {
        // Keep her existing return statement:
        return price;
    }

    /**
     * Returns the quantity currently available in stock.
     *
     * @return available product quantity
     */
    public int getAvailableQuantity() {
        // Keep her existing return statement:
        return availableQuantity;
    }

    /**
     * Updates the quantity available in stock.
     *
     * @param availableQuantity new available quantity
     */
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    /**
     * Returns a textual description of the product.
     *
     * @return product description
     */
    public abstract String getDescription();
}