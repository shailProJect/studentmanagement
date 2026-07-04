package com.example.studentmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the shape of data a CLIENT sends us when creating or
 * updating a student (POST /students, PUT /students/{id}).
 *
 * Why a SEPARATE DTO from the response shape?
 * Because the client should never be able to set fields like "id" or
 * "createdAt" — those are server-controlled. If we used one DTO for both
 * directions, we'd either have to ignore extra fields silently (confusing)
 * or expose fields for the client to (mistakenly or maliciously) set.
 * Separating request/response DTOs keeps the API contract explicit and safe.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be a valid email address")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 120, message = "Age must be realistic")
    private Integer age;

    @NotBlank(message = "Department is required")
    private String department;
}
