package com.example.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Student Management System application.
 *
 * @SpringBootApplication is a convenience annotation that combines:
 *   - @Configuration      : marks this class as a source of bean definitions
 *   - @EnableAutoConfiguration : lets Spring Boot auto-configure beans based on classpath contents
 *                                (e.g., seeing MySQL driver + JPA starter auto-configures a DataSource)
 *   - @ComponentScan      : scans this package and sub-packages for @Component, @Service,
 *                            @Repository, @Controller/@RestController beans
 */
@SpringBootApplication
public class StudentManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementSystemApplication.class, args);
    }
}
