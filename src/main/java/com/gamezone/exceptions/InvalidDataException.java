
package com.gamezone.exceptions;

/**
 * Indicates that input data does not meet the required format or range.
 */
public class InvalidDataException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception describing an invalid field.
     *
     * @param fieldName name of the invalid field
     * @param description explanation of the validation failure
     */
    public InvalidDataException(
            String fieldName,
            String description) {
        super(buildMessage(fieldName, description), "INVALID_DATA");
    }

    private static String buildMessage(
            String fieldName,
            String description) {
        String field = (fieldName == null || fieldName.isBlank())
                ? "dato"
                : fieldName.trim();
        String detail = (description == null || description.isBlank())
                ? "el valor no cumple los requisitos"
                : description.trim();
        return "Los datos del campo '" + field
                + "' no son válidos: " + detail + ".";
    }
}

