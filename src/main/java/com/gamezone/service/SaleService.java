
package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Accessory;
import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.validation.ProductValidator;
import com.gamezone.validation.SaleValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides business operations for registering and querying sales.
 *
 * <p>Sale data is delegated to SaleValidator and product stock checks are
 * delegated to ProductValidator. The service coordinates inventory, promotions,
 * warranties and sale persistence.</p>
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;

    /**
     * Creates a SaleService using the required collaborators.
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
            throw new InvalidDataException(
                    "saleRepository", "Sale repository cannot be null");
        }

        if (productService == null) {
            throw new InvalidDataException(
                    "productService", "Product service cannot be null");
        }

        if (accessoryService == null) {
            throw new InvalidDataException(
                    "accessoryService", "Accessory service cannot be null");
        }

        if (promotionService == null) {
            throw new InvalidDataException(
                    "promotionService", "Promotion service cannot be null");
        }

        if (warrantyService == null) {
            throw new InvalidDataException(
                    "warrantyService", "Warranty service cannot be null");
        }

        this.saleRepository = saleRepository;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a sale using the unified integration flow.
     *
     * @param sale sale to register
     * @param productIdsWithExtendedWarranty identifiers of consoles requesting
     *        extended warranty
     */
    public void registerSale(
            Sale sale,
            List<String> productIdsWithExtendedWarranty) {

        if (sale == null) {
            throw new InvalidDataException(
                    "sale", "Sale cannot be null");
        }

        SaleValidator.validateSaleData(
                sale.getCustomer(),
                sale.getSeller(),
                sale.getProducts()
        );

        for (Product product : sale.getProducts()) {
            if (product == null) {
                ProductValidator.validateProductExists(null, "unknown");
            }

            if (product.getIdentifier() == null
                    || product.getIdentifier().isBlank()) {
                throw new InvalidDataException(
                        "product identifier",
                        "Product identifier cannot be empty"
                );
            }
        }

        Set<String> extendedWarrantyIds =
                normalizeWarrantyProductIds(productIdsWithExtendedWarranty);

        Map<String, Integer> productQuantities =
                countProductQuantities(sale.getProducts());

        Map<String, Integer> accessoryQuantities =
                countAccessoryQuantities(sale.getProducts());

        List<Product> availableProducts = productService.listProducts();
        List<Accessory> availableAccessories =
                accessoryService.listAccessories();

        validateProductStock(productQuantities, availableProducts);
        validateAccessoryStock(accessoryQuantities, availableAccessories);

        applyBestPromotion(sale);
        assignWarranties(sale, extendedWarrantyIds);

        updateProductStock(productQuantities, availableProducts);
        updateAccessoryStock(accessoryQuantities, availableAccessories);

        List<Sale> sales = saleRepository.loadSales();
        sales.add(sale);
        saleRepository.saveSales(sales);
    }

    /**
     * Assigns the appropriate warranty to each console in the sale.
     *
     * @param sale sale associated with the warranties
     * @param extendedWarrantyIds identifiers requesting extended warranty
     */
    private void assignWarranties(
            Sale sale,
            Set<String> extendedWarrantyIds) {

        double warrantyAdditionalCost = 0.0;

        for (Product product : sale.getProducts()) {
            if (!(product instanceof Console)) {
                continue;
            }

            if (extendedWarrantyIds.contains(
                    product.getIdentifier().toLowerCase())) {

                double additionalCost = warrantyService
                        .assignExtendedWarranty(
                                product,
                                sale,
                                sale.getDate()
                        )
                        .getAdditionalCost();

                warrantyAdditionalCost += additionalCost;
            } else {
                warrantyService.assignBasicWarranty(
                        product,
                        sale,
                        sale.getDate()
                );
            }
        }

        sale.setWarrantyAdditionalCost(warrantyAdditionalCost);
    }

    /**
     * Normalizes product identifiers used for extended warranties.
     *
     * @param productIds product identifiers
     * @return normalized identifier set
     */
    private Set<String> normalizeWarrantyProductIds(
            List<String> productIds) {

        Set<String> normalizedIds = new HashSet<>();

        if (productIds == null) {
            return normalizedIds;
        }

        for (String productId : productIds) {
            if (productId != null && !productId.isBlank()) {
                normalizedIds.add(productId.trim().toLowerCase());
            }
        }

        return normalizedIds;
    }

    /**
     * Applies the active promotion that provides the highest monetary discount.
     *
     * @param sale sale to evaluate
     */
    private void applyBestPromotion(Sale sale) {
        Promotion promotion = promotionService.findBestPromotionFor(sale);

        if (promotion == null) {
            sale.setAppliedPromotionName(null);
            sale.setDiscountAmount(0.0);
            return;
        }

        sale.setAppliedPromotionName(promotion.getName());
        sale.setDiscountAmount(promotion.calculateDiscount(sale));
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
     * Finds a sale by its reference.
     *
     * @param reference sale reference, as returned by {@link Sale#getReference()}
     * @return the first sale with that reference, or null if none exists
     */
    public Sale findSaleByReference(String reference) {
        for (Sale sale : saleRepository.loadSales()) {
            if (sale.getReference().equals(reference)) {
                return sale;
            }
        }

        return null;
    }

    /**
     * Calculates the total of the sales registered in a month.
     *
     * <p>The caller validates the month and the year.</p>
     *
     * @param month month to calculate, from 1 to 12
     * @param year year to calculate
     * @return sum of the final totals of the sales of that month
     */
    public double calculateMonthlySales(int month, int year) {
        double salesTotal = 0.0;

        for (Sale sale : saleRepository.loadSales()) {
            if (sale.getDate().getMonthValue() == month
                    && sale.getDate().getYear() == year) {
                salesTotal += sale.calculateTotal();
            }
        }

        return salesTotal;
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

            ProductValidator.validateProductExists(
                    product,
                    entry.getKey()
            );

            ProductValidator.validateProductData(
                    product.getIdentifier(),
                    product.getTitle(),
                    product.getPrice(),
                    product.getAvailableQuantity()
            );

            ProductValidator.validateStockAvailability(
                    product,
                    entry.getValue()
            );
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

            ProductValidator.validateProductExists(
                    accessory,
                    entry.getKey()
            );

            ProductValidator.validateProductData(
                    accessory.getIdentifier(),
                    accessory.getTitle(),
                    accessory.getPrice(),
                    accessory.getAvailableQuantity()
            );

            ProductValidator.validateStockAvailability(
                    accessory,
                    entry.getValue()
            );
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

            ProductValidator.validateProductExists(
                    product,
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

            ProductValidator.validateProductExists(
                    accessory,
                    entry.getKey()
            );

            int remainingStock =
                    accessory.getAvailableQuantity() - entry.getValue();

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
     * @return matching product, or null when it does not exist
     */
    private Product findProductById(
            List<Product> products,
            String identifier) {

        for (Product product : products) {
            if (product.getIdentifier().equals(identifier)) {
                return product;
            }
        }

        return null;
    }

    /**
     * Finds an accessory by identifier.
     *
     * @param accessories accessories to search
     * @param identifier accessory identifier
     * @return matching accessory, or null when it does not exist
     */
    private Accessory findAccessoryById(
            List<Accessory> accessories,
            String identifier) {

        for (Accessory accessory : accessories) {
            if (accessory.getIdentifier().equals(identifier)) {
                return accessory;
            }
        }

        return null;
    }
}