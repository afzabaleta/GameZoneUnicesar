package com.gamezone.persistence;

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
 * <p>This class stores and loads products using Java serialization.</p>
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
    }

    /**
     * Saves a product in the repository.
     *
     * @param product product to save
     */
    public void save(Product product) {
        products.add(product);
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
     * @throws IOException if an input/output error occurs
     * @throws ClassNotFoundException if a stored class cannot be found
     */
    @SuppressWarnings("unchecked")
    public List<Product> loadProducts()
            throws IOException, ClassNotFoundException {

        File file = new File(filePath);

        if (!file.exists()) {
            products.clear();
            return products;
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(file))) {

            List<Product> loadedProducts =
                    (List<Product>) inputStream.readObject();

            products.clear();
            products.addAll(loadedProducts);

            return products;
        }
    }

    /**
     * Saves the product list to the persistence file.
     *
     * @param products products to save
     * @throws IOException if an input/output error occurs
     */
    public void saveProducts(List<Product> products) throws IOException {

        File file = new File(filePath);

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(file))) {

            outputStream.writeObject(products);
        }

        this.products.clear();
        this.products.addAll(products);
    }
}
