package com.mywebsite.spring_react_studentdatabase.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mywebsite.spring_react_studentdatabase.exception.StudentAlreadyExistsException;
import com.mywebsite.spring_react_studentdatabase.exception.StudentNotFoundException;
import com.mywebsite.spring_react_studentdatabase.model.Student;
import com.mywebsite.spring_react_studentdatabase.model.StudentDTO;
import com.mywebsite.spring_react_studentdatabase.model.StudentResponse;
import com.mywebsite.spring_react_studentdatabase.repository.StudentRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentService implements IStudentService {
    private final StudentRepository studentRepository;

    // Get All Students using Query & Pagination
    @Override
    public StudentResponse getAllStudents(String search, int pageNumber) {
        int PAGE_SIZE = 10;

        // Query & Pagination
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        Page<Student> pageStudents = null;
        if (search.equals("")) {
            pageStudents = studentRepository.getStudents(pageable); // studentRepository.findAll(pageable);
        } else {
            pageStudents = studentRepository.getStudentsByQuery(search, pageable);
        }

        // Get Content
        List<Student> listStudents = pageStudents.getContent();
        List<StudentDTO> content = listStudents.stream()
            .map(student -> mapToDTO(student))
            .collect(Collectors.toList());

        // Get Pagination Response Object
        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setContent(content);
        studentResponse.setPageNumber(pageStudents.getNumber());
        studentResponse.setPageSize(pageStudents.getSize());
        studentResponse.setTotalElements(pageStudents.getTotalElements());
        studentResponse.setTotalPages(pageStudents.getTotalPages());
        studentResponse.setLast(pageStudents.isLast());
        
        return studentResponse;
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
    public void createStudent(StudentDTO studentDTO) {
        // Check if Email Exists
        Student student = mapToEntity(studentDTO);
        if (studentAlreadyExists(student.getEmail())) {
            throw new StudentAlreadyExistsException(student.getEmail() + " already exists");
        }
        
        // Create Student
        studentRepository.save(student);
    }

    private boolean studentAlreadyExists(String email) {
        return studentRepository.findByEmail(email).isPresent();
    }

    // Update Student
    @Override
    public StudentDTO updateStudent(StudentDTO studentDTO, Long id) {
        Student student = mapToEntity(studentDTO);
        studentRepository.findById(id)
            .map(updatedStudent -> {
                updatedStudent.setFirstName(student.getFirstName());
                updatedStudent.setLastName(student.getLastName());
                updatedStudent.setEmail(student.getEmail());
                return studentRepository.save(updatedStudent);})
            .orElseThrow(() -> new StudentNotFoundException("Not found"));

        return mapToDTO(student);
    }

    // Delete Student by ID
    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("student does not exist");
        }
        studentRepository.deleteById(id);
    }








    // Entity -> DTO
    private StudentDTO mapToDTO(Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setLastName(student.getLastName());
        studentDTO.setEmail(student.getEmail());
        return studentDTO;
    }

    // DTO -> Entity
    private Student mapToEntity(StudentDTO studentDTO) {
        Student student = new Student();
        student.setId(studentDTO.getId());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        return student;
    }
}
