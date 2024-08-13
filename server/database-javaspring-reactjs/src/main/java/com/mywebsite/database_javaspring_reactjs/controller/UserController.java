package com.mywebsite.database_javaspring_reactjs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mywebsite.database_javaspring_reactjs.dto.UserInfoDTO;
import com.mywebsite.database_javaspring_reactjs.security.dto.TokenDTO;
import com.mywebsite.database_javaspring_reactjs.service.UserService;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    // Get List<UserInfoDTO> with Public Info
    @PostMapping("/infolist")
    public ResponseEntity<List<UserInfoDTO>> getUsersInfo(
        @RequestBody(required = true) List<UserInfoDTO> list
    ) {
        List<UserInfoDTO> usersList = userService.getUserInfo(list);
        
        return ResponseEntity.ok(usersList);
    }

    // Get UserInfoDTO with Public Info
    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUsersInfoByUsername(
        @RequestParam(required = true) String username
    ) {
        UserInfoDTO userInfoDTO = userService.getUserInfoByUsername(username);
        
        return ResponseEntity.ok(userInfoDTO);
    }

    // Get my UserProfile from my Auth Token
    @PostMapping("/getmyprofile")
    public ResponseEntity<UserInfoDTO> getMyUserProfile(
        @RequestBody(required = true) TokenDTO tokenDTO
    ) {
        UserInfoDTO userInfo = userService.getMyUserProfile(tokenDTO);
        
        return ResponseEntity.ok(userInfo);
    }
}
