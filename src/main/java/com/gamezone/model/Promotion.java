package com.gamezone.model;

/**
 * Abstract base class representing a promotion in the GameZone system.
 * Defines common attributes and operations shared by all promotion types.
 */
public abstract class Promotion {

    private String id;
    private String description;
    private boolean active;

    /**
     * Creates a promotion with basic information.
     *
     * @param id promotion identifier
     * @param description promotion description
     * @param active promotion status
     */
    public Promotion(String id, String description, boolean active) {
        this.id = id;
        this.description = description;
        this.active = active;
    }

    /**
     * Returns the promotion identifier.
     *
     * @return promotion id
     */
    public String getId() {
        return id;
    }

    /**
     * Updates the promotion identifier.
     *
     * @param id new promotion identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the promotion description.
     *
     * @return promotion description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the promotion description.
     *
     * @param description new promotion description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns whether the promotion is active.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Updates the promotion status.
     *
     * @param active new promotion status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Applies the promotion discount.
     *
     * @param price original product price
     * @return discounted price
     */
    public abstract double applyDiscount(double price);

    /**
     * Returns the promotion description.
     *
     * @return promotion description
     */
    public abstract String getPromotionDescription();
}
