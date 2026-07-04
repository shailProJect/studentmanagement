package com.example.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity representing the "students" table.
 *
 * Note: We deliberately use @Getter/@Setter instead of Lombok's @Data.
 * @Data auto-generates equals()/hashCode() based on all fields, which is a known
 * footgun for JPA entities (can break Hibernate's identity/proxy handling inside
 * collections). Explicit getters/setters keep entity identity semantics predictable.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA lifecycle callback executed automatically right before the entity
     * is first persisted. Ensures createdAt is always set by the server,
     * never by the client.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
