package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a bulk purchase discount promotion.
 */
public class BulkPurchaseDiscount extends Promotion {

    private int minimumQuantity;
    private double discountPercentage;

    /**
     * Creates a bulk purchase discount.
     *
     * @param identifier promotion identifier
     * @param name promotion name
     * @param startDate promotion start date
     * @param endDate promotion end date
     * @param minimumQuantity minimum quantity required
     * @param discountPercentage discount percentage
     */
    public BulkPurchaseDiscount(String identifier,
                                String name,
                                LocalDate startDate,
                                LocalDate endDate,
                                int minimumQuantity,
                                double discountPercentage) {

        super(identifier, name, startDate, endDate);

        setMinimumQuantity(minimumQuantity);
        setDiscountPercentage(discountPercentage);
    }


    /**
     * Returns minimum quantity required.
     *
     * @return minimum quantity
     */
    public int getMinimumQuantity() {
        return minimumQuantity;
    }


    /**
     * Updates minimum quantity.
     *
     * @param minimumQuantity new minimum quantity
     */
    public void setMinimumQuantity(int minimumQuantity) {

        if (minimumQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Minimum quantity must be greater than zero.");
        }

        this.minimumQuantity = minimumQuantity;
    }


    /**
     * Returns discount percentage.
     *
     * @return discount percentage
     */
    public double getDiscountPercentage() {
        return discountPercentage;
    }


    /**
     * Updates discount percentage.
     *
     * @param discountPercentage new discount percentage
     */
    public void setDiscountPercentage(double discountPercentage) {

        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException(
                    "Discount percentage must be between 0 and 100.");
        }

        this.discountPercentage = discountPercentage;
    }


    /**
     * Calculates discount amount for a sale.
     *
     * @param sale sale to evaluate
     * @return discount amount
     */
    @Override
    public double calculateDiscount(Sale sale) {

        if (sale == null) {
            throw new IllegalArgumentException(
                    "Sale cannot be null.");
        }

        int quantity = sale.getProducts().size();

        if (quantity >= minimumQuantity) {
            return sale.calculateTotal() * discountPercentage / 100;
        }

        return 0;
    }
}