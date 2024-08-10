package com.mywebsite.database_javaspring_reactjs.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateDTO {
    @NotBlank
    private String text;

    @NotBlank
    private String authtoken;
}
