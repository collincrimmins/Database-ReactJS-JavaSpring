package com.mywebsite.database_javaspring_reactjs.controller;

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

import com.mywebsite.database_javaspring_reactjs.model.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.responses.PageResponse;
import com.mywebsite.database_javaspring_reactjs.service.IStudentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@CrossOrigin("http://localhost:5173")
@RestController
@AllArgsConstructor
@RequestMapping("/students")
public class StudentController {
    private final IStudentService studentService;

    // Get All Students
    @GetMapping("")
    public ResponseEntity<PageResponse<StudentDTO>> getStudents(
        @RequestParam(value="search", defaultValue="", required=false) String search,
        @RequestParam(value="pageNumber", defaultValue="0", required=false) int pageNumber
    ) {
        // Query
        PageResponse<StudentDTO> listStudents = studentService.getAllStudents(search, pageNumber);
        
        return ResponseEntity.ok(listStudents);
    }

    // Create New Student
    @PostMapping("/new")
    public ResponseEntity<JsonResponse> addStudent(@Valid @RequestBody StudentDTO studentDTO) {
        studentService.createStudent(studentDTO);

        return ResponseEntity.ok(new JsonResponse("created-student"));
    }

    // Update Student by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<JsonResponse> updateStudent(@Valid @RequestBody StudentDTO studentDTO, @PathVariable Long id) {
        studentService.updateStudent(studentDTO, id);

        return ResponseEntity.ok(new JsonResponse("updated-student"));
    }

    // Delete Student by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<JsonResponse> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);

        return ResponseEntity.ok(new JsonResponse("deleted-student"));
    }

    // Get Student by ID
    @GetMapping("/student/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
}
