package com.example.studentmanagement.mapper;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import org.springframework.stereotype.Component;

/**
 * Responsible for converting between the JPA entity and our DTOs.
 *
 * Why a dedicated mapper class instead of putting conversion logic inline
 * inside the service? Single Responsibility Principle — the service should
 * focus on business rules, not on shuffling fields between objects. It also
 * makes the conversion logic independently unit-testable and reusable.
 *
 * For a larger project you might replace this hand-written mapper with
 * MapStruct (compile-time generated mappers) — but understanding the manual
 * version first is important before reaching for a code-gen tool.
 */
@Component
public class StudentMapper {

    /**
     * Converts an incoming request DTO into a new Student entity.
     * Note: id and createdAt are intentionally NOT set here — id is
     * database-generated, createdAt is set by the entity's @PrePersist hook.
     */
    public Student toEntity(StudentRequestDto requestDto) {
        return Student.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .age(requestDto.getAge())
                .department(requestDto.getDepartment())
                .build();
    }

    /**
     * Converts a persisted Student entity into a response DTO safe to
     * return to API clients.
     */
    public StudentResponseDto toResponseDto(Student student) {
        return StudentResponseDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .age(student.getAge())
                .department(student.getDepartment())
                .createdAt(student.getCreatedAt())
                .build();
    }

    /**
     * Applies the fields from a request DTO onto an EXISTING managed entity.
     * Used during update operations, where we want to keep the same id and
     * createdAt but overwrite the mutable business fields.
     */
    public void updateEntityFromDto(StudentRequestDto requestDto, Student student) {
        student.setFirstName(requestDto.getFirstName());
        student.setLastName(requestDto.getLastName());
        student.setEmail(requestDto.getEmail());
        student.setAge(requestDto.getAge());
        student.setDepartment(requestDto.getDepartment());
    }
}
