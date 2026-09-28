package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides CSV persistence operations for product returns.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";

    private final Path returnsPath;
    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Creates a return repository using the required services.
     *
     * @param saleService service used to resolve sales
     * @param productService service used to resolve products
     * @param accessoryService service used to resolve accessories
     * @throws IllegalArgumentException if any dependency is null
     */
    public ReturnRepository(
            SaleService saleService,
            ProductService productService,
            AccessoryService accessoryService) {

        if (saleService == null) {
            throw new IllegalArgumentException(
                    "Sale service cannot be null."
            );
        }

        if (productService == null) {
            throw new IllegalArgumentException(
                    "Product service cannot be null."
            );
        }

        if (accessoryService == null) {
            throw new IllegalArgumentException(
                    "Accessory service cannot be null."
            );
        }

        this.returnsPath = Paths.get(FILE_PATH);
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Saves all returns to the CSV persistence file.
     *
     * @param returns returns to persist
     * @throws IllegalStateException if the file cannot be written
     */
    public void saveAll(List<Return> returns) {

        try {

            Path parent = returnsPath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines =
                    new ArrayList<>();

            for (Return returnItem : returns) {
                lines.add(toCsv(returnItem));
            }

            Files.write(
                    returnsPath,
                    lines
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to save returns.",
                    e
            );
        }
    }

    /**
     * Loads all persisted returns from the CSV file.
     *
     * <p>Records created before the warranty refund field was added
     * contain six fields and are loaded with a warranty refund of zero.</p>
     *
     * @return list of persisted returns
     * @throws IllegalStateException if the file cannot be read
     */
    public List<Return> loadAll() {

        List<Return> returns =
                new ArrayList<>();

        if (!Files.exists(returnsPath)) {
            return returns;
        }

        try {

            for (String line :
                    Files.readAllLines(returnsPath)) {

                if (!line.isBlank()) {
                    returns.add(
                            fromCsv(line)
                    );
                }
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to load returns.",
                    e
            );
        }

        return returns;
    }

    /**
     * Converts a return into CSV format.
     *
     * @param returnItem return to serialize
     * @return CSV representation
     */
    private String toCsv(Return returnItem) {

        String productIds =
                returnItem.getReturnedProducts()
                        .stream()
                        .map(Product::getIdentifier)
                        .reduce(
                                (a, b) -> a + "," + b
                        )
                        .orElse("");

        return returnItem.getIdentifier()
                + ";"
                + returnItem.getReturnDate()
                + ";"
                + buildSaleReference(
                returnItem.getOriginalSale()
        )
                + ";"
                + productIds
                + ";"
                + returnItem.getReason()
                .replace(";", ",")
                + ";"
                + returnItem.getRefundAmount()
                + ";"
                + returnItem.getWarrantyRefundAmount();
    }

    /**
     * Reconstructs a return from a CSV record.
     *
     * <p>Both the current seven-field format and the previous
     * six-field format are supported.</p>
     *
     * @param line CSV record
     * @return reconstructed return
     */
    private Return fromCsv(String line) {

        String[] fields =
                line.split(";", -1);

        if (fields.length != 6
                && fields.length != 7) {

            throw new IllegalStateException(
                    "Invalid return record. Expected 6 or 7 fields."
            );
        }

        String identifier =
                fields[0].trim();

        LocalDate returnDate =
                LocalDate.parse(
                        fields[1].trim()
                );

        String saleReference =
                fields[2].trim();

        String[] productIds =
                fields[3].split(",");

        String reason =
                fields[4].trim();

        double warrantyRefundAmount = 0.0;

        if (fields.length == 7) {

            try {

                warrantyRefundAmount =
                        Double.parseDouble(
                                fields[6].trim()
                        );

            } catch (NumberFormatException e) {

                throw new IllegalStateException(
                        "Invalid warranty refund amount: "
                                + fields[6],
                        e
                );
            }
        }

        Sale sale =
                findSaleByReference(
                        saleReference
                );

        List<Product> products =
                new ArrayList<>();

        for (String productId : productIds) {

            Product product =
                    findItemById(
                            productId.trim()
                    );

            products.add(product);
        }

        return new Return(
                identifier,
                returnDate,
                sale,
                products,
                reason,
                warrantyRefundAmount
        );
    }

    /**
     * Finds a sale using its stored reference.
     *
     * @param saleReference sale reference
     * @return matching sale
     */
    private Sale findSaleByReference(
            String saleReference) {

        for (Sale sale :
                saleService.listSales()) {

            if (sale.getDate()
                    .toString()
                    .equals(saleReference)) {

                return sale;
            }
        }

        throw new IllegalStateException(
                "Sale not found for reference: "
                        + saleReference
        );
    }

    /**
     * Finds a product or accessory by identifier.
     *
     * @param itemId product or accessory identifier
     * @return matching item
     */
    private Product findItemById(String itemId) {

        for (Product product :
                productService.listProducts()) {

            if (product.getIdentifier()
                    .equals(itemId)) {

                return product;
            }
        }

        for (Accessory accessory :
                accessoryService.listAccessories()) {

            if (accessory.getIdentifier()
                    .equals(itemId)) {

                return accessory;
            }
        }

        throw new IllegalStateException(
                "Product or accessory not found for id: "
                        + itemId
        );
    }

    /**
     * Builds the reference used to identify the original sale.
     *
     * @param sale original sale
     * @return sale reference
     */
    private String buildSaleReference(Sale sale) {

        return sale.getDate().toString();
    }
}