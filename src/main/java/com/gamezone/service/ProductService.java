package gamezone.service;

import gamezone.model.Product;
import gamezone.persistence.ProductRepository;
import gamezone.model.VideoGame;
import gamezone.model.Console;
import java.util.List;

/**
 * Service class that contains business logic related to products.
 *
 * <p>This class connects the application logic with the product repository.</p>
 */
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a product service with a repository.
     *
     * @param productRepository repository used to manage products
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Registers a product in the repository.
     *
     * @param product product to register
     */
    public void registerProduct(Product product) {
        productRepository.save(product);
    }
    /**
     * Registers a video game product.
     *
     * @param videoGame video game to register
     */
    public void registerVideoGame(VideoGame videoGame) {
        productRepository.save(videoGame);
    }

    /**
     * Registers a console product.
     *
     * @param console console to register
     */
    public void registerConsole(Console console) {
        productRepository.save(console);
    }

    /**
     * Returns all registered products.
     *
     * @return list of products
     */
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    /**
     * Updates the stock quantity of a product.
     *
     * @param identifier product identifier
     * @param quantity new available quantity
     */
    public void updateProductStock(String identifier, int quantity) {
        Product product = productRepository.findByIdentifier(identifier);

        if (product != null) {
            product.setAvailableQuantity(quantity);
        }
    }

    /**
     * Registers a new product.
     *
     * @param product product to register
     */
    public void addProduct(Product product) {
        productRepository.save(product);
    }

    /**
     * Searches a product by its identifier.
     *
     * @param identifier product identifier
     * @return product found or null if it does not exist
     */
    public Product getProductByIdentifier(String identifier) {
        return productRepository.findByIdentifier(identifier);
    }

    /**
     * Returns the description of a product.
     *
     * @param identifier product identifier
     * @return product description
     */
    public String getProductDescription(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);

        if (product != null) {
            return product.getDescription();
        }

        return "Product not found";
    }
}
