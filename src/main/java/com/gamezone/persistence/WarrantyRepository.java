package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.service.ProductService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence of warranty records.
 */
public class WarrantyRepository {

    private static final String FILE_PATH =
            "data/warranties.csv";

    private static final String SEPARATOR = ";";

    private final Path filePath;
    private final SaleRepository saleRepository;
    private final ProductService productService;

    /**
     * Creates a warranty repository.
     *
     * @param saleRepository repository used to resolve sales
     * @param productService service used to resolve products
     */
    public WarrantyRepository(
            SaleRepository saleRepository,
            ProductService productService) {

        if (saleRepository == null) {
            throw new IllegalArgumentException(
                    "Sale repository cannot be null."
            );
        }

        if (productService == null) {
            throw new IllegalArgumentException(
                    "Product service cannot be null."
            );
        }

        this.filePath = Paths.get(FILE_PATH);
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    /**
     * Saves all warranties to the CSV file.
     *
     * @param warranties warranties to persist
     */
    public void saveAll(List<Warranty> warranties) {

        if (warranties == null) {
            throw new IllegalArgumentException(
                    "Warranty list cannot be null."
            );
        }

        try {

            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            try (BufferedWriter writer =
                         Files.newBufferedWriter(filePath)) {

                for (Warranty warranty : warranties) {

                    writer.write(toCsvLine(warranty));
                    writer.newLine();
                }
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not save warranties.",
                    e
            );
        }
    }

    /**
     * Loads all warranties from the CSV file.
     *
     * @return list of loaded warranties
     */
    public List<Warranty> loadAll() {

        List<Warranty> warranties = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return warranties;
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(filePath)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                Warranty warranty = fromCsvLine(line);

                if (warranty != null) {
                    warranties.add(warranty);
                }
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not load warranties.",
                    e
            );
        }

        return warranties;
    }

    /**
     * Converts a warranty into a CSV record.
     *
     * @param warranty warranty to convert
     * @return CSV representation of the warranty
     */
    private String toCsvLine(Warranty warranty) {

        String type;

        if (warranty instanceof BasicWarranty) {

            type = "BASIC";

        } else if (warranty instanceof ExtendedWarranty) {

            type = "EXTENDED";

        } else {

            throw new IllegalArgumentException(
                    "Unsupported warranty type."
            );
        }

        return String.join(
                SEPARATOR,
                type,
                warranty.getId(),
                warranty.getProduct().getIdentifier(),
                warranty.getSale().getDate().toString(),
                warranty.getStartDate().toString()
        );
    }

    /**
     * Converts a CSV record into a concrete warranty object.
     *
     * @param line CSV record
     * @return reconstructed warranty or null if references cannot be resolved
     */
    private Warranty fromCsvLine(String line) {

        String[] fields =
                line.split(SEPARATOR, -1);

        if (fields.length != 5) {

            throw new IllegalArgumentException(
                    "Invalid warranty record."
            );
        }

        String type = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleDateText = fields[3];

        LocalDate startDate =
                LocalDate.parse(fields[4]);

        Product product =
                findProduct(productId);

        Sale sale =
                findSale(saleDateText);

        if (product == null || sale == null) {
            return null;
        }

        if ("BASIC".equalsIgnoreCase(type)) {

            return new BasicWarranty(
                    id,
                    product,
                    sale,
                    startDate
            );
        }

        if ("EXTENDED".equalsIgnoreCase(type)) {

            return new ExtendedWarranty(
                    id,
                    product,
                    sale,
                    startDate
            );
        }

        throw new IllegalArgumentException(
                "Unsupported warranty type: " + type
        );
    }

    /**
     * Finds a product by identifier.
     *
     * @param productId product identifier
     * @return matching product or null
     */
    private Product findProduct(String productId) {

        for (Product product :
                productService.listProducts()) {

            if (product.getIdentifier()
                    .equalsIgnoreCase(productId)) {

                return product;
            }
        }

        return null;
    }

    /**
     * Finds a sale using its stored date reference.
     *
     * @param saleDateText sale date reference
     * @return matching sale or null
     */
    private Sale findSale(String saleDateText) {

        for (Sale sale :
                saleRepository.loadSales()) {

            if (sale.getDate()
                    .toString()
                    .equals(saleDateText)) {

                return sale;
            }
        }

        return null;
    }
}