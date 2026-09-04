package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.persistence.ProductRepository;

import java.io.IOException;
import java.util.List;

/**
 * Provides business operations for managing products.
 * It handles product registration, listing, and stock updates.
 */

public class ProductService {

    private final ProductRepository repository;
    private final List<Product> products;

    /**
     * Creates a product service using the specified repository.
     * It loads the previously stored products when the service starts.
     *
     * @param repository repository used to manage product persistence
     * @throws IOException if an error occurs while reading stored products
     * @throws ClassNotFoundException if a stored product class cannot be found
     */

    public ProductService(ProductRepository repository)
            throws IOException, ClassNotFoundException {

        this.repository = repository;
        this.products = repository.loadProducts();
    }

    /**
     * Registers a new product and saves the updated product list.
     *
     * @param product product to register
     * @throws IOException if an error occurs while saving the product information
     */

    public void registerProduct(Product product) throws IOException {
        products.add(product);
        repository.saveProducts(products);
    }

    /**
     * Returns the current list of registered products.
     *
     * @return the list of registered products
     */

    public List<Product> getProducts() {
        return products;
    }

    /**
     * Updates the stock of a product identified by its ID.
     *
     * @param productId identifier of the product to update
     * @param newStock new quantity available in inventory
     * @return true if the product was found and updated, false otherwise
     * @throws IOException if an error occurs while saving the updated product information
     * @throws IllegalArgumentException if the new stock is negative
     */

    public boolean updateStock(String productId, int newStock) throws IOException {

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setStock(newStock);
                repository.saveProducts(products);
                return true;
            }
        }

        return false;
    }
}
