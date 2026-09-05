package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for persisting customers and sellers.
 */
public class PersonRepository {

    private static final String CUSTOMERS_FILE = "customers.dat";
    private static final String SELLERS_FILE = "sellers.dat";

    /**
     * Saves the list of customers to disk.
     *
     * @param customers customers to save
     */
    public void saveCustomers(List<Customer> customers) {
        saveList(CUSTOMERS_FILE, customers);
    }

    /**
     * Saves the list of sellers to disk.
     *
     * @param sellers sellers to save
     */
    public void saveSellers(List<Seller> sellers) {
        saveList(SELLERS_FILE, sellers);
    }

    /**
     * Loads customers from disk.
     *
     * @return stored customers, or an empty list if no file exists
     */
    public List<Customer> loadCustomers() {
        return loadList(CUSTOMERS_FILE);
    }

    /**
     * Loads sellers from disk.
     *
     * @return stored sellers, or an empty list if no file exists
     */
    public List<Seller> loadSellers() {
        return loadList(SELLERS_FILE);
    }

    /**
     * Saves a list to a file.
     *
     * @param fileName file where the list will be stored
     * @param list list to save
     */
    private <T> void saveList(String fileName, List<T> list) {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(fileName))) {

            outputStream.writeObject(list);

        } catch (IOException e) {
            throw new RuntimeException("Error saving data to " + fileName, e);
        }
    }

    /**
     * Loads a list from a file.
     *
     * @param fileName file containing the stored list
     * @return stored list or an empty list
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> loadList(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(file))) {

            return (List<T>) inputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading data from " + fileName, e);
        }
    }
}