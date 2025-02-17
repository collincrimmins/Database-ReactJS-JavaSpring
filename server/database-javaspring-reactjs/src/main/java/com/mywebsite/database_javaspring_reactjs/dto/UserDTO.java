package com.mywebsite.database_javaspring_reactjs.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
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
