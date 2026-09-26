package com.gamezone.persistence;

import com.gamezone.model.Warranty;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.nio.file.Path;
import java.nio.file.Paths;

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
}