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
    private double refundAmount;

    /**
     * Creates a return.
     *
     * @param identifier return identifier
     * @param returnDate return date
     * @param originalSale original sale
     * @param returnedProducts returned products
     * @param reason return reason
     */
    public Return(
            String identifier,
            LocalDate returnDate,
            Sale originalSale,
            List<Product> returnedProducts,
            String reason) {

        validateIdentifier(identifier);
        validateReturnDate(returnDate);
        validateSale(originalSale);
        validateProducts(returnedProducts);
        validateReason(reason);

        this.identifier = identifier;
        this.returnDate = returnDate;
        this.originalSale = originalSale;
        this.returnedProducts = new ArrayList<>(returnedProducts);
        this.reason = reason;
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
     * Returns the reason for this return.
     *
     * @return return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the refund amount.
     *
     * @return refund amount
     */
    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Calculates the refund amount based on the returned products.
     *
     * @return refund amount
     */
    public double calculateRefundAmount() {
        double total = 0;

        for (Product product : returnedProducts) {
            total += product.getPrice();
        }

        this.refundAmount = total;

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

        for (Product product : returnedProducts) {
            receipt.append("- ")
                    .append(product.getTitle())
                    .append(" - $")
                    .append(product.getPrice())
                    .append("\n");
        }

        receipt.append("\nValor devolución: $")
                .append(refundAmount)
                .append("\n");

        receipt.append("==============================");

        return receipt.toString();
    }
}