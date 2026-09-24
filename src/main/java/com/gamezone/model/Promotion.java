package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a promotion that can be applied to a sale.
 */
public abstract class Promotion {

    private String identifier;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a promotion.
     *
     * @param identifier unique promotion identifier
     * @param name promotion name
     * @param startDate promotion start date
     * @param endDate promotion end date
     */
    public Promotion(
            String identifier,
            String name,
            LocalDate startDate,
            LocalDate endDate) {

        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Promotion identifier cannot be blank.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Promotion name cannot be blank.");
        }

        validateDateRange(startDate, endDate);

        this.identifier = identifier;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the promotion identifier.
     *
     * @return promotion identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Updates the promotion identifier.
     *
     * @param identifier new promotion identifier
     */
    public void setIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Promotion identifier cannot be blank.");
        }

        this.identifier = identifier;
    }

    /**
     * Returns the promotion name.
     *
     * @return promotion name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the promotion name.
     *
     * @param name new promotion name
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Promotion name cannot be blank.");
        }

        this.name = name;
    }

    /**
     * Returns the promotion start date.
     *
     * @return promotion start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Updates the promotion start date.
     *
     * @param startDate new promotion start date
     */
    public void setStartDate(LocalDate startDate) {
        validateDateRange(startDate, endDate);
        this.startDate = startDate;
    }

    /**
     * Returns the promotion end date.
     *
     * @return promotion end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Updates the promotion end date.
     *
     * @param endDate new promotion end date
     */
    public void setEndDate(LocalDate endDate) {
        validateDateRange(startDate, endDate);
        this.endDate = endDate;
    }

    /**
     * Checks whether the promotion is active on the given date.
     *
     * @param date date to evaluate
     * @return true if the date is within the promotion validity range
     */
    public boolean isActive(LocalDate date) {
        return date != null
                && !date.isBefore(startDate)
                && !date.isAfter(endDate);
    }

    /**
     * Validates the promotion date range.
     *
     * @param startDate promotion start date
     * @param endDate promotion end date
     */
    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Promotion dates cannot be null.");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Promotion start date cannot be after end date.");
        }
    }

    /**
     * Calculates the discount amount for a sale.
     *
     * @param sale sale to evaluate
     * @return discount amount
     */
    public abstract double calculateDiscount(Sale sale);
}