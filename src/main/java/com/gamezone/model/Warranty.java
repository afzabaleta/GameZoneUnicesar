package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a general warranty associated with a product and a sale.
 */
public abstract class Warranty {

    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a warranty and calculates its end date automatically.
     *
     * @param id unique warranty identifier
     * @param product associated product
     * @param sale associated sale
     * @param startDate warranty start date
     */
    public Warranty(
            String id,
            Product product,
            Sale sale,
            LocalDate startDate) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Warranty identifier cannot be blank."
            );
        }

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null."
            );
        }

        if (sale == null) {
            throw new IllegalArgumentException(
                    "Sale cannot be null."
            );
        }

        if (startDate == null) {
            throw new IllegalArgumentException(
                    "Start date cannot be null."
            );
        }

        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(
                getDurationInMonths()
        );
    }

    /**
     * Returns the warranty identifier.
     *
     * @return warranty identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the product associated with the warranty.
     *
     * @return associated product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the sale associated with the warranty.
     *
     * @return associated sale
     */
    public Sale getSale() {
        return sale;
    }

    /**
     * Returns the warranty start date.
     *
     * @return warranty start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the warranty end date.
     *
     * @return warranty end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the duration of the warranty in months.
     *
     * @return warranty duration in months
     */
    public abstract int getDurationInMonths();

    /**
     * Returns the name of the warranty type.
     *
     * @return warranty type
     */
    public abstract String getWarrantyType();

    /**
     * Returns the additional cost of the warranty.
     *
     * @return additional warranty cost
     */
    public abstract double getAdditionalCost();

    /**
     * Checks whether the warranty is active on a given date.
     *
     * @param date date to check
     * @return true if the warranty is active, false otherwise
     */
    public boolean isActive(LocalDate date) {

        if (date == null) {
            return false;
        }

        return !date.isBefore(startDate)
                && !date.isAfter(endDate);
    }

    /**
     * Generates a formatted warranty certificate.
     *
     * @return warranty certificate text
     */
    public String generateWarrantyCertificate() {

        return String.format(
                "Certificado de %s | ID: %s | Producto: %s | Fecha inicio: %s | Fecha vencimiento: %s | Costo adicional: $%.2f",
                getWarrantyType(),
                id,
                product.getDescription(),
                startDate,
                endDate,
                getAdditionalCost()
        );
    }
}