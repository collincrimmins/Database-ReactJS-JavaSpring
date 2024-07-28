package com.mywebsite.spring_react_studentdatabase.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mywebsite.spring_react_studentdatabase.model.Student;
import com.mywebsite.spring_react_studentdatabase.model.StudentResponse;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

    // Get by Field "email"
    Optional<Student> findByEmail(String email);

    // Get All Students
    @Query(
        value = "SELECT * FROM student",
        countQuery = "SELECT count(*) FROM student",
        nativeQuery = true)
    Page<Student> getStudents(Pageable pageable);

    // Get All Students using Search Query
    @Query(
        value = """
            SELECT * 
            FROM student 
            WHERE firstName LIKE %:search%
                OR lastName LIKE %:search%
                OR id LIKE %:search%
                OR email LIKE %:search%
        """,
        countQuery = """
            SELECT COUNT(*) 
            FROM student 
            WHERE firstName LIKE %:search%
                OR lastName LIKE %:search%
                OR id LIKE %:search%
                OR email LIKE %:search%
        """,
        nativeQuery = true)
    Page<Student> getStudentsByQuery(String search, Pageable pageable);
}
