
package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides business operations for registering and consulting product returns.
 */
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final InventoryService inventoryService;
    private final WarrantyService warrantyService;

    /**
     * Creates a return service with its required dependencies.
     *
     * @param returnRepository repository used to persist returns
     * @param saleService service used to access sales
     * @param inventoryService service used to find items and restore stock
     * @param warrantyService service used to cancel warranties
     * @throws IllegalArgumentException if any dependency is null
     */
    public ReturnService(
            ReturnRepository returnRepository,
            SaleService saleService,
            InventoryService inventoryService,
            WarrantyService warrantyService) {

        if (returnRepository == null
                || saleService == null
                || inventoryService == null
                || warrantyService == null) {

            throw new IllegalArgumentException(
                    "Return service dependencies cannot be null."
            );
        }

        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.inventoryService = inventoryService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a product return associated with an existing sale.
     *
     * @param saleId identifier of the sale
     * @param productIds identifiers of the products being returned
     * @param reason reason for the return
     * @return the registered return
     * @throws IllegalArgumentException if the sale, products, or reason are
     * invalid, if the return period has expired, or if a product does not
     * belong to the sale
     */
    public Return registerReturn(
            String saleId,
            List<String> productIds,
            String reason) {

        if (saleId == null || saleId.isBlank()) {
            throw new IllegalArgumentException(
                    "Sale identifier cannot be blank."
            );
        }

        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one product must be returned."
            );
        }

        validateProductIds(productIds);

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Return reason cannot be blank."
            );
        }

        Sale sale = findSale(saleId);

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException(
                    "La venta supera el plazo de 30 dias."
            );
        }

        List<Product> returnedProducts = new ArrayList<>();

        for (String productId : productIds) {
            Product product = findProduct(productId);

            if (!containsProduct(sale, productId)) {
                throw new IllegalArgumentException(
                        "El producto no pertenece a la venta indicada: "
                                + productId
                );
            }

            returnedProducts.add(product);
        }

        double warrantyRefundAmount =
                cancelReturnedConsoleWarranties(
                        saleId,
                        returnedProducts
                );

        String identifier = "RET-" + System.currentTimeMillis();

        Return returnItem = new Return(
                identifier,
                LocalDate.now(),
                sale,
                returnedProducts,
                reason,
                warrantyRefundAmount
        );

        restoreReturnedStock(returnedProducts);

        List<Return> returns = returnRepository.loadAll();
        returns.add(returnItem);
        returnRepository.saveAll(returns);

        return returnItem;
    }

    /**
     * Cancels warranties for every returned product eligible for warranty.
     *
     * @param saleId identifier of the original sale
     * @param returnedProducts returned products
     * @return total refundable warranty cost
     */
    private double cancelReturnedConsoleWarranties(
            String saleId,
            List<Product> returnedProducts) {

        double warrantyRefundAmount = 0.0;

        for (Product product : returnedProducts) {
            if (product.isWarrantyEligible()) {
                warrantyRefundAmount +=
                        warrantyService.cancelWarranties(
                                product.getIdentifier(),
                                saleId
                        );
            }
        }

        return warrantyRefundAmount;
    }

    /**
     * Restores stock for every returned item through InventoryService.
     *
     * @param returnedProducts products and accessories being returned
     */
    private void restoreReturnedStock(
            List<Product> returnedProducts) {

        for (Product product : returnedProducts) {
            inventoryService.restoreStock(product, 1);
        }
    }

    /**
     * Returns all persisted returns.
     *
     * @return list containing all registered returns
     */
    public List<Return> viewAllReturns() {
        return returnRepository.loadAll();
    }

    /**
     * Returns all returns associated with a customer.
     *
     * @param customerId customer identification
     * @return list of returns associated with the customer
     */
    public List<Return> viewReturnsByCustomer(String customerId) {

        List<Return> result = new ArrayList<>();

        for (Return returnItem : returnRepository.loadAll()) {
            if (returnItem.getOriginalSale()
                    .getCustomer()
                    .getIdentification()
                    .equals(customerId)) {

                result.add(returnItem);
            }
        }

        return result;
    }

    /**
     * Returns all returns associated with a sale.
     *
     * @param saleId identifier of the sale
     * @return list of returns associated with the sale
     */
    public List<Return> viewReturnsBySale(String saleId) {

        List<Return> result = new ArrayList<>();

        for (Return returnItem : returnRepository.loadAll()) {
            if (returnItem.getOriginalSale()
                    .getReference()
                    .equals(saleId)) {

                result.add(returnItem);
            }
        }

        return result;
    }

    /**
     * Calculates the total sales for a specific month and year.
     *
     * @param month month to calculate, from 1 to 12
     * @param year year to calculate
     * @return total final sales for the month
     */
    public double calculateMonthlySales(int month, int year) {
        validateMonthAndYear(month, year);
        return saleService.calculateMonthlySales(month, year);
    }

    /**
     * Calculates the total refunded amount for a specific month and year.
     *
     * @param month month to calculate, from 1 to 12
     * @param year year to calculate
     * @return total returned amount for the month
     */
    public double calculateMonthlyReturns(int month, int year) {

        validateMonthAndYear(month, year);

        double returnsTotal = 0.0;

        for (Return returnItem : returnRepository.loadAll()) {
            if (returnItem.getReturnDate().getMonthValue() == month
                    && returnItem.getReturnDate().getYear() == year) {

                returnsTotal += returnItem.getRefundAmount();
            }
        }

        return returnsTotal;
    }

    /**
     * Calculates the monthly net balance.
     *
     * @param month month to calculate, from 1 to 12
     * @param year year to calculate
     * @return total sales minus total refunds
     */
    public double generateMonthlyBalance(int month, int year) {

        double salesTotal = calculateMonthlySales(month, year);
        double returnsTotal = calculateMonthlyReturns(month, year);

        return salesTotal - returnsTotal;
    }

    /**
     * Validates the month and year used by monthly reports.
     *
     * @param month month to validate
     * @param year year to validate
     */
    private void validateMonthAndYear(int month, int year) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12."
            );
        }

        if (year < 1) {
            throw new IllegalArgumentException(
                    "Year must be positive."
            );
        }
    }

    /**
     * Validates that all product identifiers are present and non-blank.
     *
     * @param productIds product identifiers to validate
     * @throws IllegalArgumentException if any identifier is null or blank
     */
    private void validateProductIds(List<String> productIds) {

        for (String productId : productIds) {
            if (productId == null || productId.isBlank()) {
                throw new IllegalArgumentException(
                        "Product identifier cannot be blank."
                );
            }
        }
    }

    /**
     * Finds a sale by its identifier.
     *
     * @param saleId sale identifier
     * @return matching sale
     */
    private Sale findSale(String saleId) {

        for (Sale sale : saleService.listSales()) {
            if (sale.getReference().equals(saleId)) {
                return sale;
            }
        }

        throw new IllegalArgumentException(
                "Venta no encontrada: " + saleId
        );
    }

    /**
     * Finds a product or accessory through InventoryService.
     *
     * @param productId product or accessory identifier
     * @return matching product or accessory
     */
    private Product findProduct(String productId) {

        Product product = inventoryService.findItem(productId);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Producto o accesorio no encontrado: " + productId
            );
        }

        return product;
    }

    /**
     * Checks whether the sale contains the specified item.
     *
     * @param sale sale to inspect
     * @param productId item identifier
     * @return true if the item belongs to the sale
     */
    private boolean containsProduct(
            Sale sale,
            String productId) {

        return sale.getProducts()
                .stream()
                .anyMatch(product ->
                        product.getIdentifier().equals(productId)
                );
    }
}
