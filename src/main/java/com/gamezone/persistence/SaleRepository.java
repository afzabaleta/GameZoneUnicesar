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
 *
 * <p>A sale stores references to a customer, seller, products and the
 * promotion information applied to the transaction.</p>
 */
public class SaleRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = "data/sales.txt";
    private static final String FIELD_SEPARATOR = ";";
    private static final String PRODUCT_SEPARATOR = ",";

    private final PersonRepository personRepository;
    private final ProductRepository productRepository;
    private final AccessoryRepository accessoryRepository;

    /**
     * Creates a SaleRepository able to resolve customers, sellers,
     * products and accessories by their identifiers.
     *
     * @param personRepository repository used to look up customers and sellers
     * @param productRepository repository used to look up products
     * @param accessoryRepository repository used to look up accessories
     */
    public SaleRepository(
            PersonRepository personRepository,
            ProductRepository productRepository,
            AccessoryRepository accessoryRepository) {

        if (personRepository == null) {
            throw new IllegalArgumentException(
                    "Person repository cannot be null.");
        }

        if (productRepository == null) {
            throw new IllegalArgumentException(
                    "Product repository cannot be null.");
        }

        if (accessoryRepository == null) {
            throw new IllegalArgumentException(
                    "Accessory repository cannot be null.");
        }

        this.personRepository = personRepository;
        this.productRepository = productRepository;
        this.accessoryRepository = accessoryRepository;
    }

    /**
     * Saves the given sales to the sales file.
     *
     * @param sales sales to persist
     */
    public void saveSales(List<Sale> sales) {

        try {
            Files.createDirectories(
                    Paths.get(DATA_DIRECTORY)
            );

            try (BufferedWriter writer =
                         new BufferedWriter(
                                 new FileWriter(FILE_PATH))) {

                for (Sale sale : sales) {
                    writer.write(buildLine(sale));
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error saving sales to file: "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Loads all sales from the sales file.
     *
     * <p>Four-field legacy records are also accepted. In that case,
     * the promotion name remains null and the discount amount is zero.</p>
     *
     * @return list of persisted sales
     */
    public List<Sale> loadSales() {

        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return sales;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                sales.add(parseLine(line));
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error loading sales from file: "
                            + e.getMessage(),
                    e
            );
        }

        return sales;
    }

    /**
     * Builds the CSV-like representation of a sale.
     *
     * <p>Format:</p>
     *
     * <pre>
     * date;customerId;sellerId;productIds;promotionName;discountAmount
     * </pre>
     *
     * @param sale sale to serialize
     * @return serialized sale
     */
    private String buildLine(Sale sale) {

        StringBuilder productIds =
                new StringBuilder();

        List<Product> products =
                sale.getProducts();

        for (int i = 0; i < products.size(); i++) {

            productIds.append(
                    products.get(i).getIdentifier()
            );

            if (i < products.size() - 1) {
                productIds.append(
                        PRODUCT_SEPARATOR
                );
            }
        }

        String promotionName =
                sale.getAppliedPromotionName();

        if (promotionName == null) {
            promotionName = "";
        }

        return sale.getDate()
                + FIELD_SEPARATOR
                + sale.getCustomer().getIdentification()
                + FIELD_SEPARATOR
                + sale.getSeller().getIdentification()
                + FIELD_SEPARATOR
                + productIds
                + FIELD_SEPARATOR
                + promotionName
                + FIELD_SEPARATOR
                + sale.getDiscountAmount();
    }

    /**
     * Parses a persisted sale.
     *
     * <p>Both the legacy four-field format and the new six-field
     * format are supported.</p>
     *
     * @param line persisted sale line
     * @return reconstructed sale
     */
    private Sale parseLine(String line) {

        String[] fields =
                line.split(FIELD_SEPARATOR, -1);

        if (fields.length != 4 && fields.length != 6) {
            throw new IllegalStateException(
                    "Expected 4 or 6 fields, found "
                            + fields.length
            );
        }

        LocalDate date =
                LocalDate.parse(fields[0]);

        Customer customer =
                findCustomerById(fields[1]);

        Seller seller =
                findSellerById(fields[2]);

        List<Product> products =
                new ArrayList<>();

        for (String productId :
                fields[3].split(PRODUCT_SEPARATOR)) {

            products.add(
                    findItemById(productId)
            );
        }

        Sale sale =
                new Sale(
                        date,
                        customer,
                        seller,
                        products
                );

        if (fields.length == 6) {

            String promotionName =
                    fields[4].trim();

            if (!promotionName.isBlank()) {
                sale.setAppliedPromotionName(
                        promotionName
                );
            }

            double discountAmount =
                    Double.parseDouble(
                            fields[5].trim()
                    );

            sale.setDiscountAmount(
                    discountAmount
            );
        }

        return sale;
    }

    /**
     * Finds a customer by identification.
     *
     * @param id customer identification
     * @return matching customer
     */
    private Customer findCustomerById(String id) {

        for (Customer customer :
                personRepository.loadCustomers()) {

            if (customer.getIdentification()
                    .equals(id)) {

                return customer;
            }
        }

        throw new IllegalStateException(
                "Customer not found for id: " + id
        );
    }

    /**
     * Finds a seller by identification.
     *
     * @param id seller identification
     * @return matching seller
     */
    private Seller findSellerById(String id) {

        for (Seller seller :
                personRepository.loadSellers()) {

            if (seller.getIdentification()
                    .equals(id)) {

                return seller;
            }
        }

        throw new IllegalStateException(
                "Seller not found for id: " + id
        );
    }

    /**
     * Finds a product or accessory by identifier.
     *
     * @param id item identifier
     * @return matching product or accessory
     */
    private Product findItemById(String id) {

        try {

            for (Product product :
                    productRepository.loadProducts()) {

                if (product.getIdentifier()
                        .equals(id)) {

                    return product;
                }
            }

        } catch (IOException | ClassNotFoundException e) {

            throw new IllegalStateException(
                    "Unable to load products from file",
                    e
            );
        }

        Product accessory =
                accessoryRepository.findByIdentifier(id);

        if (accessory != null) {
            return accessory;
        }

        throw new IllegalStateException(
                "Product or accessory not found for id: "
                        + id
        );
    }
}