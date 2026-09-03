package com.gamezone.model;
import java.io.Serializable;

public abstract class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private double price;
    private int stock;

    public Product(String id, String title, double price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public abstract String getDescription();
}
