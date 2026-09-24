package com.gamezone.model;

/**
 * Represents a percentage-based discount promotion.
 */
public class PercentageDiscount extends Promotion {

    private double discountPercentage;

    /**
     * Creates a percentage discount promotion.
     *
     * @param id promotion identifier
     * @param description promotion description
     * @param active promotion status
     * @param discountPercentage discount percentage value
     */
    public PercentageDiscount(String id,
                              String description,
                              boolean active,
                              double discountPercentage) {

        super(id, description, active);
        this.discountPercentage = discountPercentage;
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
     * Updates the discount percentage.
     *
     * @param discountPercentage new discount percentage
     */
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    /**
     * Applies the percentage discount to a price.
     *
     * @param price original price
     * @return discounted price
     */
    @Override
    public double applyDiscount(double price) {
        return price - (price * discountPercentage / 100);
    }

    /**
     * Returns the promotion description.
     *
     * @return percentage discount description
     */
    @Override
    public String getPromotionDescription() {
        return getDescription() + " - Discount: "
                + discountPercentage + "%";
    }
}
