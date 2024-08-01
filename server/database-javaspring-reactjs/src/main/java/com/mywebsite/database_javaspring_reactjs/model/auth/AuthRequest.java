package com.mywebsite.database_javaspring_reactjs.model.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
