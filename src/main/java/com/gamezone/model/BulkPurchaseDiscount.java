package com.gamezone.model;

/**
 * Represents a bulk purchase discount promotion.
 */
public class BulkPurchaseDiscount extends Promotion {

    private int minimumQuantity;
    private double discountPercentage;

    /**
     * Creates a bulk purchase discount.
     *
     * @param id promotion identifier
     * @param description promotion description
     * @param active promotion status
     * @param minimumQuantity minimum quantity required
     * @param discountPercentage discount percentage
     */
    public BulkPurchaseDiscount(String id,
                                String description,
                                boolean active,
                                int minimumQuantity,
                                double discountPercentage) {

        super(id, description, active);
        this.minimumQuantity = minimumQuantity;
        this.discountPercentage = discountPercentage;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    /**
     * Applies the bulk discount.
     *
     * @param price original price
     * @return discounted price
     */
    @Override
    public double applyDiscount(double price) {
        return price - (price * discountPercentage / 100);
    }

    /**
     * Returns promotion description.
     *
     * @return bulk discount description
     */
    @Override
    public String getPromotionDescription() {
        return getDescription()
                + " - Minimum quantity: "
                + minimumQuantity
                + " - Discount: "
                + discountPercentage
                + "%";
    }
}
