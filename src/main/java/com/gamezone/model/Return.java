package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product return associated with a sale.
 */
public class Return {

    private String identifier;
    private LocalDate returnDate;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double warrantyRefundAmount;
    private double refundAmount;

    /**
     * Creates a return without a warranty refund.
     *
     * @param identifier return identifier
     * @param returnDate return date
     * @param originalSale original sale
     * @param returnedProducts returned products
     * @param reason reason for the return
     */
    public Return(
            String identifier,
            LocalDate returnDate,
            Sale originalSale,
            List<Product> returnedProducts,
            String reason) {

        this(
                identifier,
                returnDate,
                originalSale,
                returnedProducts,
                reason,
                0.0
        );
    }

    /**
     * Creates a return including a refundable warranty amount.
     *
     * @param identifier return identifier
     * @param returnDate return date
     * @param originalSale original sale
     * @param returnedProducts returned products
     * @param reason reason for the return
     * @param warrantyRefundAmount refundable warranty amount
     */
    public Return(
            String identifier,
            LocalDate returnDate,
            Sale originalSale,
            List<Product> returnedProducts,
            String reason,
            double warrantyRefundAmount) {

        validateIdentifier(identifier);
        validateReturnDate(returnDate);
        validateSale(originalSale);
        validateProducts(returnedProducts);
        validateReason(reason);
        validateWarrantyRefundAmount(warrantyRefundAmount);

        this.identifier = identifier;
        this.returnDate = returnDate;
        this.originalSale = originalSale;
        this.returnedProducts = new ArrayList<>(returnedProducts);
        this.reason = reason;
        this.warrantyRefundAmount = warrantyRefundAmount;
        this.refundAmount = calculateRefundAmount();
    }

    private void validateIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Return identifier cannot be blank.");
        }
    }

    private void validateReturnDate(LocalDate returnDate) {
        if (returnDate == null) {
            throw new IllegalArgumentException(
                    "Return date cannot be null.");
        }
    }

    private void validateSale(Sale originalSale) {
        if (originalSale == null) {
            throw new IllegalArgumentException(
                    "Original sale cannot be null.");
        }
    }

    private void validateProducts(List<Product> returnedProducts) {
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Returned products cannot be empty.");
        }
    }

    private void validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Return reason cannot be blank.");
        }
    }

    private void validateWarrantyRefundAmount(
            double warrantyRefundAmount) {

        if (Double.isNaN(warrantyRefundAmount)
                || Double.isInfinite(warrantyRefundAmount)
                || warrantyRefundAmount < 0) {

            throw new IllegalArgumentException(
                    "Warranty refund amount cannot be negative."
            );
        }
    }

    /**
     * Returns the identifier of this return.
     *
     * @return return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the date of this return.
     *
     * @return return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Returns the original sale associated with this return.
     *
     * @return original sale
     */
    public Sale getOriginalSale() {
        return originalSale;
    }

    /**
     * Returns the products included in this return.
     *
     * @return returned products
     */
    public List<Product> getReturnedProducts() {
        return new ArrayList<>(returnedProducts);
    }

    /**
     * Returns the reason for the return.
     *
     * @return return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the refundable warranty amount.
     *
     * @return warranty refund amount
     */
    public double getWarrantyRefundAmount() {
        return warrantyRefundAmount;
    }

    /**
     * Returns the refund amount including the refundable
     * warranty cost.
     *
     * @return total refund amount
     */
    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Calculates the refund amount applying the proportional
     * share of the original sale discount and adding the
     * refundable warranty amount.
     *
     * @return refund amount after the proportional discount
     * and warranty refund
     */
    public double calculateRefundAmount() {

        double returnedSubtotal = 0.0;

        for (Product product : returnedProducts) {
            returnedSubtotal += product.getPrice();
        }

        double saleSubtotal = originalSale.calculateSubtotal();
        double saleDiscount = originalSale.getDiscountAmount();

        double productRefund;

        if (saleSubtotal <= 0 || saleDiscount <= 0) {

            productRefund = returnedSubtotal;

        } else {

            double proportionalDiscount =
                    saleDiscount
                            * (returnedSubtotal / saleSubtotal);

            productRefund =
                    Math.max(
                            0.0,
                            returnedSubtotal - proportionalDiscount
                    );
        }

        this.refundAmount =
                productRefund + warrantyRefundAmount;

        return refundAmount;
    }

    /**
     * Generates a receipt containing the return information.
     *
     * @return return receipt information
     */
    public String generateReturnReceipt() {

        StringBuilder receipt = new StringBuilder();

        receipt.append("===== RECIBO DE DEVOLUCIÓN =====\n");
        receipt.append("-------------------------------\n");

        receipt.append("Identificador: ")
                .append(identifier)
                .append("\n");

        receipt.append("Fecha devolución: ")
                .append(returnDate)
                .append("\n");

        receipt.append("Motivo: ")
                .append(reason)
                .append("\n");

        receipt.append("Venta original: ")
                .append(originalSale)
                .append("\n\n");

        receipt.append("Productos devueltos:\n");

        double saleSubtotal = originalSale.calculateSubtotal();
        double saleDiscount = originalSale.getDiscountAmount();

        double discountRate = 0.0;

        if (saleSubtotal > 0) {
            discountRate = saleDiscount / saleSubtotal;
        }

        for (Product product : returnedProducts) {

            double listPrice = product.getPrice();

            double proportionalDiscount =
                    listPrice * discountRate;

            double itemRefund =
                    listPrice - proportionalDiscount;

            receipt.append("- ")
                    .append(product.getTitle())
                    .append("\n");

            receipt.append("  Precio de lista: $")
                    .append(listPrice)
                    .append("\n");

            receipt.append("  Descuento proporcional: $")
                    .append(proportionalDiscount)
                    .append("\n");

            receipt.append("  Monto reembolsado: $")
                    .append(itemRefund)
                    .append("\n");
        }

        receipt.append("\nReembolso de garantía: $")
                .append(warrantyRefundAmount)
                .append("\n");

        receipt.append("Valor devolución: $")
                .append(refundAmount)
                .append("\n");

        receipt.append("==============================");

        return receipt.toString();
    }
}