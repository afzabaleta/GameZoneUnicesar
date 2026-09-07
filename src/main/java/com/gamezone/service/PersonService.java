package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing people in the GameZone system.
 *
 * <p>This service communicates with the person repository and provides
 * operations related to customer registration.</p>
 */
public class PersonService {

    private final PersonRepository personRepository;
    private final List<Customer> customers;

    /**
     * Creates a person service using the specified repository.
     *
     * @param personRepository repository used to persist people
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.customers = new ArrayList<>(personRepository.loadCustomers());
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
}