package com.gamezone.persistence;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
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
import java.util.function.Function;

/**
 * Handles saving and loading sales to and from a text file.
 *
 * <p>I/O failures are converted into PersistenceException with the file and
 * operation in the message. Invalid or inconsistent persisted records are also
 * reported as persistence failures.</p>
 *
 * <p>The repository does not know where customers, sellers and items are
 * stored: it receives three lookup functions (Indirection), so it no longer
 * depends on the person, product and accessory repositories.</p>
 */
public class SaleRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = "data/sales.txt";
    private static final String FIELD_SEPARATOR = ";";
    private static final String PRODUCT_SEPARATOR = ",";

    private final Function<String, Customer> customerLookup;
    private final Function<String, Seller> sellerLookup;
    private final Function<String, Product> itemLookup;

    /**
     * Creates a SaleRepository that resolves people and items through the
     * given lookup functions.
     *
     * <p>Each function receives an identifier and returns the matching
     * object, or null when it does not exist.</p>
     *
     * @param customerLookup finds a customer by identification
     * @param sellerLookup finds a seller by identification
     * @param itemLookup finds a product or an accessory by identifier
     */
    public SaleRepository(
            Function<String, Customer> customerLookup,
            Function<String, Seller> sellerLookup,
            Function<String, Product> itemLookup) {

        if (customerLookup == null) {
            throw new InvalidDataException("customerLookup", "must not be null");
        }

        if (sellerLookup == null) {
            throw new InvalidDataException("sellerLookup", "must not be null");
        }

        if (itemLookup == null) {
            throw new InvalidDataException("itemLookup", "must not be null");
        }

        this.customerLookup = customerLookup;
        this.sellerLookup = sellerLookup;
        this.itemLookup = itemLookup;
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
        Customer customer = customerLookup.apply(id);

        if (customer != null) {
            return customer;
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
        Seller seller = sellerLookup.apply(id);

        if (seller != null) {
            return seller;
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
        Product item = itemLookup.apply(id);

        if (item != null) {
            return item;
        }

        throw new ResourceNotFoundException("product or accessory", id);
    }
}
