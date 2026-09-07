package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Repository responsible for persisting and loading customers and sellers.
 *
 * <p>The repository uses plain text files to preserve person information
 * between application executions.</p>
 */
public class PersonRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String SELLERS_FILE = "sellers.txt";

    private final Path customersPath;
    private final Path sellersPath;

    /**
     * Creates a person repository using the default data directory.
     */
    public PersonRepository() {
        Path dataPath = Paths.get(DATA_DIRECTORY);
        this.customersPath = dataPath.resolve(CUSTOMERS_FILE);
        this.sellersPath = dataPath.resolve(SELLERS_FILE);
    }

    /**
     * Saves the provided customers.
     *
     * @param customers customers to save
     */
    public void saveCustomers(List<Customer> customers) {
        // Implemented in commit 9.
    }

    /**
     * Saves the provided sellers.
     *
     * @param sellers sellers to save
     */
    public void saveSellers(List<Seller> sellers) {
        // Implemented in commit 9.
    }

    /**
     * Loads all persisted customers.
     *
     * @return the persisted customers
     */
    public List<Customer> loadCustomers() {
        // Implemented in commit 8.
        return Collections.emptyList();
    }

    /**
     * Loads all persisted sellers.
     *
     * @return the persisted sellers
     */
    public List<Seller> loadSellers() {
        // Implemented in commit 8.
        return Collections.emptyList();
    }

    /**
     * Returns the path used to persist customers.
     *
     * @return the customers file path
     */
    public Path getCustomersPath() {
        return customersPath;
    }

    /**
     * Returns the path used to persist sellers.
     *
     * @return the sellers file path
     */
    public Path getSellersPath() {
        return sellersPath;
    }
}