
package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.validation.ProductValidator;

import java.util.List;

/**
 * Service class that contains business logic related to products.
 *
 * <p>Product data and stock rules are delegated to ProductValidator.
 * Persistence errors are handled by ProductRepository.</p>
 */
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a product service with a repository.
     *
     * @param productRepository repository used to manage products
     */
    public ProductService(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new InvalidDataException(
                    "productRepository",
                    "es obligatorio."
            );
        }

        this.productRepository = productRepository;
    }

    /**
     * Registers a video game product after validating its data.
     *
     * @param videoGame video game to register
     */
    public void registerVideoGame(VideoGame videoGame) {
        if (videoGame == null) {
            throw new InvalidDataException(
                    "product",
                    "es obligatorio."
            );
        }

        ProductValidator.validateProductData(
                videoGame.getIdentifier(),
                videoGame.getTitle(),
                videoGame.getPrice(),
                videoGame.getAvailableQuantity()
        );

        productRepository.save(videoGame);
    }

    /**
     * Registers a console product after validating its data.
     *
     * @param console console to register
     */
    public void registerConsole(Console console) {
        if (console == null) {
            throw new InvalidDataException(
                    "product",
                    "es obligatorio."
            );
        }

        ProductValidator.validateProductData(
                console.getIdentifier(),
                console.getTitle(),
                console.getPrice(),
                console.getAvailableQuantity()
        );

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
     * @param productIdentifier product identifier
     * @param quantity new available quantity
     */
    public void updateStock(String productIdentifier, int quantity) {
        if (productIdentifier == null || productIdentifier.isBlank()) {
            throw new InvalidDataException(
                    "productIdentifier",
                    "es obligatorio."
            );
        }

        Product product =
                productRepository.findByIdentifier(productIdentifier);

        ProductValidator.validateProductExists(
                product,
                productIdentifier
        );

        ProductValidator.validateProductData(
                product.getIdentifier(),
                product.getTitle(),
                product.getPrice(),
                quantity
        );

        product.setAvailableQuantity(quantity);
        productRepository.saveProducts(productRepository.findAll());
    }

    /**
     * Restores stock after a successful product return.
     *
     * @param productId identifier of the product
     * @param quantity quantity to restore
     */
    public void restoreStock(String productId, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new InvalidDataException(
                    "productId",
                    "es obligatorio."
            );
        }

        if (quantity <= 0) {
            throw new InvalidDataException(
                    "quantity",
                    "debe ser positiva."
            );
        }

        Product product = productRepository.findByIdentifier(productId);

        ProductValidator.validateProductExists(product, productId);

        int restoredStock =
                product.getAvailableQuantity() + quantity;

        ProductValidator.validateProductData(
                product.getIdentifier(),
                product.getTitle(),
                product.getPrice(),
                restoredStock
        );

        product.setAvailableQuantity(restoredStock);
        productRepository.saveProducts(productRepository.findAll());
    }
}