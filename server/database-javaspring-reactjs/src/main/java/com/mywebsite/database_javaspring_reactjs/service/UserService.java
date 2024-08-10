package com.mywebsite.database_javaspring_reactjs.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.mywebsite.database_javaspring_reactjs.dto.UserDTO;
import com.mywebsite.database_javaspring_reactjs.dto.UserInfoDTO;
import com.mywebsite.database_javaspring_reactjs.exceptions.UserNotFoundException;
import com.mywebsite.database_javaspring_reactjs.model.User;
import com.mywebsite.database_javaspring_reactjs.repository.UserRepository;
import com.mywebsite.database_javaspring_reactjs.security.JwtService;
import com.mywebsite.database_javaspring_reactjs.security.dto.TokenDTO;

import jakarta.validation.Valid;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    String TEST_PHOTO = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1200px-Google_%22G%22_logo.svg.png";

    // Get UserID from AuthToken
    public Long getUserIDfromToken(String AuthToken) {
        Long userID = Long.parseLong(jwtService.extractUserID(AuthToken));
        
        return userID;
    }

    // Get List<> with Public Info
    public List<UserInfoDTO> getUserInfo(@Valid List<UserInfoDTO> list) {
        for (UserInfoDTO userInfoDTO : list) {
            // Get User
            Long userID = userInfoDTO.getId();
            User user = userRepository.findById(userID)
             .orElseThrow(() -> new UserNotFoundException());
            // Set Fields
            userInfoDTO.setUsername(user.getUsername());
            userInfoDTO.setPhoto(TEST_PHOTO);
        }
        
        return list;
    }

    // Get my UserProfile from my Auth Token
    public UserInfoDTO getMyUserProfile(@Valid @RequestBody TokenDTO tokenRequest) {
        Long userID = getUserIDfromToken(tokenRequest.getToken());
       // Long userID = Long.parseLong(jwtService.extractUserID(tokenRequest.getToken()));
        User user = userRepository.findById(userID)
             .orElseThrow(() -> new UserNotFoundException());

        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPhoto(TEST_PHOTO);

        return userInfo;
    }


    








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
