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
        if (personRepository == null) {
            throw new IllegalArgumentException(
                    "Person repository cannot be null"
            );
        }

        this.personRepository = personRepository;
        this.customers = new ArrayList<>(
                personRepository.loadCustomers()
        );
        this.sellers = new ArrayList<>(
                personRepository.loadSellers()
        );
    }

    /**
     * Registers a new customer and persists the updated customer list.
     *
     * @param customer customer to register
     * @throws IllegalArgumentException if the customer is null or has invalid data
     */
    public void registerCustomer(Customer customer) {
        validateCustomer(customer);

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

    /**
     * Validates the information of a customer before registration.
     *
     * @param customer customer to validate
     * @throws IllegalArgumentException if any required customer data is invalid
     */
    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer name cannot be blank"
            );
        }

        if (customer.getIdentification() == null
                || customer.getIdentification().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer identification cannot be blank"
            );
        }

        if (customer.getPhone() == null || customer.getPhone().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer phone cannot be blank"
            );
        }

        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer email cannot be blank"
            );
        }

        if (!customer.getEmail().contains("@")) {
            throw new IllegalArgumentException(
                    "Customer email must contain @"
            );
        }
    }
}