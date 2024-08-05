package com.mywebsite.database_javaspring_reactjs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mywebsite.database_javaspring_reactjs.dto.PostDTO;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;
import com.mywebsite.database_javaspring_reactjs.service.PostService;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("create")
    public ResponseEntity<JsonResponse> createPost(PostDTO postDTO) {
        postService.createPost(postDTO);

        return ResponseEntity.ok(new JsonResponse("created-post"));
    }
}
