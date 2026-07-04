package com.example.studentmanagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * A consistent error response shape returned for EVERY error in the API.
 * Consistency here matters a lot for API consumers (e.g., your future
 * frontend, or a Postman collection, or another team's service) — they
 * should be able to parse errors the same way regardless of which endpoint
 * failed or why.
 */
@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Populated only for validation errors — maps field name to the
     * specific validation failure message. Omitted from JSON entirely
     * (via @JsonInclude) when null, keeping non-validation error
     * responses clean.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> fieldErrors;
}
