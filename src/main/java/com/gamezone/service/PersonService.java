package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Customer;
import com.gamezone.model.Person;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.validation.PersonValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing people in the GameZone system.
 *
 * Person data and uniqueness checks are delegated to PersonValidator.
 * Persistence errors are handled by PersonRepository.
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
            throw new InvalidDataException(
                    "personRepository",
                    "es obligatorio."
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
     * Registers a new customer after validating the data
     * and identification.
     *
     * @param customer customer to register
     */
    public void registerCustomer(Customer customer) {
        if (customer == null) {
            throw new InvalidDataException(
                    "customer",
                    "es obligatorio."
            );
        }

        PersonValidator.validatePersonData(
                customer.getIdentification(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );

        List<Person> existingPeople = new ArrayList<>();
        existingPeople.addAll(customers);
        existingPeople.addAll(sellers);

        PersonValidator.validateUniquePerson(
                existingPeople,
                customer.getIdentification()
        );

        List<Customer> updatedCustomers =
                new ArrayList<>(customers);

        updatedCustomers.add(customer);

        personRepository.saveCustomers(updatedCustomers);

        customers.add(customer);
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
     * Finds a registered customer by identification.
     *
     * @param identification customer identification
     * @return matching customer, or null when it does not exist
     */
    public Customer findCustomerById(String identification) {
        for (Customer customer : customers) {
            if (customer.getIdentification().equals(identification)) {
                return customer;
            }
        }

        return null;
    }

    /**
     * Finds a registered seller by identification.
     *
     * @param identification seller identification
     * @return matching seller, or null when it does not exist
     */
    public Seller findSellerById(String identification) {
        for (Seller seller : sellers) {
            if (seller.getIdentification().equals(identification)) {
                return seller;
            }
        }

        return null;
    }
}
