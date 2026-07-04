package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.DuplicateResourceException;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.mapper.StudentMapper;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Concrete implementation of StudentService — this is where actual
 * business rules live (uniqueness checks, existence checks, orchestration
 * between repository and mapper).
 *
 * @Service marks this as a Spring-managed bean in the service layer
 * (semantically equivalent to @Component, but signals intent clearly
 * and is the conventional choice for business-logic classes).
 *
 * @RequiredArgsConstructor (Lombok) generates a constructor for all
 * `final` fields below. Combined with Spring's constructor injection,
 * this means:
 *   - dependencies are immutable (final) once the bean is constructed
 *   - the class is easy to unit test — just call `new StudentServiceImpl(mockRepo, mockMapper)`
 *   - there's no need for @Autowired at all, since Spring auto-detects
 *     a single constructor and injects it
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * @Transactional ensures the save operation (and any future related
     * writes we add here) is atomic — if anything fails mid-method,
     * the transaction rolls back rather than leaving partial data.
     */
    @Override
    @Transactional
    public StudentResponseDto createStudent(StudentRequestDto requestDto) {
        logger.info("Attempting to create student with email: {}", requestDto.getEmail());

        if (studentRepository.existsByEmail(requestDto.getEmail())) {
            logger.warn("Student creation failed - email already exists: {}", requestDto.getEmail());
            throw new DuplicateResourceException(
                    "A student with email '" + requestDto.getEmail() + "' already exists");
        }

        Student student = studentMapper.toEntity(requestDto);
        Student savedStudent = studentRepository.save(student);

        logger.info("Student created successfully with id: {}", savedStudent.getId());
        return studentMapper.toResponseDto(savedStudent);
    }

    /**
     * Read-only transaction — hints to Hibernate it can skip dirty-checking
     * overhead since we won't be modifying entities in this method.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        logger.info("Fetching all students");

        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long id) {
        logger.info("Fetching student with id: {}", id);

        Student student = findStudentOrThrow(id);
        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(Long id, StudentRequestDto requestDto) {
        logger.info("Attempting to update student with id: {}", id);

        Student existingStudent = findStudentOrThrow(id);

        // If the email is being changed, make sure the new email isn't already
        // taken by a DIFFERENT student.
        boolean emailBelongsToAnotherStudent = studentRepository.findByEmail(requestDto.getEmail())
                .map(Student::getId)
                .filter(existingId -> !existingId.equals(id))
                .isPresent();

        if (emailBelongsToAnotherStudent) {
            logger.warn("Update failed - email already in use by another student: {}", requestDto.getEmail());
            throw new DuplicateResourceException(
                    "Email '" + requestDto.getEmail() + "' is already in use by another student");
        }

        studentMapper.updateEntityFromDto(requestDto, existingStudent);
        Student updatedStudent = studentRepository.save(existingStudent);

        logger.info("Student with id {} updated successfully", id);
        return studentMapper.toResponseDto(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        logger.info("Attempting to delete student with id: {}", id);

        Student student = findStudentOrThrow(id);
        studentRepository.delete(student);

        logger.info("Student with id {} deleted successfully", id);
    }

    /**
     * Shared helper to avoid repeating the "find or throw 404" pattern
     * across every method that needs an existing student.
     */
    private Student findStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + id));
    }
}
