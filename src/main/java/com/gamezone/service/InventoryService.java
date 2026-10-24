package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Accessory;
import com.gamezone.model.Product;
import com.gamezone.validation.ProductValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that hides where each kind of sellable item is stored.
 *
 * <p>Products are kept by {@link ProductService} and accessories by
 * {@link AccessoryService}. This class is the only place that decides which
 * one handles an item, so sales and returns work with any {@link Product}
 * without checking their concrete type (Pure Fabrication and Protected
 * Variations).</p>
 */
public class InventoryService {

    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Creates an inventory service.
     *
     * @param productService service that stores regular products
     * @param accessoryService service that stores accessories
     */
    public InventoryService(
            ProductService productService,
            AccessoryService accessoryService) {

        if (productService == null) {
            throw new InvalidDataException(
                    "productService", "Product service cannot be null");
        }

        if (accessoryService == null) {
            throw new InvalidDataException(
                    "accessoryService", "Accessory service cannot be null");
        }

        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Finds a product or an accessory by identifier.
     *
     * <p>Regular products are searched first, then accessories.</p>
     *
     * @param identifier item identifier
     * @return matching item, or null when it does not exist
     */
    public Product findItem(String identifier) {
        for (Product product : productService.listProducts()) {
            if (product.getIdentifier().equals(identifier)) {
                return product;
            }
        }

        for (Accessory accessory : accessoryService.listAccessories()) {
            if (accessory.getIdentifier().equals(identifier)) {
                return accessory;
            }
        }

        return null;
    }

    /**
     * Validates that every item of a sale exists and has enough stock.
     *
     * <p>Regular products are validated before accessories, and each
     * identifier is checked with the total quantity requested.</p>
     *
     * @param items items included in the sale, one entry per unit
     */
    public void validateStock(List<Product> items) {
        for (Map.Entry<String, Integer> entry
                : countQuantities(items, false).entrySet()) {

            Product product = findProduct(entry.getKey());
            validateAvailability(product, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry
                : countQuantities(items, true).entrySet()) {

            Accessory accessory = findAccessory(entry.getKey());
            validateAvailability(
                    accessory, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Decreases the stock of every item of a sale.
     *
     * @param items items included in the sale, one entry per unit
     */
    public void decreaseStock(List<Product> items) {
        for (Map.Entry<String, Integer> entry
                : countQuantities(items, false).entrySet()) {

            Product product = findProduct(entry.getKey());
            ProductValidator.validateProductExists(product, entry.getKey());

            productService.updateStock(
                    entry.getKey(),
                    product.getAvailableQuantity() - entry.getValue()
            );
        }

        for (Map.Entry<String, Integer> entry
                : countQuantities(items, true).entrySet()) {

            Accessory accessory = findAccessory(entry.getKey());
            ProductValidator.validateProductExists(
                    accessory, entry.getKey());

            accessoryService.updateStock(
                    entry.getKey(),
                    accessory.getAvailableQuantity() - entry.getValue()
            );
        }
    }

    /**
     * Restores the stock of a returned item in the service that stores it.
     *
     * @param item returned product or accessory
     * @param quantity quantity to restore
     */
    public void restoreStock(Product item, int quantity) {
        if (item instanceof Accessory) {
            accessoryService.restoreStock(item.getIdentifier(), quantity);
        } else {
            productService.restoreStock(item.getIdentifier(), quantity);
        }
    }

    private void validateAvailability(
            Product item,
            String identifier,
            int requestedQuantity) {

        ProductValidator.validateProductExists(item, identifier);

        ProductValidator.validateProductData(
                item.getIdentifier(),
                item.getTitle(),
                item.getPrice(),
                item.getAvailableQuantity()
        );

        ProductValidator.validateStockAvailability(
                item, requestedQuantity);
    }

    private Map<String, Integer> countQuantities(
            List<Product> items,
            boolean accessories) {

        Map<String, Integer> counts = new HashMap<>();

        for (Product item : items) {
            if ((item instanceof Accessory) == accessories) {
                counts.merge(item.getIdentifier(), 1, Integer::sum);
            }
        }

        return counts;
    }

    private Product findProduct(String identifier) {
        for (Product product : productService.listProducts()) {
            if (product.getIdentifier().equals(identifier)) {
                return product;
            }
        }

        return null;
    }

    private Accessory findAccessory(String identifier) {
        for (Accessory accessory : accessoryService.listAccessories()) {
            if (accessory.getIdentifier().equals(identifier)) {
                return accessory;
            }
        }

        return null;
    }
}