package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing people in the GameZone system.
 *
 * <p>This service communicates with the person repository and provides
 * operations for registering and listing customers and sellers.</p>
 */
public class PersonService {

    private final PersonRepository personRepository;
    private final List<Customer> customers;
    private final List<Seller> sellers;

    /**
     * Creates a person service using the specified repository.
     *
     * @param personRepository repository used to persist and load people
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.customers = new ArrayList<>(personRepository.loadCustomers());
        this.sellers = new ArrayList<>(personRepository.loadSellers());
    }

    /**
     * Registers a new customer and persists the updated customer list.
     *
     * @param customer customer to register
     * @throws IllegalArgumentException if the customer is null
     */
    public void registerCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        customers.add(customer);
        personRepository.saveCustomers(customers);
    }

    /**
     * Returns all registered customers.
     *
     * @return list of registered customers
     */
    public List<Customer> listCustomers() {
        return new ArrayList<>(customers);
    }

    /**
     * Returns all registered sellers.
     *
     * @return list of registered sellers
     */
    public List<Seller> listSellers() {
        return new ArrayList<>(sellers);
    }
}