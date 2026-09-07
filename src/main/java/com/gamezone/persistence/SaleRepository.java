package com.gamezone.persistence;

import com.gamezone.model.Sale;

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
        // TODO: implement in commit #5
    }

    /**
     * Loads all sales from the sales file, reconstructing each Sale with
     * real Customer, Seller and Product references.
     *
     * @return the list of sales stored in the file
     */
    public List<Sale> loadSales() {
        // TODO: implement in commit #4
        return null;
    }
}