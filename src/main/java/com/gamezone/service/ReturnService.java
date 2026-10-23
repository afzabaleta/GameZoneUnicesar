
package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRecord;
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
                    "Return service dependencies cannot be null.");
        }

        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.inventoryService = inventoryService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a product return associated with an existing sale.
     */
    public Return registerReturn(
            String saleId,
            List<String> productIds,
            String reason) {

        if (saleId == null || saleId.isBlank()) {
            throw new IllegalArgumentException(
                    "Sale identifier cannot be blank.");
        }

        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one product must be returned.");
        }

        validateProductIds(productIds);

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Return reason cannot be blank.");
        }

        Sale sale = findSale(saleId);

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException(
                    "La venta supera el plazo de 30 dias.");
        }

        List<Product> returnedProducts = new ArrayList<>();

        for (String productId : productIds) {
            Product product = findProduct(productId);

            if (!containsProduct(sale, productId)) {
                throw new IllegalArgumentException(
                        "El producto no pertenece a la venta indicada: "
                                + productId);
            }

            returnedProducts.add(product);
        }

        double warrantyRefundAmount =
                cancelReturnedConsoleWarranties(saleId, returnedProducts);

        Return returnItem = new Return(
                "RET-" + System.currentTimeMillis(),
                LocalDate.now(),
                sale,
                returnedProducts,
                reason,
                warrantyRefundAmount);

        restoreReturnedStock(returnedProducts);

        List<ReturnRecord> records = returnRepository.loadAll();
        records.add(toRecord(returnItem));
        returnRepository.saveAll(records);

        return returnItem;
    }

    private double cancelReturnedConsoleWarranties(
            String saleId,
            List<Product> returnedProducts) {

        double warrantyRefundAmount = 0.0;

        for (Product product : returnedProducts) {
            if (product.isWarrantyEligible()) {
                warrantyRefundAmount += warrantyService.cancelWarranties(
                        product.getIdentifier(),
                        saleId);
            }
        }

        return warrantyRefundAmount;
    }

    private void restoreReturnedStock(List<Product> returnedProducts) {
        for (Product product : returnedProducts) {
            inventoryService.restoreStock(product, 1);
        }
    }

    /**
     * Returns all persisted returns reconstructed as domain objects.
     */
    public List<Return> viewAllReturns() {
        return loadReturns();
    }

    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> result = new ArrayList<>();

        for (Return returnItem : loadReturns()) {
            if (returnItem.getOriginalSale()
                    .getCustomer()
                    .getIdentification()
                    .equals(customerId)) {
                result.add(returnItem);
            }
        }

        return result;
    }

    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();

        for (Return returnItem : loadReturns()) {
            if (returnItem.getOriginalSale()
                    .getReference()
                    .equals(saleId)) {
                result.add(returnItem);
            }
        }

        return result;
    }

    /**
     * Calculates total sales for a specific month and year.
     */
    public double calculateMonthlySales(int month, int year) {
        validateMonthAndYear(month, year);
        return saleService.calculateMonthlySales(month, year);
    }

    /**
     * Calculates the total refunded amount for a specific month and year.
     */
    public double calculateMonthlyReturns(int month, int year) {
        validateMonthAndYear(month, year);

        double returnsTotal = 0.0;

        for (Return returnItem : loadReturns()) {
            if (returnItem.getReturnDate().getMonthValue() == month
                    && returnItem.getReturnDate().getYear() == year) {
                returnsTotal += returnItem.getRefundAmount();
            }
        }

        return returnsTotal;
    }

    /**
     * Calculates the monthly net balance.
     */
    public double generateMonthlyBalance(int month, int year) {
        double salesTotal = calculateMonthlySales(month, year);
        double returnsTotal = calculateMonthlyReturns(month, year);
        return salesTotal - returnsTotal;
    }

    private void validateMonthAndYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12.");
        }

        if (year < 1) {
            throw new IllegalArgumentException(
                    "Year must be positive.");
        }
    }

    private void validateProductIds(List<String> productIds) {
        for (String productId : productIds) {
            if (productId == null || productId.isBlank()) {
                throw new IllegalArgumentException(
                        "Product identifier cannot be blank.");
            }
        }
    }

    /**
     * Resolves the original sale through SaleService.
     */
    private Sale findSale(String saleId) {
        Sale sale = saleService.findSaleByReference(saleId);

        if (sale == null) {
            throw new IllegalArgumentException(
                    "Venta no encontrada: " + saleId);
        }

        return sale;
    }

    /**
     * Resolves products and accessories through InventoryService.
     */
    private Product findProduct(String productId) {
        Product product = inventoryService.findItem(productId);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Producto o accesorio no encontrado: " + productId);
        }

        return product;
    }

    private boolean containsProduct(Sale sale, String productId) {
        return sale.getProducts()
                .stream()
                .anyMatch(product ->
                        product.getIdentifier().equals(productId));
    }

    /**
     * Converts a domain return into persistence-only data.
     */
    private ReturnRecord toRecord(Return returnItem) {
        String productIds = returnItem.getReturnedProducts()
                .stream()
                .map(Product::getIdentifier)
                .reduce((first, second) -> first + "," + second)
                .orElse("");

        return new ReturnRecord(
                returnItem.getIdentifier(),
                returnItem.getReturnDate(),
                returnItem.getOriginalSale().getReference(),
                productIds,
                returnItem.getReason(),
                returnItem.getRefundAmount(),
                returnItem.getWarrantyRefundAmount());
    }

    /**
     * Reconstructs a domain return by resolving its identifiers.
     */
    private Return fromRecord(ReturnRecord record) {
        Sale sale = findSale(record.saleReference());
        List<Product> products = new ArrayList<>();

        if (!record.productIds().isBlank()) {
            for (String productId : record.productIds().split(",")) {
                products.add(findProduct(productId.trim()));
            }
        }

        return new Return(
                record.identifier(),
                record.returnDate(),
                sale,
                products,
                record.reason(),
                record.warrantyRefundAmount());
    }

    private List<Return> loadReturns() {
        List<Return> returns = new ArrayList<>();

        for (ReturnRecord record : returnRepository.loadAll()) {
            returns.add(fromRecord(record));
        }

        return returns;
    }
}
