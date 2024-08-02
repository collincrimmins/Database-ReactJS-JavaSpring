package com.mywebsite.database_javaspring_reactjs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.mywebsite.database_javaspring_reactjs.exceptions.AuthFailedException;
import com.mywebsite.database_javaspring_reactjs.model.User;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.security.dto.AuthRequestDTO;
import com.mywebsite.database_javaspring_reactjs.security.dto.TokenDTO;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Check if Token Valid
    @PostMapping("/checkToken")
    public ResponseEntity<TokenDTO> checkToken(@Valid @RequestBody TokenDTO tokenRequest) {
        // Check if Token Expired
        String token = tokenRequest.getToken();
        boolean isExpired = true;
        try {
            isExpired = jwtService.isTokenExpired(token);
        } catch(Exception e) {}

        if (isExpired) {
            // Invalid Token
            return ResponseEntity.ok(new TokenDTO("invalid-token", ""));
        } else {
            // Valid Token
            return ResponseEntity.ok(new TokenDTO("valid-token", ""));
        }
    }

    // Register
    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@RequestBody AuthRequestDTO authRequest) {
        service.addUser(authRequest);

        return login(authRequest); //ResponseEntity.ok(new JsonResponse("created-user"));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody AuthRequestDTO authRequest) {
        // Check Email & Password
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(), 
                    authRequest.getPassword()));
        } catch(Exception e) {
            throw new AuthFailedException();
        }

        // Return Token
        if (authentication.isAuthenticated()) {
            String jwtToken = jwtService.generateToken(authRequest.getEmail());
            return ResponseEntity.ok(new TokenDTO("", jwtToken));
        }
        return null;
    }

    // @GetMapping("/user/userProfile")
    // @PreAuthorize("hasAuthority('ROLE_USER')")
    // public ResponseEntity<String> userProfile() {
    //     return ResponseEntity.ok("Welcome to User Profile");
    // }

    // @GetMapping("/admin/adminProfile")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // public ResponseEntity<String> adminProfile() {
    //     return ResponseEntity.ok("Welcome to Admin Profile");
    // }
}
