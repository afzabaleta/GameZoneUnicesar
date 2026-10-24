package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.validation.SaleValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides business operations for registering and querying sales.
 *
 * <p>Sale data is delegated to SaleValidator and stock checks and updates
 * are delegated to InventoryService. The service coordinates inventory,
 * promotions, warranties and sale persistence.</p>
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final InventoryService inventoryService;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;

    /**
     * Creates a SaleService using the required collaborators.
     *
     * @param saleRepository repository used to persist and load sales
     * @param inventoryService service used to check and update the stock of
     *        products and accessories
     * @param promotionService service used to find applicable promotions
     * @param warrantyService service used to manage product warranties
     */
    public SaleService(
            SaleRepository saleRepository,
            InventoryService inventoryService,
            PromotionService promotionService,
            WarrantyService warrantyService) {

        if (saleRepository == null) {
            throw new InvalidDataException(
                    "saleRepository", "Sale repository cannot be null");
        }

        if (inventoryService == null) {
            throw new InvalidDataException(
                    "inventoryService", "Inventory service cannot be null");
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
        this.inventoryService = inventoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Creates a sale dated today and registers it with the unified
     * integration flow.
     *
     * <p>This service records the sales, so it is the creator of each
     * {@link Sale} (Creator); the user interface only collects the data.</p>
     *
     * @param customer customer who buys
     * @param seller seller who attends the sale
     * @param items products and accessories sold, one entry per unit
     * @param productIdsWithExtendedWarranty identifiers of consoles requesting
     *        extended warranty
     * @return the registered sale
     */
    public Sale registerSale(
            Customer customer,
            Seller seller,
            List<Product> items,
            List<String> productIdsWithExtendedWarranty) {

        Sale sale = new Sale(
                LocalDate.now(),
                customer,
                seller,
                items
        );

        registerSale(sale, productIdsWithExtendedWarranty);

        return sale;
    }

    /**
     * Registers a sale using the unified integration flow.
     *
     * @param sale sale to register
     * @param productIdsWithExtendedWarranty identifiers of consoles requesting
     *        extended warranty
     */
    private void registerSale(
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

        Set<String> extendedWarrantyIds =
                normalizeWarrantyProductIds(productIdsWithExtendedWarranty);

        inventoryService.validateStock(sale.getProducts());

        applyBestPromotion(sale);
        assignWarranties(sale, extendedWarrantyIds);

        inventoryService.decreaseStock(sale.getProducts());

        List<Sale> sales = saleRepository.loadSales();
        sales.add(sale);
        saleRepository.saveSales(sales);
    }

    /**
     * Assigns the appropriate warranty to each product of the sale that
     * has a warranty.
     *
     * @param sale sale associated with the warranties
     * @param extendedWarrantyIds identifiers requesting extended warranty
     */
    private void assignWarranties(
            Sale sale,
            Set<String> extendedWarrantyIds) {

        double warrantyAdditionalCost = 0.0;

        for (Product product : sale.getProducts()) {
            if (!product.isWarrantyEligible()) {
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
}
