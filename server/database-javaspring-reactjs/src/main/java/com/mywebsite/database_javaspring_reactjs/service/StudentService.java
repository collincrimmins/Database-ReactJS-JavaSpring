package com.mywebsite.database_javaspring_reactjs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.dto.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.exceptions.StudentEmailRequestAlreadyExists;
import com.mywebsite.database_javaspring_reactjs.exceptions.StudentNotFoundException;
import com.mywebsite.database_javaspring_reactjs.model.Student;
import com.mywebsite.database_javaspring_reactjs.repository.StudentRepository;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;

import jakarta.validation.Valid;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Get Students<> using Pagination & Query
    public PaginationResponse<StudentDTO> getAllStudents(String search, int pageNumber) {
        // Query & Pagination
        Pageable pageable = PageRequest.of(pageNumber, 10);
        Page<Student> pageStudents = null;
        if (search.equals("")) {
            pageStudents = studentRepository.getStudents(pageable);
        } else {
            pageStudents = studentRepository.getStudentsByQuery(search, pageable);
        }

        // Get Content
        List<Student> listStudents = pageStudents.getContent();
        List<StudentDTO> content = listStudents.stream()
            .map(student -> mapToDTO(student))
            .collect(Collectors.toList());

        // Create Pagination Response Object
        PaginationResponse<StudentDTO> responsePage = new PaginationResponse<>();
        responsePage.setContent(content);
        responsePage.setPageNumber(pageStudents.getNumber());
        responsePage.setPageSize(pageStudents.getSize());
        responsePage.setTotalPages(pageStudents.getTotalPages());
        responsePage.setTotalElements(pageStudents.getTotalElements());
        
        return responsePage;
    }

    // Get Student by ID
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException());

        StudentDTO studentDTO = mapToDTO(student);

        return studentDTO;
    }

    // Create Student
    public void createStudent(@Valid StudentDTO studentDTO) {
        // Check if Email Exists
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new StudentEmailRequestAlreadyExists();
        }
        
        // Create Student
        Student student = mapToEntity(studentDTO);
        studentRepository.save(student);
    }

    // Update Student
    public void updateStudent(@Valid StudentDTO studentDTO, Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException());

        // Check if another Student already owns this Email
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            Student studentWithRequestedEmail = studentRepository.findByEmail(studentDTO.getEmail())
                .orElseThrow(() -> new StudentNotFoundException());
            if (student.getId() != studentWithRequestedEmail.getId()) {
                throw new StudentEmailRequestAlreadyExists();
            }
        }
        
        // Update Student
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        
        studentRepository.save(student);
    }

    // Delete Student by ID
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException();
        }
        studentRepository.deleteById(id);
    }


    








    // Map (Entity -> DTO)
    private StudentDTO mapToDTO(Student student) {
        StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        return studentDTO;
    }

    // Map (DTO -> Entity)
    private Student mapToEntity(StudentDTO studentDTO) {
        Student student = modelMapper.map(studentDTO, Student.class);

        return student;
    }
}
