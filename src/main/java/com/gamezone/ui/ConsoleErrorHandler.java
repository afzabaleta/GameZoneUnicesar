package com.gamezone.ui;

import com.gamezone.exceptions.GameZoneException;

/**
 * Shows in the console the message of each error of the domain.
 *
 * <p>Pure Fabrication: before this class, every menu operation repeated
 * the same four catch blocks. The messages are now written once, here, and
 * each operation only catches {@link GameZoneException}. The message is
 * chosen with the error code of the exception, so a new kind of error only
 * needs a change in this class.</p>
 */
public class ConsoleErrorHandler {

    private static final String BUSINESS_RULE_CODE =
            "BUSINESS_RULE_VIOLATION";

    private static final String PERSISTENCE_CODE =
            "PERSISTENCE_ERROR";

    /**
     * Prints the message that corresponds to a domain error.
     *
     * <p>Business rule errors are shown with a prefix, persistence errors
     * with a fixed message that hides technical details, and data and
     * not-found errors with their own message.</p>
     *
     * @param error error raised by a service, a repository or a validator
     */
    public void showError(GameZoneException error) {

        switch (error.getErrorCode()) {

            case BUSINESS_RULE_CODE:
                System.out.println(
                        "No se puede completar la operación: "
                                + error.getMessage()
                );
                break;

            case PERSISTENCE_CODE:
                System.out.println(
                        "Ocurrió un problema al acceder a los datos "
                                + "del sistema. Contacte al administrador."
                );
                break;

            default:
                System.out.println(error.getMessage());
        }
    }
}
