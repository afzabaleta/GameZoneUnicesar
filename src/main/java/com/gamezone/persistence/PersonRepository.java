package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
     * Loads all persisted customers from the customers file.
     *
     * @return the persisted customers, or an empty list when the file does not exist
     */
    public List<Customer> loadCustomers() {
        if (!Files.exists(customersPath)) {
            return new ArrayList<>();
        }

        List<Customer> customers = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(customersPath);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length == 4) {
                    Customer customer = new Customer(
                            data[0],
                            data[1],
                            data[2],
                            data[3]
                    );

                    customers.add(customer);
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load customers from file.",
                    e
            );
        }

        return customers;
    }

    /**
     * Loads all persisted sellers from the sellers file.
     *
     * <p>If the sellers file does not exist, the repository provides three
     * default sellers for the first execution.</p>
     *
     * @return the persisted sellers or the default sellers
     */
    public List<Seller> loadSellers() {
        if (!Files.exists(sellersPath)) {
            return createDefaultSellers();
        }

        List<Seller> sellers = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(sellersPath);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length == 5) {
                    Seller seller = new Seller(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            data[4]
                    );

                    sellers.add(seller);
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load sellers from file.",
                    e
            );
        }

        return sellers;
    }

    /**
     * Creates the default sellers required for the first execution.
     *
     * @return a list containing three default sellers
     */
    private List<Seller> createDefaultSellers() {
        List<Seller> sellers = new ArrayList<>();

        sellers.add(new Seller(
                "Carlos Rodriguez",
                "1001",
                "3001111111",
                "EMP001",
                "Morning"
        ));

        sellers.add(new Seller(
                "Laura Martinez",
                "1002",
                "3002222222",
                "EMP002",
                "Afternoon"
        ));

        sellers.add(new Seller(
                "Andres Gomez",
                "1003",
                "3003333333",
                "EMP003",
                "Evening"
        ));

        return sellers;
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