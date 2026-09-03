package com.gamezone.persistence;

import com.gamezone.model.Product;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ProductRepository {

    private final String filePath;

    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveProducts(List<Product> products) throws IOException {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(filePath))) {

            outputStream.writeObject(products);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Product> loadProducts() throws IOException, ClassNotFoundException {

        File file = new File(filePath);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(filePath))) {

            return (List<Product>) inputStream.readObject();
        }
    }
}