package com.mywebsite.database_javaspring_reactjs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mywebsite.database_javaspring_reactjs.dto.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;
import com.mywebsite.database_javaspring_reactjs.service.StudentService;

@RestController
@RequestMapping("/v1/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    // Get Page<Students>
    @GetMapping("")
    public ResponseEntity<PaginationResponse<StudentDTO>> getStudents(
        @RequestParam(value="search", defaultValue="", required=false) String search,
        @RequestParam(value="pageNumber", defaultValue="0", required=false) int pageNumber
    ) {
        PaginationResponse<StudentDTO> content = studentService.getAllStudents(search, pageNumber);
        
        return ResponseEntity.ok(content);
    }

    // Get Student by ID
    @GetMapping("/student/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // Create New Student
    @PostMapping("/new")
    public ResponseEntity<JsonResponse> addStudent(@RequestBody StudentDTO studentDTO) {
        studentService.createStudent(studentDTO);

        return ResponseEntity.ok(new JsonResponse("created-student"));
    }

    // Update Student by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<JsonResponse> updateStudent(@RequestBody StudentDTO studentDTO, @PathVariable Long id) {
        studentService.updateStudent(studentDTO, id);

        return ResponseEntity.ok(new JsonResponse("updated-student"));
    }

    // Delete Student by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<JsonResponse> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);

        return ResponseEntity.ok(new JsonResponse("deleted-student"));
    }

    // Do something (Protected by Authorization)
    // @GetMapping("/AuthTest")
    // public void testAuthGet(
    //     @RequestHeader(value="Authorization", required=true) String token
    // ) {
    //     // boolean Result = jwtservice.checkToken(token);
    //     // System.out.println(Result);
    // }
}
