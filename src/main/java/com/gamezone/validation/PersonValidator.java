
package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;

import java.util.List;

/** Validates person data and registration rules. */
public final class PersonValidator {

    private PersonValidator() {
        // Utility class; instances are not required.
    }

    /** Validates required fields and a basic email format. */
    public static void validatePersonData(
            String id, String name, String email, String phone) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("id", "el identificador no puede estar vacío");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("name", "el nombre no puede estar vacío");
        }
        if (phone == null || phone.isBlank()) {
            throw new InvalidDataException("phone", "el teléfono no puede estar vacío");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidDataException("email", "el correo no puede estar vacío");
        }

        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');
        if (atIndex <= 0 || dotIndex <= atIndex + 1
                || dotIndex >= email.length() - 1) {
            throw new InvalidDataException(
                    "email",
                    "el correo debe contener una arroba y un punto en posiciones válidas");
        }
    }

    /** Validates that a person was found. */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    /** Validates that the proposed identifier is unique. */
    public static void validateUniquePerson(List<Person> existing, String newId) {
        if (newId == null || newId.isBlank()) {
            throw new InvalidDataException("id", "el identificador no puede estar vacío");
        }
        if (existing == null) {
            throw new InvalidDataException(
                    "existing", "la lista de personas existentes no puede ser nula");
        }
        for (Person person : existing) {
            if (person != null && person.getIdentification() != null
                    && person.getIdentification().equalsIgnoreCase(newId.trim())) {
                throw new BusinessRuleException(
                        "Ya existe una persona con el identificador "
                                + newId.trim() + ".");
            }
        }
    }
}

