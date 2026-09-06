package gamezone.persistence;

import gamezone.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Repository responsible for managing product storage operations.
 *
 * <p>This class provides methods to save, retrieve and search products
 * in the GameZone system.</p>
 */
public class ProductRepository {

    private final List<Product> products;

    /**
     * Creates an empty product repository.
     */
    public ProductRepository() {
        this.products = new ArrayList<>();
    }

    /**
     * Saves a product in the repository.
     *
     * @param product product to save
     */
    public void save(Product product) {
        products.add(product);
    }

    /**
     * Returns all products stored in the repository.
     *
     * @return list of products
     */
    public List<Product> findAll() {
        return products;
    }

    /**
     * Finds a product by its identifier.
     *
     * @param identifier product identifier
     * @return matching product or null if not found
     */
    public Product findByIdentifier(String identifier) {
        for (Product product : products) {
            if (product.getIdentifier().equals(identifier)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Loads products from storage.
     *
     * @return list of stored products
     */
    public List<Product> loadProducts() {
        return products;
    }

    /**
     * Saves products to a file.
     *
     * @param products list of products to save
     */
    public void saveProducts(List<Product> products) {
        try (FileWriter writer = new FileWriter("products.txt")) {

            for (Product product : products) {
                writer.write(
                        product.getIdentifier() + "," +
                                product.getTitle() + "," +
                                product.getPrice() + "," +
                                product.getAvailableQuantity() + "\n"
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }
}
