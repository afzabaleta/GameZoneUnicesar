package com.gamezone.persistence;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Accessory;
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
 * <p>I/O failures are converted into PersistenceException with the file and
 * operation in the message. Invalid or inconsistent persisted records are also
 * reported as persistence failures.</p>
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
     * Creates a SaleRepository able to resolve people, products and accessories.
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
            throw new InvalidDataException("personRepository", "must not be null");
        }

        if (productRepository == null) {
            throw new InvalidDataException("productRepository", "must not be null");
        }

        if (accessoryRepository == null) {
            throw new InvalidDataException("accessoryRepository", "must not be null");
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
            Files.createDirectories(Paths.get(DATA_DIRECTORY));

            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(FILE_PATH))) {

                for (Sale sale : sales) {
                    writer.write(buildLine(sale));
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to write sales file '" + FILE_PATH + "'.",
                    e
            );
        }
    }

    /**
     * Loads all sales from the sales file.
     *
     * @return list of persisted sales
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
                if (!line.isBlank()) {
                    sales.add(parseLine(line));
                }
            }

        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to read sales file '" + FILE_PATH + "'.",
                    e
            );

        } catch (RuntimeException e) {
            if (e instanceof PersistenceException persistenceException) {
                throw persistenceException;
            }

            throw new PersistenceException(
                    "Unable to parse sales file '" + FILE_PATH
                            + "'. The file may contain corrupted or inconsistent records.",
                    e
            );
        }

        return sales;
    }

    /**
     * Builds the persisted representation of a sale.
     *
     * @param sale sale to serialize
     * @return serialized sale
     */
    private String buildLine(Sale sale) {
        StringBuilder productIds = new StringBuilder();
        List<Product> products = sale.getProducts();

        for (int i = 0; i < products.size(); i++) {
            productIds.append(products.get(i).getIdentifier());

            if (i < products.size() - 1) {
                productIds.append(PRODUCT_SEPARATOR);
            }
        }

        String promotionName = sale.getAppliedPromotionName();

        if (promotionName == null) {
            promotionName = "";
        }

        return sale.getDate()
                + FIELD_SEPARATOR + sale.getCustomer().getIdentification()
                + FIELD_SEPARATOR + sale.getSeller().getIdentification()
                + FIELD_SEPARATOR + productIds
                + FIELD_SEPARATOR + promotionName
                + FIELD_SEPARATOR + sale.getDiscountAmount()
                + FIELD_SEPARATOR + sale.getWarrantyAdditionalCost();
    }

    /**
     * Parses a persisted sale. Legacy four-field and previous six-field records
     * remain supported.
     *
     * @param line persisted sale line
     * @return reconstructed sale
     */
    private Sale parseLine(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, -1);

        if (fields.length != 4
                && fields.length != 6
                && fields.length != 7) {

            throw new IllegalArgumentException(
                    "Expected 4, 6 or 7 fields, found " + fields.length
            );
        }

        LocalDate date = LocalDate.parse(fields[0]);
        Customer customer = findCustomerById(fields[1]);
        Seller seller = findSellerById(fields[2]);

        List<Product> products = new ArrayList<>();

        for (String productId : fields[3].split(PRODUCT_SEPARATOR)) {
            products.add(findItemById(productId));
        }

        Sale sale = new Sale(date, customer, seller, products);

        if (fields.length == 6 || fields.length == 7) {
            String promotionName = fields[4].trim();

            if (!promotionName.isBlank()) {
                sale.setAppliedPromotionName(promotionName);
            }

            sale.setDiscountAmount(Double.parseDouble(fields[5].trim()));
        }

        if (fields.length == 7) {
            sale.setWarrantyAdditionalCost(
                    Double.parseDouble(fields[6].trim())
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
        for (Customer customer : personRepository.loadCustomers()) {
            if (customer.getIdentification().equals(id)) {
                return customer;
            }
        }

        throw new ResourceNotFoundException("customer", id);
    }

    /**
     * Finds a seller by identification.
     *
     * @param id seller identification
     * @return matching seller
     */
    private Seller findSellerById(String id) {
        for (Seller seller : personRepository.loadSellers()) {
            if (seller.getIdentification().equals(id)) {
                return seller;
            }
        }

        throw new ResourceNotFoundException("seller", id);
    }

    /**
     * Finds a product or accessory by identifier.
     *
     * @param id product or accessory identifier
     * @return matching item
     */
    private Product findItemById(String id) {
        for (Product product : productRepository.loadProducts()) {
            if (product.getIdentifier().equals(id)) {
                return product;
            }
        }

        Accessory accessory = accessoryRepository.findByIdentifier(id);

        if (accessory != null) {
            return accessory;
        }

        throw new ResourceNotFoundException("product or accessory", id);
    }
}

