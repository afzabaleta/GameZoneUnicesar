package com.gamezone.persistence;

import java.time.LocalDate;

/**
 * Persistence-only record for a product return.
 */
public record ReturnRecord(
        String identifier,
        LocalDate returnDate,
        String saleReference,
        String productIds,
        String reason,
        double refundAmount,
        double warrantyRefundAmount) {
}
