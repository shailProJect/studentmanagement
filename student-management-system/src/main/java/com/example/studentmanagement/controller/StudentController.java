package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing CRUD endpoints for Student resources.
 *
 * This class is intentionally "thin" — it does NOT contain business logic.
 * Its only responsibilities are:
 *   1. Map HTTP requests to Java method calls
 *   2. Delegate to the service layer
 *   3. Wrap the result in an appropriate ResponseEntity with correct status code
 *
 * @RestController = @Controller + @ResponseBody, meaning every method's
 * return value is serialized directly into the HTTP response body as JSON
 * (instead of being resolved to a view template).
 *
 * @RequestMapping("/students") sets a common base path for every endpoint
 * in this controller.
 *
 * @Tag is Swagger/OpenAPI metadata — it groups these endpoints under a
 * labeled section in the generated Swagger UI.
 */
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for managing student records")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<StudentResponseDto> createStudent(
            @Valid @RequestBody StudentRequestDto requestDto) {

        logger.info("Received request to create student with email: {}", requestDto.getEmail());
        StudentResponseDto createdStudent = studentService.createStudent(requestDto);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Retrieve all students")
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        logger.info("Received request to fetch all students");
        List<StudentResponseDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a student by id")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        logger.info("Received request to fetch student with id: {}", id);
        StudentResponseDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing student")
    public ResponseEntity<StudentResponseDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto requestDto) {

        logger.info("Received request to update student with id: {}", id);
        StudentResponseDto updatedStudent = studentService.updateStudent(id, requestDto);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student by id")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        logger.info("Received request to delete student with id: {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
