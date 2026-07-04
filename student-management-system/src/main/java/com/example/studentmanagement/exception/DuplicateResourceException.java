package com.example.studentmanagement.exception;

/**
 * Thrown when attempting to create a resource that violates a uniqueness
 * business rule (e.g., a student email that already exists).
 * Translated by GlobalExceptionHandler into a 409 Conflict response.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
