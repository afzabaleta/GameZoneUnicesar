package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.persistence.ProductRepository;

import java.io.IOException;
import java.util.List;

public class ProductService {

    private final ProductRepository repository;
    private final List<Product> products;

    public ProductService(ProductRepository repository)
            throws IOException, ClassNotFoundException {

        this.repository = repository;
        this.products = repository.loadProducts();
    }

    public void registerProduct(Product product) throws IOException {
        products.add(product);
        repository.saveProducts(products);
    }

    public List<Product> getProducts() {
        return products;
    }

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
