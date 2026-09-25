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
    private String status;


    /**
     * Creates a return.
     *
     * @param identifier return identifier
     * @param returnDate return date
     * @param originalSale original sale
     * @param returnedProducts returned products
     * @param reason return reason
     */
    public Return(String identifier,
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
        this.status = "PENDING";
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


    public String getIdentifier() {
        return identifier;
    }


    public LocalDate getReturnDate() {
        return returnDate;
    }


    public Sale getOriginalSale() {
        return originalSale;
    }


    public List<Product> getReturnedProducts() {
        return new ArrayList<>(returnedProducts);
    }


    public String getReason() {
        return reason;
    }


    public double getRefundAmount() {
        return refundAmount;
    }


    /**
     * Returns current status.
     *
     * @return return status
     */
    public String getStatus() {
        return status;
    }


    /**
     * Updates return status.
     *
     * @param status new status
     */
    public void setStatus(String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Return status cannot be blank.");
        }

        this.status = status;
    }


    /**
     * Calculates refund amount.
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
     * Generates return receipt.
     *
     * @return receipt information
     */
    public String generateReceipt() {

        StringBuilder receipt = new StringBuilder();

        receipt.append("RETURN RECEIPT\n");
        receipt.append("-------------------------\n");
        receipt.append("Identifier: ")
                .append(identifier)
                .append("\n");

        receipt.append("Date: ")
                .append(returnDate)
                .append("\n");

        receipt.append("Reason: ")
                .append(reason)
                .append("\n");

        receipt.append("Status: ")
                .append(status)
                .append("\n");

        receipt.append("Products returned: ")
                .append(returnedProducts.size())
                .append("\n");

        receipt.append("Refund amount: $")
                .append(refundAmount)
                .append("\n");

        return receipt.toString();
    }
}