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
import java.util.ArrayList;
import java.util.List;

public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";

    private final Path returnsPath;
    private final SaleService saleService;
    private final ProductService productService;

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
        throw new UnsupportedOperationException(
                "CSV reconstruction is completed in the next commit.");
    }

    private String buildSaleReference(Sale sale) {
        return sale.getDate().toString();
    }
}