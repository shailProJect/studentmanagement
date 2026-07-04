package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.DuplicateResourceException;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.mapper.StudentMapper;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentServiceImpl.
 *
 * Why unit tests at the SERVICE layer specifically? This is where our
 * business rules live (uniqueness checks, existence checks). We test this
 * layer in isolation — with the repository and mapper MOCKED — so:
 *   1. Tests run fast (no real database, no Spring context startup)
 *   2. We are testing OUR logic, not Hibernate's or MySQL's behavior
 *   3. Failures point precisely to a business-rule bug, not an
 *      infrastructure/config issue
 *
 * @ExtendWith(MockitoExtension.class) wires up Mockito annotations
 * (@Mock, @InjectMocks) without needing a full Spring ApplicationContext —
 * this is what keeps these tests fast and "unit" rather than "integration".
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentRequestDto requestDto;
    private StudentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("Rahul")
                .lastName("Sharma")
                .email("rahul.sharma@example.com")
                .age(21)
                .department("Computer Science")
                .createdAt(LocalDateTime.now())
                .build();

        requestDto = StudentRequestDto.builder()
                .firstName("Rahul")
                .lastName("Sharma")
                .email("rahul.sharma@example.com")
                .age(21)
                .department("Computer Science")
                .build();

        responseDto = StudentResponseDto.builder()
                .id(1L)
                .firstName("Rahul")
                .lastName("Sharma")
                .email("rahul.sharma@example.com")
                .age(21)
                .department("Computer Science")
                .createdAt(student.getCreatedAt())
                .build();
    }

    @Test
    void createStudent_shouldSaveAndReturnStudent_whenEmailIsUnique() {
        // Arrange
        when(studentRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(studentMapper.toEntity(requestDto)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponseDto(student)).thenReturn(responseDto);

        // Act
        StudentResponseDto result = studentService.createStudent(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("rahul.sharma@example.com");
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void createStudent_shouldThrowDuplicateResourceException_whenEmailAlreadyExists() {
        // Arrange
        when(studentRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(requestDto.getEmail());

        // Verify save was NEVER called — this is the critical business rule check
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void getAllStudents_shouldReturnListOfStudents() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(List.of(student));
        when(studentMapper.toResponseDto(student)).thenReturn(responseDto);

        // Act
        List<StudentResponseDto> result = studentService.getAllStudents();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("rahul.sharma@example.com");
    }

    @Test
    void getStudentById_shouldReturnStudent_whenStudentExists() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDto(student)).thenReturn(responseDto);

        // Act
        StudentResponseDto result = studentService.getStudentById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getStudentById_shouldThrowResourceNotFoundException_whenStudentDoesNotExist() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStudent_shouldUpdateAndReturnStudent_whenStudentExistsAndEmailIsAvailable() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponseDto(student)).thenReturn(responseDto);

        // Act
        StudentResponseDto result = studentService.updateStudent(1L, requestDto);

        // Assert
        assertThat(result).isNotNull();
        verify(studentMapper, times(1)).updateEntityFromDto(requestDto, student);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void updateStudent_shouldThrowResourceNotFoundException_whenStudentDoesNotExist() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.updateStudent(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void updateStudent_shouldThrowDuplicateResourceException_whenEmailBelongsToAnotherStudent() {
        // Arrange: student with id=1 wants to update to an email owned by student id=2
        Student anotherStudent = Student.builder().id(2L).email(requestDto.getEmail()).build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(anotherStudent));

        // Act & Assert
        assertThatThrownBy(() -> studentService.updateStudent(1L, requestDto))
                .isInstanceOf(DuplicateResourceException.class);

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void deleteStudent_shouldDeleteStudent_whenStudentExists() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act
        studentService.deleteStudent(1L);

        // Assert
        verify(studentRepository, times(1)).delete(student);
    }

    @Test
    void deleteStudent_shouldThrowResourceNotFoundException_whenStudentDoesNotExist() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).delete(any(Student.class));
    }
}
