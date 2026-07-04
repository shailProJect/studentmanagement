package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Student.
 *
 * By extending JpaRepository<Student, Long>, we get CRUD methods
 * (save, findById, findAll, deleteById, etc.) implemented for us at
 * runtime by Spring — no SQL, no boilerplate implementation class needed.
 *
 * @Repository is technically optional here (JpaRepository sub-interfaces
 * are auto-detected by Spring Data), but adding it explicitly documents
 * intent and enables Spring's persistence-exception-translation, which
 * converts low-level JDBC/Hibernate exceptions into Spring's consistent
 * DataAccessException hierarchy.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Derived query method — Spring Data JPA generates the implementation
     * from the method name alone (SELECT * FROM students WHERE email = ?).
     * Used by the service layer to enforce email uniqueness at the
     * business-logic level before hitting the DB unique constraint.
     */
    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);
}
