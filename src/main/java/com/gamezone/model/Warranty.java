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
}