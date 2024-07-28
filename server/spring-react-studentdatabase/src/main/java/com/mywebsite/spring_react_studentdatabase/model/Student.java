package com.mywebsite.spring_react_studentdatabase.model;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Data;

@Entity
@Data
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NaturalId(mutable = true)
    @NotNull
    @NotEmpty
    @Email
    @Pattern(regexp=".+@.+\\..+")
    private String email;
}
