package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides business operations for registering and querying sales.
 * Coordinates sale persistence with product stock validation and updates.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;

    /**
     * Creates a SaleService that validates stock through the given
     * ProductService and persists sales through the given SaleRepository.
     *
     * @param saleRepository repository used to persist and load sales
     * @param productService service used to check and update product stock
     */
    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    /**
     * Registers a new sale after validating that it is not null, that it
     * contains at least one product, and that enough stock is available
     * for every product sold. On success, updates the stock of each
     * product and persists the sale together with the existing sales.
     *
     * @param sale the sale to register
     */
    public void registerSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null.");
        }

        if (sale.getProducts() == null || sale.getProducts().isEmpty()) {
            throw new IllegalArgumentException(
                    "A sale must contain at least one product."
            );
        }

        Map<String, Integer> requestedQuantities = countByIdentifier(sale.getProducts());
        List<Product> availableProducts = productService.listProducts();

        validateStock(requestedQuantities, availableProducts);
        updateStock(requestedQuantities, availableProducts);

        List<Sale> sales = saleRepository.loadSales();
        sales.add(sale);
        saleRepository.saveSales(sales);
    }

    /**
     * Lists all registered sales.
     *
     * @return the complete list of sales
     */
    public List<Sale> listSales() {
        return saleRepository.loadSales();
    }

    /**
     * Lists all sales made by a specific customer.
     *
     * @param customerId the identification of the customer
     * @return the sales associated with that customer
     */
    public List<Sale> listSalesByCustomer(String customerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : saleRepository.loadSales()) {
            Customer customer = sale.getCustomer();
            if (customer != null && customer.getIdentification().equals(customerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Lists all sales attended by a specific seller.
     *
     * @param sellerId the identification of the seller
     * @return the sales attended by that seller
     */
    public List<Sale> listSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : saleRepository.loadSales()) {
            Seller seller = sale.getSeller();
            if (seller != null && seller.getIdentification().equals(sellerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    private Map<String, Integer> countByIdentifier(List<Product> products) {
        Map<String, Integer> counts = new HashMap<>();

        for (Product product : products) {
            counts.merge(product.getIdentifier(), 1, Integer::sum);
        }

        return counts;
    }

    private void validateStock(
            Map<String, Integer> requestedQuantities,
            List<Product> availableProducts) {

        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            Product product = findProductById(
                    availableProducts,
                    entry.getKey()
            );

            if (entry.getValue() > product.getAvailableQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product "
                                + product.getIdentifier()
                                + ": requested "
                                + entry.getValue()
                                + ", available "
                                + product.getAvailableQuantity()
                );
            }
        }
    }

    private void updateStock(
            Map<String, Integer> requestedQuantities,
            List<Product> availableProducts) {

        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            Product product = findProductById(
                    availableProducts,
                    entry.getKey()
            );

            int remainingStock =
                    product.getAvailableQuantity() - entry.getValue();

            productService.updateStock(
                    entry.getKey(),
                    remainingStock
            );
        }
    }

    private Product findProductById(
            List<Product> products,
            String identifier) {

        for (Product product : products) {
            if (product.getIdentifier().equals(identifier)) {
                return product;
            }
        }

        throw new IllegalStateException(
                "Product not found for id: " + identifier
        );
    }
}