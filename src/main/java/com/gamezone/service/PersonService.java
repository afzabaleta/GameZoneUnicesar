package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing customers and sellers.
 */
public class PersonService {

    private final PersonRepository personRepository;

    private List<Customer> customers;
    private List<Seller> sellers;

    /**
     * Creates a PersonService and loads the persisted data.
     */
    public PersonService() {
        this.personRepository = new PersonRepository();
        this.customers = new ArrayList<>(personRepository.loadCustomers());
        this.sellers = new ArrayList<>(personRepository.loadSellers());
    }

    /**
     * Registers a new customer.
     *
     * @param customer customer to register
     * @return true if the customer was registered
     */
    public boolean addCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }

        if (findCustomerByIdentification(customer.getIdentification()) != null) {
            return false;
        }

        customers.add(customer);
        personRepository.saveCustomers(customers);
        return true;
    }

    /**
     * Finds a customer by identification.
     *
     * @param identification customer's identification
     * @return the customer if found, otherwise null
     */
    public Customer findCustomerByIdentification(String identification) {
        for (Customer customer : customers) {
            if (customer.getIdentification().equals(identification)) {
                return customer;
            }
        }

        return null;
    }

    /**
     * Returns all registered customers.
     *
     * @return list of customers
     */
    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    /**
     * Returns all registered sellers.
     *
     * @return list of sellers
     */
    public List<Seller> getSellers() {
        return new ArrayList<>(sellers);
    }

    /**
     * Finds a seller by identification.
     *
     * @param identification seller's identification
     * @return the seller if found, otherwise null
     */
    public Seller findSellerByIdentification(String identification) {
        for (Seller seller : sellers) {
            if (seller.getIdentification().equals(identification)) {
                return seller;
            }
        }

        return null;
    }

    /**
     * Adds a seller to the existing seller collection.
     * Sellers are normally preloaded by the application.
     *
     * @param seller seller to add
     */
    public void addSeller(Seller seller) {
        if (seller != null &&
                findSellerByIdentification(seller.getIdentification()) == null) {
            sellers.add(seller);
            personRepository.saveSellers(sellers);
        }
    }
}