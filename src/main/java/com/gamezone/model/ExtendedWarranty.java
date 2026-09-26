package com.gamezone.model;

import java.time.LocalDate;

public class ExtendedWarranty extends Warranty {

    private int extraMonths;


    public ExtendedWarranty(String id,
                            Product product,
                            Sale sale,
                            LocalDate startDate,
                            int extraMonths) {

        super(id, product, sale, startDate);

        this.extraMonths = extraMonths;
    }


    public int getExtraMonths() {
        return extraMonths;
    }


    @Override
    public LocalDate getEndDate() {

        return getStartDate()
                .plusMonths(extraMonths);
    }
}
