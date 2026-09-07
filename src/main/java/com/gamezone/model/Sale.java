package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction in the GameZone system.
 * A sale is associated with one customer, one seller and one or more products.
 */

public class Sale {
    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<Product> products;

    /**
     * Creates a new sale.
     *
     * @param date     the date the sale was made
     * @param customer the customer who made the purchase
     * @param seller   the seller who attended the sale
     * @param products the list of products included in the sale (must contain at least one product)
     */
    public Sale(LocalDate date, Customer customer, Seller seller, List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }
        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.products = products;
    }
    
    /**
     * Returns the sale date.
     *
     * @return the date of the sale
     */
    public LocalDate getDate(){
        return date;
    }

    /**
     * Returns the customer associated with the sale.
     *
     * @return the sale customer
     */
    public Customer getCustomer(){
        return customer;
    }

    /**
     * Returns the seller associated with the sale.
     *
     * @return the sale seller
     */
    public Seller getSeller(){
        return seller;
    }

    /**
     * Returns a copy of the products included in this sale.
     *
     * @return a copy of the products
     */
    public List<Product> getProducts(){
        return new ArrayList<>(products);
    }
}
