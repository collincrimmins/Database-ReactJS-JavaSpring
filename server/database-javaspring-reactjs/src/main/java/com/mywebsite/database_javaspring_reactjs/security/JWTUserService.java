package com.mywebsite.database_javaspring_reactjs.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.exceptions.UserEmailRequestAlreadyExists;
import com.mywebsite.database_javaspring_reactjs.model.User;
import com.mywebsite.database_javaspring_reactjs.repository.UserRepository;
import com.mywebsite.database_javaspring_reactjs.security.dto.AuthRequestDTO;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class JWTUserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Turn "User" entity into "UserDetail"
    @Override
    public UserDetails loadUserByUsername(String UserID) throws UsernameNotFoundException {
        Optional<User> userDetail = userRepository.findByEmail(UserID);

        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + UserID));
    }

    // Create User
    public String createUser(@Valid AuthRequestDTO authRequest) {
        // Check if Email Exists
        boolean emailExists = userRepository.existsByEmail(authRequest.getEmail());
        if (emailExists) {
            throw new UserEmailRequestAlreadyExists();
        }
        
        // Save User & Set Password
        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        user.setName("myusername");
        user.setRoles("ROLE_USER");
        
        userRepository.save(user);

        return "User Added Successfully";
    }
}
