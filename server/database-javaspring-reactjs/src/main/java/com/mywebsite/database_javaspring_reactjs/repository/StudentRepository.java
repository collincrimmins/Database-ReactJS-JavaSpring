package com.mywebsite.database_javaspring_reactjs.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mywebsite.database_javaspring_reactjs.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);

    // Get All List<Student>
    @Query(
        value = "SELECT * FROM student",
        countQuery = "SELECT count(1) FROM student",
        nativeQuery = true)
    Page<Student> getStudents(Pageable pageable);

    // Get Page<Student> using Search Query
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
            SELECT COUNT(1) 
            FROM student 
            WHERE firstName LIKE %:search%
                OR lastName LIKE %:search%
                OR id LIKE %:search%
                OR email LIKE %:search%
        """,
        nativeQuery = true)
    Page<Student> getStudentsByQuery(String search, Pageable pageable);
}