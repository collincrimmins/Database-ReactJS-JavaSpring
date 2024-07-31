package com.mywebsite.database_javaspring_reactjs.dto;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private Long id;
    
    @NotEmpty(message = "invalid")
    private String firstName;

    @NotEmpty(message = "invalid")
    private String lastName;

    @Email(message = "invalid")
    @NotEmpty(message = "invalid")
    private String email;
}
