
package com.gamezone.validation;

import com.gamezone.model.Accessory;

/**
 * Validates accessory data and inventory rules.
 */
public final class AccessoryValidator {

    private AccessoryValidator() {
        // Utility class; instances are not required.
    }

    /**
     * Validates the common data required to register an accessory.
     *
     * @param accessory accessory to validate
     */
    public static void validateAccessoryData(Accessory accessory) {

        if (accessory == null) {
            throw new IllegalArgumentException(
                    "Accessory cannot be null"
            );
        }

        if (accessory.getIdentifier() == null
                || accessory.getIdentifier().isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory identifier cannot be blank"
            );
        }

        if (accessory.getTitle() == null
                || accessory.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory title cannot be blank"
            );
        }

        if (accessory.getPrice() < 0) {
            throw new IllegalArgumentException(
                    "Accessory price cannot be negative"
            );
        }

        if (accessory.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Accessory stock cannot be negative"
            );
        }
    }

    /**
     * Validates that an accessory identifier is unique.
     *
     * @param accessory accessory being registered
     * @param existingAccessory accessory already found, if any
     */
    public static void validateUniqueAccessory(
            Accessory accessory,
            Accessory existingAccessory) {

        if (existingAccessory != null) {
            throw new IllegalArgumentException(
                    "Accessory already exists: "
                            + accessory.getIdentifier()
            );
        }
    }

    /**
     * Validates an accessory type search.
     *
     * @param type accessory type to validate
     */
    public static void validateAccessoryType(String type) {

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory type cannot be blank"
            );
        }
    }

    /**
     * Validates a console identifier.
     *
     * @param consoleId console identifier to validate
     */
    public static void validateConsoleIdentifier(String consoleId) {

        if (consoleId == null || consoleId.isBlank()) {
            throw new IllegalArgumentException(
                    "Console identifier cannot be blank"
            );
        }
    }

    /**
     * Validates an accessory identifier used for stock operations.
     *
     * @param identifier accessory identifier
     */
    public static void validateStockIdentifier(String identifier) {

        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Accessory identifier cannot be blank"
            );
        }
    }

    /**
     * Validates the new stock quantity.
     *
     * @param newStock new available quantity
     */
    public static void validateNewStock(int newStock) {

        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Accessory stock cannot be negative"
            );
        }
    }

    /**
     * Validates the quantity to restore.
     *
     * @param quantity quantity to restore
     */
    public static void validateRestoredQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Restored quantity must be greater than zero"
            );
        }
    }

    /**
     * Validates that the requested accessory exists.
     *
     * @param accessory accessory found in the repository
     * @param identifier requested accessory identifier
     */
    public static void validateAccessoryExists(
            Accessory accessory,
            String identifier) {

        if (accessory == null) {
            throw new IllegalArgumentException(
                    "Accessory not found: " + identifier
            );
        }
    }
}
