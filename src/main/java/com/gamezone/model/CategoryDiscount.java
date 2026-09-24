package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a category-based discount promotion.
 */
public class CategoryDiscount extends Promotion {

    private String category;
    private double discountPercentage;


    /**
     * Creates a category discount promotion.
     *
     * @param identifier promotion identifier
     * @param name promotion name
     * @param startDate promotion start date
     * @param endDate promotion end date
     * @param category product category affected
     * @param discountPercentage discount percentage
     */
    public CategoryDiscount(String identifier,
                            String name,
                            LocalDate startDate,
                            LocalDate endDate,
                            String category,
                            double discountPercentage) {

        super(identifier, name, startDate, endDate);

        this.category = category;
        setDiscountPercentage(discountPercentage);
    }


    /**
     * Returns the product category.
     *
     * @return category name
     */
    public String getCategory() {
        return category;
    }


    /**
     * Updates the product category.
     *
     * @param category new category
     */
    public void setCategory(String category) {

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Category cannot be blank.");
        }

        this.category = category;
    }


    /**
     * Returns the discount percentage.
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
     * Calculates discount amount for products of the selected category.
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

        double categoryTotal = 0;

        for (Product product : sale.getProducts()) {

            /*
             * The category is identified by the concrete product class.
             * Example:
             * VideoGame -> VIDEOGAME
             * Console -> CONSOLE
             */
            if (product.getClass()
                    .getSimpleName()
                    .equalsIgnoreCase(category)) {

                categoryTotal += product.getPrice();
            }
        }

        return categoryTotal * discountPercentage / 100;
    }
}