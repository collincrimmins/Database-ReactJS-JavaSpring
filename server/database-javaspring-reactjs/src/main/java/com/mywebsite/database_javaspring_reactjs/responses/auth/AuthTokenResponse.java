package com.mywebsite.database_javaspring_reactjs.responses.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokenResponse {
    private String token;
}
