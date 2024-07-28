package com.mywebsite.spring_react_studentdatabase.service;

import java.util.List;

import com.mywebsite.spring_react_studentdatabase.model.StudentDTO;
import com.mywebsite.spring_react_studentdatabase.model.StudentResponse;

public interface IStudentService {
    // Get
    StudentResponse getAllStudents(String search, int pageNumber);
    StudentDTO getStudentById(Long id);

    // Post
    void createStudent(StudentDTO studentDTO);

    // Put
    StudentDTO updateStudent(StudentDTO student, Long id);

    // Delete
    void deleteStudent(Long id);
}