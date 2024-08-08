package com.mywebsite.database_javaspring_reactjs.dto;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String username;
}
