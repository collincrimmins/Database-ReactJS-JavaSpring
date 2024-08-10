package com.mywebsite.database_javaspring_reactjs.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    @NotNull
    private Long id;

    private String username;

    private String photo;
}
