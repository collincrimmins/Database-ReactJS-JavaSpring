package com.mywebsite.database_javaspring_reactjs.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@Table(name = "student")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NaturalId(mutable = true)
    @Email
    @NotBlank
    private String email;

    @CreationTimestamp
    private LocalDateTime createdDate;
}
