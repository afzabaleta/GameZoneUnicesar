package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a promotion that applies a discount to a specific product category.
 */
public class CategoryDiscount extends Promotion {

    private double percentage;
    private String targetCategory;

    /**
     * Creates a category discount promotion.
     *
     * @param identifier promotion identifier
     * @param name promotion name
     * @param startDate promotion start date
     * @param endDate promotion end date
     * @param percentage discount percentage
     * @param targetCategory target category, VIDEOGAME or CONSOLE
     */
    public CategoryDiscount(
            String identifier,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            double percentage,
            String targetCategory) {

        super(identifier, name, startDate, endDate);
        setPercentage(percentage);
        setTargetCategory(targetCategory);
    }

    /**
     * Returns the discount percentage.
     *
     * @return discount percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Updates the discount percentage.
     *
     * @param percentage new discount percentage
     */
    public void setPercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Discount percentage must be between 0 and 100.");
        }

        this.percentage = percentage;
    }

    /**
     * Returns the target category.
     *
     * @return target category
     */
    public String getTargetCategory() {
        return targetCategory;
    }

    /**
     * Updates the target category.
     *
     * @param targetCategory new target category
     */
    public void setTargetCategory(String targetCategory) {
        if (targetCategory == null
                || (!targetCategory.equalsIgnoreCase("VIDEOGAME")
                && !targetCategory.equalsIgnoreCase("CONSOLE"))) {

            throw new IllegalArgumentException(
                    "Target category must be VIDEOGAME or CONSOLE.");
        }

        this.targetCategory = targetCategory.toUpperCase();
    }

    /**
     * Calculates the discount over products in the target category.
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

        double categoryTotal = 0.0;

        for (Product product : sale.getProducts()) {

            boolean matches = false;

            if ("VIDEOGAME".equals(targetCategory)
                    && product instanceof VideoGame) {
                matches = true;
            }

            if ("CONSOLE".equals(targetCategory)
                    && product instanceof Console) {
                matches = true;
            }

            if (matches) {
                categoryTotal += product.getPrice();
            }
        }

        return categoryTotal * percentage / 100.0;
    }
}