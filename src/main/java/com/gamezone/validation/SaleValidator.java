
package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.util.List;

/** Validates sale data and required references. */
public final class SaleValidator {

    private SaleValidator() {
        // Utility class; instances are not required.
    }

    /** Validates the customer, seller, and products for a sale. */
    public static void validateSaleData(
            Customer customer, Seller seller, List<Product> products) {
        if (customer == null) {
            throw new InvalidDataException("customer", "debe seleccionarse un cliente");
        }
        if (seller == null) {
            throw new InvalidDataException("seller", "debe seleccionarse un vendedor");
        }
        if (products == null) {
            throw new InvalidDataException("products", "la lista no puede ser nula");
        }
        if (products.isEmpty()) {
            throw new BusinessRuleException("No se puede registrar una venta sin productos.");
        }
        for (Product product : products) {
            if (product == null) {
                throw new InvalidDataException(
                        "products", "la lista contiene un producto nulo");
            }

            if (product.getIdentifier() == null
                    || product.getIdentifier().isBlank()) {
                throw new InvalidDataException(
                        "product identifier",
                        "Product identifier cannot be empty"
                );
            }

        }
    }

    /** Validates that a requested sale has been found. */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}
