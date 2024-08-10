package com.mywebsite.database_javaspring_reactjs.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mywebsite.database_javaspring_reactjs.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user"})
public class PostDTO {
    private Long id;

    @JsonBackReference
    //@JsonIdentityReference(alwaysAsId = true) would serialize it into an ID
    private User user;

    @Column(insertable = false, updatable = false)
    private Long userID;

    @NotBlank
    private String text;

    private LocalDateTime createdDate;
}
