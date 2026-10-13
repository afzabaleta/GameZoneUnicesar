package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for persisting and loading customers and sellers.
 *
 * <p>I/O and malformed-file errors are represented as PersistenceException
 * with the affected file and operation in the message.</p>
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
     * Saves customers to the customers file.
     *
     * @param customers list of customers to persist
     */
    public void saveCustomers(List<Customer> customers) {
        try {
            Files.createDirectories(customersPath.getParent());
            List<String> lines = new ArrayList<>();
            for (Customer customer : customers) {
                lines.add(customer.getName() + "|"
                        + customer.getIdentification() + "|"
                        + customer.getPhone() + "|"
                        + customer.getEmail());
            }
            Files.write(customersPath, lines);
        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to write customers file '" + customersPath + "'.",
                    e
            );
        }
    }

    /**
     * Saves sellers to the sellers file.
     *
     * @param sellers list of sellers to persist
     */
    public void saveSellers(List<Seller> sellers) {
        try {
            Files.createDirectories(sellersPath.getParent());
            List<String> lines = new ArrayList<>();
            for (Seller seller : sellers) {
                lines.add(seller.getName() + "|"
                        + seller.getIdentification() + "|"
                        + seller.getPhone() + "|"
                        + seller.getEmployeeCode() + "|"
                        + seller.getWorkShift());
            }
            Files.write(sellersPath, lines);
        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to write sellers file '" + sellersPath + "'.",
                    e
            );
        }
    }

    /**
     * Loads customers from the customers file.
     *
     * @return persisted customers, or an empty list if the file does not exist
     */
    public List<Customer> loadCustomers() {
        if (!Files.exists(customersPath)) {
            return new ArrayList<>();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(customersPath);
        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to read customers file '" + customersPath + "'.",
                    e
            );
        }

        List<Customer> customers = new ArrayList<>();
        try {
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] data = line.split("\\|", -1);
                if (data.length != 4) {
                    throw new IllegalArgumentException(
                            "Expected 4 fields in a customer record, found " + data.length
                    );
                }
                customers.add(new Customer(data[0], data[1], data[2], data[3]));
            }
        } catch (RuntimeException e) {
            throw new PersistenceException(
                    "Unable to parse customers file '" + customersPath
                            + "'. The file may contain a corrupted record.",
                    e
            );
        }
        return customers;
    }

    /**
     * Loads sellers from the sellers file or initializes default sellers.
     *
     * @return persisted sellers or the default sellers
     */
    public List<Seller> loadSellers() {
        if (!Files.exists(sellersPath)) {
            List<Seller> defaultSellers = createDefaultSellers();
            saveSellers(defaultSellers);
            return defaultSellers;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(sellersPath);
        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to read sellers file '" + sellersPath + "'.",
                    e
            );
        }

        List<Seller> sellers = new ArrayList<>();
        try {
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] data = line.split("\\|", -1);
                if (data.length != 5) {
                    throw new IllegalArgumentException(
                            "Expected 5 fields in a seller record, found " + data.length
                    );
                }
                sellers.add(new Seller(data[0], data[1], data[2], data[3], data[4]));
            }
        } catch (RuntimeException e) {
            throw new PersistenceException(
                    "Unable to parse sellers file '" + sellersPath
                            + "'. The file may contain a corrupted record.",
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
        sellers.add(new Seller("Carlos Rodriguez", "1001", "3001111111", "EMP001", "Morning"));
        sellers.add(new Seller("Laura Martinez", "1002", "3002222222", "EMP002", "Afternoon"));
        sellers.add(new Seller("Andres Gomez", "1003", "3003333333", "EMP003", "Evening"));
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