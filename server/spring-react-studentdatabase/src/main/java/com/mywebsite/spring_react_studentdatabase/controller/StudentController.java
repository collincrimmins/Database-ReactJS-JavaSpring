package com.mywebsite.spring_react_studentdatabase.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mywebsite.spring_react_studentdatabase.model.Student;
import com.mywebsite.spring_react_studentdatabase.model.StudentDTO;
import com.mywebsite.spring_react_studentdatabase.model.StudentResponse;
import com.mywebsite.spring_react_studentdatabase.service.IStudentService;

import lombok.AllArgsConstructor;

@CrossOrigin("http://localhost:5173")
@RestController
@AllArgsConstructor
@RequestMapping("/students")
public class StudentController {
    private final IStudentService studentService;

    // Get All Students
    @GetMapping("")
    public ResponseEntity<StudentResponse> getStudents(
        @RequestParam(value="search", defaultValue="", required=false) String search,
        @RequestParam(value="pageNumber", defaultValue="0", required=false) int pageNumber
    ) {
        // Query
        StudentResponse listStudents = studentService.getAllStudents(search, pageNumber);
        // ResponseEntity
        return new ResponseEntity<>(
            listStudents,
            HttpStatus.OK
        );
    }

    // ***
    // ADD PATH /new
    // ***
    // Create New Student
    @PostMapping("")
    public void addStudent(@RequestBody StudentDTO studentDTO) {
        studentService.createStudent(studentDTO);
    }

    // Update Student by ID
    @PutMapping("/update/{id}")
    public StudentDTO updateStudent(@RequestBody StudentDTO studentDTO, @PathVariable Long id) {
        return studentService.updateStudent(studentDTO, id);
    }

    // Delete Student by ID
    @DeleteMapping("/delete/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    // Get Student by ID
    @GetMapping("/student/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
}
