package com.gamezone.model;

/**
 * Represents a category-based discount promotion.
 */
public class CategoryDiscount extends Promotion {

    private String category;
    private double discountPercentage;

    /**
     * Creates a category discount promotion.
     *
     * @param id promotion identifier
     * @param description promotion description
     * @param active promotion status
     * @param category product category
     * @param discountPercentage discount percentage value
     */
    public CategoryDiscount(String id,
                            String description,
                            boolean active,
                            String category,
                            double discountPercentage) {

        super(id, description, active);
        this.category = category;
        this.discountPercentage = discountPercentage;
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
     * Updates the discount percentage.
     *
     * @param discountPercentage new discount percentage
     */
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    /**
     * Applies the category discount to a price.
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
     * @return category discount description
     */
    @Override
    public String getPromotionDescription() {
        return getDescription()
                + " - Category: "
                + category
                + " - Discount: "
                + discountPercentage
                + "%";
    }
}
