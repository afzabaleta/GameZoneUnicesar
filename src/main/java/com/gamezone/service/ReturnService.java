package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnService {

    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final ProductService productService;

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

    public List<Return> viewAllReturns() {
        return returnRepository.loadAll();
    }

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
}