
package com.gamezone.exceptions;

/**
 * Indicates a failure while reading or writing persistent application data.
 */
public class PersistenceException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception and retains the original persistence failure.
     *
     * @param message contextual description of the failed operation
     * @param cause original exception that caused the failure
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, "PERSISTENCE_ERROR");
        if (cause != null) {
            initCause(cause);
        }
    }
}
