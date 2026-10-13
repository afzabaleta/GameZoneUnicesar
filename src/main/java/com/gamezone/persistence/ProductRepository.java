package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for managing product persistence.
 *
 * <p>This class stores and loads products using Java serialization. I/O and
 * serialization failures are converted to PersistenceException.</p>
 */
public class ProductRepository {

    private final String filePath;
    private final List<Product> products;

    /**
     * Creates a product repository using the default persistence file.
     */
    public ProductRepository() {
        this.filePath = "data/products.dat";
        this.products = new ArrayList<>();
        loadProducts();
    }

    /**
     * Saves a product and persists the updated product list.
     *
     * @param product product to save
     */
    public void save(Product product) {
        List<Product> updatedProducts = new ArrayList<>(products);
        updatedProducts.add(product);
        saveProducts(updatedProducts);
    }

    /**
     * Returns all products currently stored in memory.
     *
     * @return list of products
     */
    public List<Product> findAll() {
        return products;
    }

/**
 * Finds a product by its identifier.
 *
 * @param identifier product identifier
 * @return matching product or null if not found
 */
public Product findByIdentifier(String identifier) {
    for (Product product : products) {
        if (product.getIdentifier().equals(identifier)) {
            return product;
        }
    }
    return null;
}

    /**
     * Loads products from the persistence file.
     *
     * @return loaded products
     */
    @SuppressWarnings("unchecked")
    public List<Product> loadProducts() {
        File file = new File(filePath);
        if (!file.exists()) {
            products.clear();
            return products;
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(file))) {
            Object storedObject = inputStream.readObject();
            if (!(storedObject instanceof List<?>)) {
                throw new ClassCastException("The serialized value is not a product list.");
            }

            List<Product> loadedProducts = (List<Product>) storedObject;
            products.clear();
            products.addAll(loadedProducts);
            return products;

        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new PersistenceException(
                    "Unable to read product file '" + filePath
                            + "'. The file may be inaccessible or corrupted.",
                    e
            );
        }
    }

    /**
     * Saves the product list to the persistence file.
     *
     * @param productsToSave products to save
     */
    public void saveProducts(List<Product> productsToSave) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new PersistenceException(
                    "Unable to create directory for product file '" + filePath + "'.",
                    new IOException("Could not create directory: " + parent)
            );
        }

        List<Product> snapshot = new ArrayList<>(productsToSave);
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(snapshot);
        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to write product file '" + filePath + "'.",
                    e
            );
        }

        products.clear();
        products.addAll(snapshot);
    }
}