package com.gamezone.persistence;

import com.gamezone.model.Product;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Manages the persistence of products using files.
 * It provides operations to save and load product information.
 */

public class ProductRepository {

    private final String filePath;

    /**
     * Creates a product repository using the specified file path.
     *
     * @param filePath path of the file used to store product information
     */

    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the current list of products to the configured file.
     *
     * @param products list of products to save
     * @throws IOException if an error occurs while writing the file
     */

    public void saveProducts(List<Product> products) throws IOException {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(filePath))) {

            outputStream.writeObject(products);
        }
    }

    /**
     * Loads the list of products from the configured file.
     *
     * @return the list of stored products
     * @throws IOException if an error occurs while reading the file
     * @throws ClassNotFoundException if a stored product class cannot be found
     */

    @SuppressWarnings("unchecked")
    public List<Product> loadProducts() throws IOException, ClassNotFoundException {

        File file = new File(filePath);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(filePath))) {

            return (List<Product>) inputStream.readObject();
        }
    }
}