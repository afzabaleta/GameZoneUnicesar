package com.gamezone.model;

import java.time.LocalDate;

public class BasicWarranty extends Warranty {

    private int durationMonths;


    public BasicWarranty(String id,
                         Product product,
                         Sale sale,
                         LocalDate startDate,
                         int durationMonths) {

        super(id, product, sale, startDate);

        this.durationMonths = durationMonths;
    }


    public int getDurationMonths() {
        return durationMonths;
    }


    @Override
    public LocalDate getEndDate() {

        return getStartDate()
                .plusMonths(durationMonths);
    }
}
