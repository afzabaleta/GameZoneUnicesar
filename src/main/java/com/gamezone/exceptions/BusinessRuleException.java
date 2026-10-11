
package com.gamezone.exceptions;

/**
 * Indicates that an operation violates a GameZone business rule.
 */
public class BusinessRuleException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception describing the violated business rule.
     *
     * @param message description of the business rule violation
     */
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }
}

