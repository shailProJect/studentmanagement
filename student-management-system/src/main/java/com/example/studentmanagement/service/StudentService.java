package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;

import java.util.List;

/**
 * Business-logic contract for student operations.
 *
 * Why an interface at all, if there's only one implementation?
 * 1. It decouples the controller from the concrete implementation —
 *    the controller depends on this abstraction, not on StudentServiceImpl.
 * 2. It makes unit testing the controller trivial (mock the interface).
 * 3. It leaves room to introduce alternate implementations later
 *    (e.g., a caching decorator, or a different persistence strategy)
 *    without touching the controller at all.
 */
public interface StudentService {

    StudentResponseDto createStudent(StudentRequestDto requestDto);

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto updateStudent(Long id, StudentRequestDto requestDto);

    void deleteStudent(Long id);
}
