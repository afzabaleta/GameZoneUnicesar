package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction in the GameZone system.
 * A sale is associated with one customer, one seller and one or more products.
 * It also stores the promotion applied and discount information.
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
     * @param date sale date
     * @param customer customer who made the purchase
     * @param seller seller who attended the sale
     * @param products products included in the sale
     */
    public Sale(LocalDate date,
                Customer customer,
                Seller seller,
                List<Product> products) {

        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException(
                    "A sale must contain at least one product.");
        }

        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.products = products;
        this.appliedPromotionName = null;
        this.discountAmount = 0.0;
    }


    /**
     * Calculates the total price of the sale.
     *
     * @return total sale amount
     */
    public double calculateTotal() {

        double total = 0.0;

        for (Product product : products) {
            total += product.getPrice();
        }

        return total;
    }


    /**
     * Determines whether the sale can be returned.
     *
     * <p>A sale can only be returned within 30 calendar days
     * after the original sale date.</p>
     *
     * @return true if the sale is within the allowed return period
     */
    public boolean canBeReturned() {

        if (date == null) {
            return false;
        }

        LocalDate returnDeadline = date.plusDays(30);

        return !LocalDate.now().isAfter(returnDeadline);
    }


    /**
     * Generates the sale receipt.
     *
     * @return formatted receipt
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


    public LocalDate getDate() {
        return date;
    }


    public Customer getCustomer() {
        return customer;
    }


    public Seller getSeller() {
        return seller;
    }


    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }


    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }


    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }


    public double getDiscountAmount() {
        return discountAmount;
    }


    public void setDiscountAmount(double discountAmount) {

        if (discountAmount < 0) {
            throw new IllegalArgumentException(
                    "Discount amount cannot be negative.");
        }

        this.discountAmount = discountAmount;
    }
}