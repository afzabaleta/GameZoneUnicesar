package com.gamezone.persistence;

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
 * <p>The repository uses plain text files to preserve person information
 * between application executions.</p>
 *
 * <p>Customers and sellers are stored in separate files inside the
 * application's data directory.</p>
 */
public class PersonRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String SELLERS_FILE = "sellers.txt";

    private final Path customersPath;
    private final Path sellersPath;

    /**
     * Creates a person repository using the default data directory.
     *
     * <p>The repository stores customer information in
     * {@code data/customers.txt} and seller information in
     * {@code data/sellers.txt}.</p>
     */
    public PersonRepository() {
        Path dataPath = Paths.get(DATA_DIRECTORY);
        this.customersPath = dataPath.resolve(CUSTOMERS_FILE);
        this.sellersPath = dataPath.resolve(SELLERS_FILE);
    }

    /**
     * Saves the provided customers to the customers file.
     *
     * @param customers list of customers to persist
     * @throws IllegalStateException if the customers cannot be saved
     */
    public void saveCustomers(List<Customer> customers) {
        try {
            Files.createDirectories(customersPath.getParent());

            List<String> lines = new ArrayList<>();

            for (Customer customer : customers) {
                lines.add(
                        customer.getName() + "|" +
                                customer.getIdentification() + "|" +
                                customer.getPhone() + "|" +
                                customer.getEmail()
                );
            }

            Files.write(customersPath, lines);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save customers to file.",
                    e
            );
        }
    }

    /**
     * Saves the provided sellers to the sellers file.
     *
     * @param sellers list of sellers to persist
     * @throws IllegalStateException if the sellers cannot be saved
     */
    public void saveSellers(List<Seller> sellers) {
        try {
            Files.createDirectories(sellersPath.getParent());

            List<String> lines = new ArrayList<>();

            for (Seller seller : sellers) {
                lines.add(
                        seller.getName() + "|" +
                                seller.getIdentification() + "|" +
                                seller.getPhone() + "|" +
                                seller.getEmployeeCode() + "|" +
                                seller.getWorkShift()
                );
            }

            Files.write(sellersPath, lines);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save sellers to file.",
                    e
            );
        }
    }

    /**
     * Loads all persisted customers from the customers file.
     *
     * <p>If the customers file does not exist, an empty list is returned.</p>
     *
     * @return the persisted customers, or an empty list when the file does not exist
     * @throws IllegalStateException if the customers file cannot be read
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
     * <p>If the sellers file does not exist, the repository provides
     * three default sellers for the first execution.</p>
     *
     * @return the persisted sellers or the default sellers
     * @throws IllegalStateException if the sellers file cannot be read
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