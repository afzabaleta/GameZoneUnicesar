package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Warranty;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles persistence of warranty records.
 */
public class WarrantyRepository {

    private static final String FILE_PATH =
            "data/warranties.csv";

    private static final String SEPARATOR = ";";

    private final Path filePath;
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Creates a warranty repository.
     *
     * @param saleService service used to resolve sales
     * @param productService service used to resolve products
     */
    public WarrantyRepository(
            SaleService saleService,
            ProductService productService) {

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

        this.filePath = Paths.get(FILE_PATH);
        this.saleService = saleService;
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
}