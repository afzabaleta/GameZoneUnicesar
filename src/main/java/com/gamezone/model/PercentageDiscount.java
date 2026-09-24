package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a percentage-based discount promotion.
 */
public class PercentageDiscount extends Promotion {

    private double discountPercentage;


    /**
     * Creates a percentage discount promotion.
     *
     * @param identifier promotion identifier
     * @param name promotion name
     * @param startDate promotion start date
     * @param endDate promotion end date
     * @param discountPercentage discount percentage value
     */
    public PercentageDiscount(String identifier,
                              String name,
                              LocalDate startDate,
                              LocalDate endDate,
                              double discountPercentage) {

        super(identifier, name, startDate, endDate);

        setDiscountPercentage(discountPercentage);
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
     * @param discountPercentage new percentage
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

        return sale.calculateTotal() * discountPercentage / 100;
    }
}