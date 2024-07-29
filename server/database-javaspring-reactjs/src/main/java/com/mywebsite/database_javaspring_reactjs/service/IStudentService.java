package com.mywebsite.database_javaspring_reactjs.service;

import org.springframework.http.ResponseEntity;

import com.mywebsite.database_javaspring_reactjs.model.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.responses.PageResponse;

public interface IStudentService {
    // Get List<>
    PageResponse<StudentDTO> getAllStudents(String search, int pageNumber);

    // Get
    StudentDTO getStudentById(Long id);

    // Post
    String createStudent(StudentDTO studentDTO);

    // Put
    void updateStudent(StudentDTO student, Long id);

    // Delete
    String deleteStudent(Long id);
}