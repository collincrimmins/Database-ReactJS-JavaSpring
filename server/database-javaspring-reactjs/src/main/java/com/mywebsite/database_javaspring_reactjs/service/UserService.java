package com.mywebsite.database_javaspring_reactjs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.dto.UserDTO;
import com.mywebsite.database_javaspring_reactjs.model.User;
import com.mywebsite.database_javaspring_reactjs.repository.UserRepository;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;

import jakarta.validation.Valid;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    


    








    // Map (Entity -> DTO)
    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        
        return userDTO;
    }

    // Map (DTO -> Entity)
    private User mapToEntity(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        return user;
    }
}
