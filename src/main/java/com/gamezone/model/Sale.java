package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction in the GameZone system.
 *
 * <p>A sale is associated with one customer, one seller and one or more
 * products. It also stores the promotion applied and the discount amount.</p>
 */
public class Sale {

    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<Product> products;
    private String appliedPromotionName;
    private double discountAmount;

    /**
     * Creates a new sale.
     *
     * @param date date of the sale
     * @param customer customer who made the purchase
     * @param seller seller who attended the sale
     * @param products products included in the sale
     */
    public Sale(
            LocalDate date,
            Customer customer,
            Seller seller,
            List<Product> products) {

        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException(
                    "A sale must contain at least one product."
            );
        }

        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.products = products;
        this.appliedPromotionName = null;
        this.discountAmount = 0.0;
    }

    /**
     * Calculates the subtotal of the sale before applying discounts.
     *
     * @return subtotal amount
     */
    public double calculateTotal() {
        double total = 0.0;

        for (Product product : products) {
            total += product.getPrice();
        }

        return total;
    }

    /**
     * Generates a receipt containing the subtotal, promotion,
     * discount amount and final total.
     *
     * @return formatted sale receipt
     */
    public String generateReceipt() {

        double subtotal = calculateTotal();
        double finalTotal = subtotal - discountAmount;

        StringBuilder receipt = new StringBuilder();

        receipt.append("===== RECIBO DE VENTA =====\n");

        receipt.append("Fecha: ")
                .append(date)
                .append("\n");

        receipt.append("Cliente: ")
                .append(customer.getName())
                .append("\n");

        receipt.append("Vendedor: ")
                .append(seller.getName())
                .append("\n");

        receipt.append("Subtotal: $")
                .append(subtotal)
                .append("\n");

        if (appliedPromotionName != null
                && !appliedPromotionName.isBlank()) {

            receipt.append("Promoción aplicada: ")
                    .append(appliedPromotionName)
                    .append("\n");

            receipt.append("Descuento: $")
                    .append(discountAmount)
                    .append("\n");

        } else {

            receipt.append("Promoción aplicada: Ninguna\n");
            receipt.append("Descuento: $0.0\n");
        }

        receipt.append("Total final: $")
                .append(finalTotal)
                .append("\n");

        receipt.append("==========================");

        return receipt.toString();
    }

    /**
     * Returns the sale date.
     *
     * @return sale date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the customer associated with the sale.
     *
     * @return sale customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the seller associated with the sale.
     *
     * @return sale seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * Returns a copy of the products included in the sale.
     *
     * @return copy of sale products
     */
    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Returns the name of the promotion applied to the sale.
     *
     * @return applied promotion name
     */
    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    /**
     * Updates the name of the promotion applied to the sale.
     *
     * @param appliedPromotionName promotion name
     */
    public void setAppliedPromotionName(
            String appliedPromotionName) {

        this.appliedPromotionName = appliedPromotionName;
    }

    /**
     * Returns the discount amount applied to the sale.
     *
     * @return discount amount
     */
    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Updates the discount amount applied to the sale.
     *
     * @param discountAmount discount amount
     */
    public void setDiscountAmount(double discountAmount) {

        if (discountAmount < 0) {
            throw new IllegalArgumentException(
                    "Discount amount cannot be negative."
            );
        }

        this.discountAmount = discountAmount;
    }

    /**
     * Checks whether this sale can be returned.
     *
     * @return true if the sale contains products that can be returned
     */
    public boolean canBeReturned() {
        return products != null && !products.isEmpty();
    }
}