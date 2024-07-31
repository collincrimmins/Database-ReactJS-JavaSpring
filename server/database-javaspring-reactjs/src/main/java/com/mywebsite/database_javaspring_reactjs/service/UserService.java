package com.mywebsite.database_javaspring_reactjs.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.exceptions.UserEmailRequestAlreadyExists;
import com.mywebsite.database_javaspring_reactjs.model.AuthRequest;
import com.mywebsite.database_javaspring_reactjs.model.User;
import com.mywebsite.database_javaspring_reactjs.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository database;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = database.findByEmail(username);

        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public String addUser(@Valid AuthRequest authRequest) {
        // Check if Email Exists
        boolean emailExists = database.findByEmail(authRequest.getEmail()).isPresent();
        if (emailExists) {
            throw new UserEmailRequestAlreadyExists();
        }
        
        // Save User & Set Password
        User user = new User();
        user.setName("testname");
        user.setEmail(authRequest.getEmail());
        user.setPassword(encoder.encode(authRequest.getPassword()));
        user.setRoles("ROLE_USER");
        database.save(user);

        return "User Added Successfully";
    }
}
