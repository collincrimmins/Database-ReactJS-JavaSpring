package com.mywebsite.database_javaspring_reactjs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.exceptions.StudentEmailRequestAlreadyExists;
import com.mywebsite.database_javaspring_reactjs.exceptions.StudentNotFoundException;
import com.mywebsite.database_javaspring_reactjs.model.Student;
import com.mywebsite.database_javaspring_reactjs.dto.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.repository.StudentRepository;
import com.mywebsite.database_javaspring_reactjs.responses.PageResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentService implements IStudentService {
    private final StudentRepository database;

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
            pageStudents = database.getStudents(pageable);
        } else {
            pageStudents = database.getStudentsByQuery(search, pageable);
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
        Student student = database.findById(id)
            .orElseThrow(() -> new StudentNotFoundException());

        StudentDTO studentDTO = mapToDTO(student);

        return studentDTO;
    }

    // Create Student
    @Override
    public void createStudent(@Valid StudentDTO studentDTO) {
        // Check if Email Exists
        if (checkStudentExistsByEmail(studentDTO.getEmail())) {
            throw new StudentEmailRequestAlreadyExists();
        }
        
        // Create Student
        Student student = mapToEntity(studentDTO);
        database.save(student);
    }

    // Update Student
    @Override
    public void updateStudent(@Valid StudentDTO studentDTO, Long id) {
        Student student = database.findById(id)
            .orElseThrow(() -> new StudentNotFoundException());

        // Check if another Student already owns this Email
        if (checkStudentExistsByEmail(studentDTO.getEmail())) {
            Student studentWithRequestedEmail = database.findByEmail(studentDTO.getEmail())
                .orElseThrow(() -> new StudentNotFoundException());
            if (student.getId() != studentWithRequestedEmail.getId()) {
                throw new StudentEmailRequestAlreadyExists();
            }
        }
        
        // Update Student
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        database.save(student);
    }

    // Delete Student by ID
    @Override
    public void deleteStudent(Long id) {
        if (!database.existsById(id)) {
            throw new StudentNotFoundException();
        }
        database.deleteById(id);
    }


    







    // Check Student Exists with Email
    private boolean checkStudentExistsByEmail(String email) {
        return database.findByEmail(email).isPresent();
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
