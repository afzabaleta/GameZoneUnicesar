
package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;

/** Validates product data and inventory rules. */
public final class ProductValidator {

    private ProductValidator() {
        // Utility class; instances are not required.
    }

    /** Validates identifier, title, price, and initial stock. */
    public static void validateProductData(
            String id, String title, double price, int stock) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("id", "el identificador no puede estar vacío");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidDataException("title", "el título no puede estar vacío");
        }
        if (!Double.isFinite(price) || price <= 0) {
            throw new InvalidDataException("price", "el precio debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "la cantidad no puede ser negativa");
        }
    }

    /** Validates that requested quantity is available in stock. */
    public static void validateStockAvailability(
            Product product, int requestedQuantity) {
        validateProductExists(product, "no indicado");
        if (requestedQuantity <= 0) {
            throw new InvalidDataException(
                    "requestedQuantity",
                    "la cantidad solicitada debe ser mayor que cero");
        }
        if (product.getAvailableQuantity() < requestedQuantity) {
            throw new BusinessRuleException(
                    "Stock insuficiente para el producto "
                            + product.getIdentifier()
                            + ". Disponible: " + product.getAvailableQuantity()
                            + "; solicitado: " + requestedQuantity + ".");
        }
    }

    /** Validates that a requested product has been found. */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }
}
