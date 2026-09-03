package com.gamezone.model;

public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    public VideoGame(String id, String title, double price, int stock,
                     String platform, String genre, String ageRating) {

        super(id, title, price, stock);

        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    @Override
    public String getDescription() {
        return "Video Game: " + getTitle()
                + " | Platform: " + platform
                + " | Genre: " + genre
                + " | Age Rating: " + ageRating
                + " | Price: " + getPrice()
                + " | Stock: " + getStock();
    }
}
