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
     * @param returnedProducts products being returned
     * @param reason reason for return
     */
    public Return(String identifier,
                  LocalDate returnDate,
                  Sale originalSale,
                  List<Product> returnedProducts,
                  String reason) {

        this.identifier = identifier;
        this.returnDate = returnDate;
        this.originalSale = originalSale;
        this.returnedProducts = new ArrayList<>(returnedProducts);
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }


    /**
     * Returns the return identifier.
     *
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }


    /**
     * Returns the return date.
     *
     * @return return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }


    /**
     * Returns the original sale.
     *
     * @return original sale
     */
    public Sale getOriginalSale() {
        return originalSale;
    }


    /**
     * Returns returned products.
     *
     * @return copy of returned products
     */
    public List<Product> getReturnedProducts() {
        return new ArrayList<>(returnedProducts);
    }


    /**
     * Returns the reason of the return.
     *
     * @return reason
     */
    public String getReason() {
        return reason;
    }


    /**
     * Returns refund amount.
     *
     * @return refund amount
     */
    public double getRefundAmount() {
        return refundAmount;
    }


    /**
     * Calculates the refund amount from returned products.
     *
     * @return total refund amount
     */
    private double calculateRefundAmount() {

        double total = 0;

        for (Product product : returnedProducts) {
            total += product.getPrice();
        }

        return total;
    }
}
