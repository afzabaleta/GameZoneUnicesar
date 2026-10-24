
package com.gamezone.exceptions;

/**
 * Indicates that a requested resource does not exist.
 */
public class ResourceNotFoundException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception for a resource that could not be found.
     *
     * @param resourceType type of the missing resource, such as product
     * @param identifier identifier used to search for the resource
     */
    public ResourceNotFoundException(
            String resourceType,
            String identifier) {
        super(buildMessage(resourceType, identifier),
                "RESOURCE_NOT_FOUND");
    }

    private static String buildMessage(
            String resourceType,
            String identifier) {
        String type = (resourceType == null || resourceType.isBlank())
                ? "recurso"
                : resourceType.trim();
        String id = (identifier == null || identifier.isBlank())
                ? "no indicado"
                : identifier.trim();
        return "No se encontró el recurso solicitado: " + type
                + " con identificador " + id + ".";
    }
}

