package com.mywebsite.database_javaspring_reactjs.controller;

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

import com.mywebsite.database_javaspring_reactjs.dto.AuthResponseDTO;
import com.mywebsite.database_javaspring_reactjs.model.AuthRequest;
import com.mywebsite.database_javaspring_reactjs.model.User;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.service.JwtService;
import com.mywebsite.database_javaspring_reactjs.service.UserService;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/addNewUser")
    public ResponseEntity<JsonResponse> addNewUser(@RequestBody AuthRequest authRequest) {
        service.addUser(authRequest);

        return ResponseEntity.ok(new JsonResponse("created-user"));
    }

    @PostMapping("/generateToken")
    public ResponseEntity<AuthResponseDTO> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        // Check Email & Password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        // Return Token
        if (authentication.isAuthenticated()) {
            String jwtToken = jwtService.generateToken(authRequest.getEmail());
            return ResponseEntity.ok(new AuthResponseDTO(jwtToken));
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> userProfile() {
        return ResponseEntity.ok("Welcome to User Profile");
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> adminProfile() {
        return ResponseEntity.ok("Welcome to Admin Profile");
    }
}
