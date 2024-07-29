package com.mywebsite.database_javaspring_reactjs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.exceptions.StudentEmailRequestAlreadyExists;
import com.mywebsite.database_javaspring_reactjs.exceptions.StudentNotFoundException;
import com.mywebsite.database_javaspring_reactjs.model.Student;
import com.mywebsite.database_javaspring_reactjs.model.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.repository.StudentRepository;
import com.mywebsite.database_javaspring_reactjs.responses.PageResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentService implements IStudentService {
    private final StudentRepository studentRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Get All Students using Query & Pagination
    @Override
    public PageResponse<StudentDTO> getAllStudents(String search, int pageNumber) {
        // Query & Pagination
        int PAGE_SIZE = 10;
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
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
        PageResponse<StudentDTO> responsePage = new PageResponse<>();
        responsePage.setContent(content);
        responsePage.setPageNumber(pageStudents.getNumber());
        responsePage.setPageSize(pageStudents.getSize());
        responsePage.setTotalPages(pageStudents.getTotalPages());
        responsePage.setTotalElements(pageStudents.getTotalElements());
        
        return responsePage;
    }

    // Get Student by ID
    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id + " not found"));

        StudentDTO studentDTO = mapToDTO(student);

        return studentDTO;
    }

    // Create Student
    @Override
    public String createStudent(StudentDTO studentDTO) {
        // Check if Email Exists
        if (checkStudentExistsByEmail(studentDTO.getEmail())) {
            throw new StudentEmailRequestAlreadyExists();
        }
        
        // Create Student
        Student student = mapToEntity(studentDTO);
        studentRepository.save(student);

        return "created student";
    }

    private boolean checkStudentExistsByEmail(String email) {
        return studentRepository.findByEmail(email).isPresent();
    }

    // Update Student
    @Override
    public void updateStudent(StudentDTO studentDTO, Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id + " does not exist"));

        // Check if another Student already owns this Email
        if (checkStudentExistsByEmail(studentDTO.getEmail())) {
            Student studentWithRequestedEmail = studentRepository.findByEmail(studentDTO.getEmail())
                .orElseThrow(() -> new StudentNotFoundException("email does not exist"));
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
    @Override
    public String deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id + " does not exist");
        }
        studentRepository.deleteById(id);

        return "deleted student";
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

        // Get Entity from Database
        // if (postDto.getId() != null) {
        //     Post oldPost = postService.getPostById(postDto.getId());
        //     post.setRedditID(oldPost.getRedditID());
        //     post.setSent(oldPost.isSent());
        // }
    }
}
