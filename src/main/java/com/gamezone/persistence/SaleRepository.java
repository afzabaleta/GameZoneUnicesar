package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading sales to and from a text file.
 * Since a Sale references Customer, Seller and Product objects,
 * this repository stores only their identifiers and relies on
 * PersonRepository and ProductRepository to reconstruct the real
 * objects when loading sales back into memory.
 */
public class SaleRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = "data/sales.txt";
    private static final String FIELD_SEPARATOR = ";";
    private static final String PRODUCT_SEPARATOR = ",";

    private final PersonRepository personRepository;
    private final ProductRepository productRepository;

    /**
     * Creates a SaleRepository able to resolve customers, sellers and
     * products by their identifiers.
     *
     * @param personRepository  repository used to look up customers and sellers
     * @param productRepository repository used to look up products
     */
    public SaleRepository(PersonRepository personRepository, ProductRepository productRepository) {
        this.personRepository = personRepository;
        this.productRepository = productRepository;
    }

    /**
     * Saves the given sales to the sales file, overwriting its previous content.
     *
     * @param sales the list of sales to persist
     */
    public void saveSales(List<Sale> sales) {
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Sale sale : sales) {
                    writer.write(buildLine(sale));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error saving sales to file: " + e.getMessage(), e
            );
        }
    }

    /**
     * Loads all sales from the sales file, reconstructing each Sale with
     * real Customer, Seller and Product references.
     *
     * @return the list of sales stored in the file
     */
    public List<Sale> loadSales() {
        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return sales;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                sales.add(parseLine(line));
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error loading sales from file: " + e.getMessage(), e
            );
        }

        return sales;
    }

    private String buildLine(Sale sale) {
        StringBuilder productIds = new StringBuilder();
        List<Product> products = sale.getProducts();

        for (int i = 0; i < products.size(); i++) {
            productIds.append(products.get(i).getIdentifier());

            if (i < products.size() - 1) {
                productIds.append(PRODUCT_SEPARATOR);
            }
        }

        return sale.getDate() + FIELD_SEPARATOR
                + sale.getCustomer().getIdentification() + FIELD_SEPARATOR
                + sale.getSeller().getIdentification() + FIELD_SEPARATOR
                + productIds;
    }

    private Sale parseLine(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, -1);

        if (fields.length != 4) {
            throw new IllegalStateException(
                    "Expected 4 fields, found " + fields.length
            );
        }

        LocalDate date = LocalDate.parse(fields[0]);
        Customer customer = findCustomerById(fields[1]);
        Seller seller = findSellerById(fields[2]);

        List<Product> products = new ArrayList<>();

        for (String productId : fields[3].split(PRODUCT_SEPARATOR)) {
            products.add(findProductById(productId));
        }

        return new Sale(date, customer, seller, products);
    }

    private Customer findCustomerById(String id) {
        for (Customer customer : personRepository.loadCustomers()) {
            if (customer.getIdentification().equals(id)) {
                return customer;
            }
        }

        throw new IllegalStateException("Customer not found for id: " + id);
    }

    private Seller findSellerById(String id) {
        for (Seller seller : personRepository.loadSellers()) {
            if (seller.getIdentification().equals(id)) {
                return seller;
            }
        }

        throw new IllegalStateException("Seller not found for id: " + id);
    }

    private Product findProductById(String id) {
        try {
            for (Product product : productRepository.loadProducts()) {
                if (product.getIdentifier().equals(id)) {
                    return product;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Unable to load products from file",
                    e
            );
        }

        throw new IllegalStateException("Product not found for id: " + id);
    }
}