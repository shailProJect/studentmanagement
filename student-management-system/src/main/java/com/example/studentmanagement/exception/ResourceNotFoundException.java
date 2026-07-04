package com.example.studentmanagement.exception;

/**
 * Thrown when a requested resource (e.g., a Student by id) does not exist.
 * Caught centrally by GlobalExceptionHandler and translated into a 404 response.
 *
 * Using a custom exception instead of returning null or throwing a generic
 * RuntimeException lets the exception handler distinguish "not found" from
 * other error types and respond with the correct HTTP status code.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
