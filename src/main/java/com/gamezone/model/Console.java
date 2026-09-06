package gamezone.model;

/**
 * Represents a console product in the GameZone catalog.
 * Extends the common attributes and behavior from Product.
 */

public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    public Console(
            String identifier,
            String title,
            double price,
            int availableQuantity,
            String brand,
            String model,
            String generation) {

        super(identifier, title, price, availableQuantity);

        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    @Override
    public String getDescription() {
        return "Console: " + getTitle()
                + " Brand: " + brand
                + " Model: " + model
                + " Generation: " + generation;
    }

}
