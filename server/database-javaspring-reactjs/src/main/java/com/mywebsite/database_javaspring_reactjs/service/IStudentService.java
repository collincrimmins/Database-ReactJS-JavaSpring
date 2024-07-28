package com.mywebsite.database_javaspring_reactjs.service;

import com.mywebsite.database_javaspring_reactjs.model.PageResponse;
import com.mywebsite.database_javaspring_reactjs.model.StudentDTO;

public interface IStudentService {
    // Get List<>
    PageResponse<StudentDTO> getAllStudents(String search, int pageNumber);

    // Get 1
    StudentDTO getStudentById(Long id);

    // Post
    void createStudent(StudentDTO studentDTO);

    // Put
    StudentDTO updateStudent(StudentDTO student, Long id);

    // Delete
    void deleteStudent(Long id);
}