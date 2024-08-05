package com.mywebsite.database_javaspring_reactjs.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import com.mywebsite.database_javaspring_reactjs.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    private Long id;

    private User user;

    @NotEmpty
    private String text;

    private LocalDateTime createdDate;
}
