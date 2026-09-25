package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
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

    /**
     * Creates a return repository using the required services.
     *
     * @param saleService service used to resolve sales
     * @param productService service used to resolve products
     * @throws IllegalArgumentException if any dependency is null
     */
    public ReturnRepository(
            SaleService saleService,
            ProductService productService) {

        if (saleService == null) {
            throw new IllegalArgumentException(
                    "Sale service cannot be null.");
        }

        if (productService == null) {
            throw new IllegalArgumentException(
                    "Product service cannot be null.");
        }

        this.returnsPath = Paths.get(FILE_PATH);
        this.saleService = saleService;
        this.productService = productService;
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

            List<String> lines = new ArrayList<>();

            for (Return returnItem : returns) {
                lines.add(toCsv(returnItem));
            }

            Files.write(returnsPath, lines);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save returns.", e);
        }
    }

    /**
     * Loads all persisted returns from the CSV file.
     *
     * @return list of persisted returns
     * @throws IllegalStateException if the file cannot be read
     */
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();

        if (!Files.exists(returnsPath)) {
            return returns;
        }

        try {
            for (String line : Files.readAllLines(returnsPath)) {

                if (!line.isBlank()) {
                    returns.add(fromCsv(line));
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load returns.", e);
        }

        return returns;
    }

    private String toCsv(Return returnItem) {

        String productIds = returnItem.getReturnedProducts()
                .stream()
                .map(Product::getIdentifier)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        return returnItem.getIdentifier() + ";"
                + returnItem.getReturnDate() + ";"
                + buildSaleReference(returnItem.getOriginalSale()) + ";"
                + productIds + ";"
                + returnItem.getReason().replace(";", ",") + ";"
                + returnItem.getRefundAmount();
    }

    private Return fromCsv(String line) {
        String[] fields = line.split(";", -1);

        if (fields.length != 6) {
            throw new IllegalStateException(
                    "Invalid return record. Expected 6 fields.");
        }

        String identifier = fields[0].trim();

        LocalDate returnDate = LocalDate.parse(fields[1].trim());

        String saleReference = fields[2].trim();

        String[] productIds = fields[3].split(",");

        String reason = fields[4].trim();

        Sale sale = findSaleByReference(saleReference);

        List<Product> products = new ArrayList<>();

        for (String productId : productIds) {
            Product product = findProductById(productId.trim());
            products.add(product);
        }

        return new Return(
                identifier,
                returnDate,
                sale,
                products,
                reason
        );
    }

    private Sale findSaleByReference(String saleReference) {
        for (Sale sale : saleService.listSales()) {
            if (sale.getDate().toString().equals(saleReference)) {
                return sale;
            }
        }

        throw new IllegalStateException(
                "Sale not found for reference: " + saleReference);
    }

    private Product findProductById(String productId) {
        for (Product product : productService.listProducts()) {
            if (product.getIdentifier().equals(productId)) {
                return product;
            }
        }

        throw new IllegalStateException(
                "Product not found for id: " + productId);
    }

    private String buildSaleReference(Sale sale) {
        return sale.getDate().toString();
    }
}