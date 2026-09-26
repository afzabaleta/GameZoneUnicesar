package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides business operations for registering and querying sales.
 *
 * <p>This service coordinates sale persistence with product and accessory
 * stock validation, inventory updates, promotion application and warranty
 * assignment.</p>
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;

    /**
     * Creates a SaleService that validates stock, applies promotions,
     * manages warranties and persists sales through the corresponding
     * services and repository.
     *
     * @param saleRepository repository used to persist and load sales
     * @param productService service used to check and update product stock
     * @param accessoryService service used to check and update accessory stock
     * @param promotionService service used to find applicable promotions
     * @param warrantyService service used to manage product warranties
     */
    public SaleService(
            SaleRepository saleRepository,
            ProductService productService,
            AccessoryService accessoryService,
            PromotionService promotionService,
            WarrantyService warrantyService) {

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

        if (accessoryService == null) {
            throw new IllegalArgumentException(
                    "Accessory service cannot be null."
            );
        }

        if (promotionService == null) {
            throw new IllegalArgumentException(
                    "Promotion service cannot be null."
            );
        }

        if (warrantyService == null) {
            throw new IllegalArgumentException(
                    "Warranty service cannot be null."
            );
        }

        this.saleRepository = saleRepository;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new sale after validating stock and applying
     * the best active promotion.
     *
     * @param sale the sale to register
     */
    public void registerSale(Sale sale) {
        validateSale(sale);

        Map<String, Integer> productQuantities =
                countProductQuantities(sale.getProducts());

        Map<String, Integer> accessoryQuantities =
                countAccessoryQuantities(sale.getProducts());

        List<Product> availableProducts =
                productService.listProducts();

        List<Accessory> availableAccessories =
                accessoryService.listAccessories();

        validateProductStock(
                productQuantities,
                availableProducts
        );

        validateAccessoryStock(
                accessoryQuantities,
                availableAccessories
        );

        applyBestPromotion(sale);

        updateProductStock(
                productQuantities,
                availableProducts
        );

        updateAccessoryStock(
                accessoryQuantities,
                availableAccessories
        );

        List<Sale> sales = saleRepository.loadSales();
        sales.add(sale);
        saleRepository.saveSales(sales);
    }

    /**
     * Applies the active promotion that provides the highest
     * monetary discount to the sale.
     *
     * @param sale sale to evaluate
     */
    private void applyBestPromotion(Sale sale) {

        Promotion promotion =
                promotionService.findBestPromotionFor(sale);

        if (promotion == null) {
            sale.setAppliedPromotionName(null);
            sale.setDiscountAmount(0.0);
            return;
        }

        double discount =
                promotion.calculateDiscount(sale);

        sale.setAppliedPromotionName(
                promotion.getName()
        );

        sale.setDiscountAmount(discount);
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

            if (customer != null
                    && customer.getIdentification().equals(customerId)) {
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

            if (seller != null
                    && seller.getIdentification().equals(sellerId)) {
                result.add(sale);
            }
        }

        return result;
    }

    /**
     * Validates the basic sale rules.
     *
     * @param sale sale to validate
     */
    private void validateSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException(
                    "Sale cannot be null."
            );
        }

        if (sale.getProducts() == null
                || sale.getProducts().isEmpty()) {
            throw new IllegalArgumentException(
                    "A sale must contain at least one product."
            );
        }
    }

    /**
     * Counts only regular products included in the sale.
     *
     * @param products sale items
     * @return requested quantities grouped by identifier
     */
    private Map<String, Integer> countProductQuantities(
            List<Product> products) {

        Map<String, Integer> counts = new HashMap<>();

        for (Product product : products) {
            if (!(product instanceof Accessory)) {
                counts.merge(
                        product.getIdentifier(),
                        1,
                        Integer::sum
                );
            }
        }

        return counts;
    }

    /**
     * Counts only accessories included in the sale.
     *
     * @param products sale items
     * @return requested accessory quantities grouped by identifier
     */
    private Map<String, Integer> countAccessoryQuantities(
            List<Product> products) {

        Map<String, Integer> counts = new HashMap<>();

        for (Product product : products) {
            if (product instanceof Accessory) {
                counts.merge(
                        product.getIdentifier(),
                        1,
                        Integer::sum
                );
            }
        }

        return counts;
    }

    /**
     * Validates stock for regular products.
     *
     * @param requestedQuantities requested product quantities
     * @param availableProducts available products
     */
    private void validateProductStock(
            Map<String, Integer> requestedQuantities,
            List<Product> availableProducts) {

        for (Map.Entry<String, Integer> entry
                : requestedQuantities.entrySet()) {

            Product product = findProductById(
                    availableProducts,
                    entry.getKey()
            );

            if (product.getAvailableQuantity() < 0) {
                throw new IllegalStateException(
                        "Invalid stock for product "
                                + product.getIdentifier()
                );
            }

            if (entry.getValue()
                    > product.getAvailableQuantity()) {

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

    /**
     * Validates stock for accessories.
     *
     * @param requestedQuantities requested accessory quantities
     * @param availableAccessories available accessories
     */
    private void validateAccessoryStock(
            Map<String, Integer> requestedQuantities,
            List<Accessory> availableAccessories) {

        for (Map.Entry<String, Integer> entry
                : requestedQuantities.entrySet()) {

            Accessory accessory = findAccessoryById(
                    availableAccessories,
                    entry.getKey()
            );

            if (accessory.getAvailableQuantity() < 0) {
                throw new IllegalStateException(
                        "Invalid stock for accessory "
                                + accessory.getIdentifier()
                );
            }

            if (entry.getValue()
                    > accessory.getAvailableQuantity()) {

                throw new IllegalStateException(
                        "Insufficient stock for accessory "
                                + accessory.getIdentifier()
                                + ": requested "
                                + entry.getValue()
                                + ", available "
                                + accessory.getAvailableQuantity()
                );
            }
        }
    }

    /**
     * Updates stock for regular products.
     *
     * @param requestedQuantities requested product quantities
     * @param availableProducts available products
     */
    private void updateProductStock(
            Map<String, Integer> requestedQuantities,
            List<Product> availableProducts) {

        for (Map.Entry<String, Integer> entry
                : requestedQuantities.entrySet()) {

            Product product = findProductById(
                    availableProducts,
                    entry.getKey()
            );

            int remainingStock =
                    product.getAvailableQuantity()
                            - entry.getValue();

            productService.updateStock(
                    entry.getKey(),
                    remainingStock
            );
        }
    }

    /**
     * Updates stock for accessories.
     *
     * @param requestedQuantities requested accessory quantities
     * @param availableAccessories available accessories
     */
    private void updateAccessoryStock(
            Map<String, Integer> requestedQuantities,
            List<Accessory> availableAccessories) {

        for (Map.Entry<String, Integer> entry
                : requestedQuantities.entrySet()) {

            Accessory accessory = findAccessoryById(
                    availableAccessories,
                    entry.getKey()
            );

            int remainingStock =
                    accessory.getAvailableQuantity()
                            - entry.getValue();

            accessoryService.updateStock(
                    entry.getKey(),
                    remainingStock
            );
        }
    }

    /**
     * Finds a regular product by identifier.
     *
     * @param products products to search
     * @param identifier product identifier
     * @return matching product
     */
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

    /**
     * Finds an accessory by identifier.
     *
     * @param accessories accessories to search
     * @param identifier accessory identifier
     * @return matching accessory
     */
    private Accessory findAccessoryById(
            List<Accessory> accessories,
            String identifier) {

        for (Accessory accessory : accessories) {
            if (accessory.getIdentifier().equals(identifier)) {
                return accessory;
            }
        }

        throw new IllegalStateException(
                "Accessory not found for id: " + identifier
        );
    }
}