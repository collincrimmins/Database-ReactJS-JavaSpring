package com.mywebsite.database_javaspring_reactjs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mywebsite.database_javaspring_reactjs.modelDTO.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;
import com.mywebsite.database_javaspring_reactjs.security.JwtService;
import com.mywebsite.database_javaspring_reactjs.service.StudentService;

@CrossOrigin
@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService service;

    @Autowired
    private JwtService jwtservice;

    // Get Students
    @GetMapping("")
    public ResponseEntity<PaginationResponse<StudentDTO>> getStudents(
        @RequestParam(value="search", defaultValue="", required=false) String search,
        @RequestParam(value="pageNumber", defaultValue="0", required=false) int pageNumber
    ) {
        // Query
        PaginationResponse<StudentDTO> listStudents = service.getAllStudents(search, pageNumber);
        
        return ResponseEntity.ok(listStudents);
    }

    // Get Student by ID
    @GetMapping("/student/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return service.getStudentById(id);
    }

    // Create New Student
    @PostMapping("/new")
    public ResponseEntity<JsonResponse> addStudent(@RequestBody StudentDTO studentDTO) {
        service.createStudent(studentDTO);

        return ResponseEntity.ok(new JsonResponse("created-student"));
    }

    // Update Student by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<JsonResponse> updateStudent(@RequestBody StudentDTO studentDTO, @PathVariable Long id) {
        service.updateStudent(studentDTO, id);

        return ResponseEntity.ok(new JsonResponse("updated-student"));
    }

    // Delete Student by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<JsonResponse> deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);

        return ResponseEntity.ok(new JsonResponse("deleted-student"));
    }

    // Do something (Protected by Authorization)
    @GetMapping("/AuthTest")
    public void testAuthGet(
        @RequestHeader(value="Authorization", required=true) String token
    ) {
        // boolean Result = jwtservice.checkToken(token);
        // System.out.println(Result);
    }
}
