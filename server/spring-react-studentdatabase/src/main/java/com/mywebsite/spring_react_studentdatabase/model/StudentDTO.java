package com.mywebsite.spring_react_studentdatabase.model;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
