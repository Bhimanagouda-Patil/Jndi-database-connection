package com.unisys.errors;

/**
 * Custom exception class for errors occurring in the UserService layer.
 * Encapsulates specific error messages and underlying exceptions.
 */
public class UserServiceException extends RuntimeException {

    /**
     * Constructs a new UserServiceException with the specified detail message.
     * 
     * @param message The detail message.
     */
    public UserServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserServiceException with the specified detail message
     * and cause.
     * 
     * @param message The detail message.
     * @param cause   The cause of the exception (a throwable that caused this exception).
     */
    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
