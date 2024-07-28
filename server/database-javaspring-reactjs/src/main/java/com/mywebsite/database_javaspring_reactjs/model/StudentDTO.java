package com.mywebsite.database_javaspring_reactjs.model;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
