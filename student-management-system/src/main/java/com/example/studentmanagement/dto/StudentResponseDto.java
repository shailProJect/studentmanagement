package com.example.studentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO representing the shape of data we send BACK to the client.
 * Includes server-generated fields (id, createdAt) that the client
 * is never allowed to set directly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private String department;
    private LocalDateTime createdAt;
}
