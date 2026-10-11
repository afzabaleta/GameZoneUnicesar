package com.gamezone.exceptions;

/**
 * Root exception for errors handled by the GameZone application.
 *
 * <p>The error code allows the application to identify the category of
 * a failure without relying on the displayed message.</p>
 */
public class GameZoneException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String errorCode;

    /**
     * Creates a GameZone exception with the default error code.
     *
     * @param message descriptive error message
     */
    public GameZoneException(String message) {
        this(message, "GAMEZONE_ERROR");
    }

    /**
     * Creates a GameZone exception with a specific error code.
     *
     * @param message descriptive error message
     * @param errorCode stable code identifying the error category
     */
    public GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = (errorCode == null || errorCode.isBlank())
                ? "GAMEZONE_ERROR"
                : errorCode.trim();
    }

    /**
     * Returns the code associated with this error.
     *
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}

