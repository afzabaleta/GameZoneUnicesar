package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Console;
import java.util.List;
import java.io.IOException;

/**
 * Service class that contains business logic related to products.
 *
 * <p>This class connects the application logic with the product repository.</p>
 */
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a product service with a repository.
     *
     * @param productRepository repository used to manage products
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Registers a video game product.
     *
     * @param videoGame video game to register
     */
    public void registerVideoGame(VideoGame videoGame) {
        productRepository.save(videoGame);
    }

    /**
     * Registers a console product.
     *
     * @param console console to register
     */
    public void registerConsole(Console console) {
        productRepository.save(console);
    }

    /**
     * Returns all registered products.
     *
     * @return list of products
     */
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    /**
     * Updates the stock quantity of a product.
     *
     * @param identifier product identifier
     * @param quantity new available quantity
     */
    /**
     * Updates the stock quantity of a product.
     *
     * @param productIdentifier product identifier
     * @param quantity new available quantity
     */
    public void updateStock(String productIdentifier, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Product product = productRepository.findByIdentifier(productIdentifier);

        if (product != null) {
            product.setAvailableQuantity(quantity);

            try {
                productRepository.saveProducts(productRepository.findAll());
            } catch (IOException e) {
                throw new IllegalStateException("Could not update product stock.", e);
            }
        }
    }
    /**
     * Registers a new product.
     *
     * @param product product to register
     */



}
