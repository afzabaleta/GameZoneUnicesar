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
    private final ProductService productService;

    /**
     * Creates a return service with its required dependencies.
     *
     * @param returnRepository repository used to persist returns
     * @param saleService service used to access sales
     * @param productService service used to access products and restore stock
     * @throws IllegalArgumentException if any dependency is null
     */
    public ReturnService(
            ReturnRepository returnRepository,
            SaleService saleService,
            ProductService productService) {

        if (returnRepository == null
                || saleService == null
                || productService == null) {
            throw new IllegalArgumentException(
                    "Return service dependencies cannot be null.");
        }

        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
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

        String identifier = "RET-" + System.currentTimeMillis();

        Return returnItem = new Return(
                identifier,
                LocalDate.now(),
                sale,
                returnedProducts,
                reason
        );

        for (Product product : returnedProducts) {
            productService.restoreStock(
                    product.getIdentifier(),
                    1
            );
        }

        List<Return> returns = returnRepository.loadAll();
        returns.add(returnItem);
        returnRepository.saveAll(returns);

        return returnItem;
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
                    .getDate()
                    .toString()
                    .equals(saleId)) {
                result.add(returnItem);
            }
        }

        return result;
    }

    /**
     * Calculates the monthly balance by subtracting return refunds
     * from the total sales for the specified month and year.
     *
     * @param month month to calculate, from 1 to 12
     * @param year year to calculate
     * @return monthly sales total minus return refunds
     * @throws IllegalArgumentException if the month or year is invalid
     */
    public double generateMonthlyBalance(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12.");
        }

        if (year < 1) {
            throw new IllegalArgumentException(
                    "Year must be positive.");
        }

        double salesTotal = 0.0;

        for (Sale sale : saleService.listSales()) {
            if (sale.getDate().getMonthValue() == month
                    && sale.getDate().getYear() == year) {
                salesTotal += sale.calculateTotal();
            }
        }

        double returnsTotal = 0.0;

        for (Return returnItem : returnRepository.loadAll()) {
            if (returnItem.getReturnDate().getMonthValue() == month
                    && returnItem.getReturnDate().getYear() == year) {
                returnsTotal += returnItem.getRefundAmount();
            }
        }

        return salesTotal - returnsTotal;
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
                        "Product identifier cannot be blank.");
            }
        }
    }

    private Sale findSale(String saleId) {
        for (Sale sale : saleService.listSales()) {
            if (sale.getDate().toString().equals(saleId)) {
                return sale;
            }
        }

        throw new IllegalArgumentException(
                "Venta no encontrada: " + saleId);
    }

    private Product findProduct(String productId) {
        for (Product product : productService.listProducts()) {
            if (product.getIdentifier().equals(productId)) {
                return product;
            }
        }

        throw new IllegalArgumentException(
                "Producto no encontrado: " + productId);
    }

    private boolean containsProduct(
            Sale sale,
            String productId) {

        return sale.getProducts()
                .stream()
                .anyMatch(product ->
                        product.getIdentifier().equals(productId));
    }
}