package com.gamezone.model;

import java.time.LocalDate;

public abstract class Warranty {

    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;


    public Warranty(String id,
                    Product product,
                    Sale sale,
                    LocalDate startDate) {

        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }


    public String getId() {
        return id;
    }


    public Product getProduct() {
        return product;
    }


    public Sale getSale() {
        return sale;
    }


    public LocalDate getStartDate() {
        return startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }


    public abstract int getDurationInMonths();


    public abstract String getWarrantyType();


    public abstract double getAdditionalCost();


    public boolean isActive(LocalDate date) {

        if (date == null) {
            return false;
        }

        return !date.isBefore(startDate)
                && !date.isAfter(endDate);
    }


    public String generateWarrantyCertificate() {

        return String.format(
                "Certificado de %s | ID: %s | Producto: %s | Fecha inicio: %s | Fecha vencimiento: %s | Costo adicional: $%.2f",
                getWarrantyType(),
                id,
                product != null ? product.getDescription() : "N/A",
                startDate,
                endDate,
                getAdditionalCost()
        );
    }
}