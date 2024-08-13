package com.mywebsite.database_javaspring_reactjs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mywebsite.database_javaspring_reactjs.dto.PostCreateDTO;
import com.mywebsite.database_javaspring_reactjs.dto.PostDTO;
import com.mywebsite.database_javaspring_reactjs.dto.SliceRequestDTO;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.responses.SliceResponse;
import com.mywebsite.database_javaspring_reactjs.service.PostService;

@RestController
@RequestMapping("/v1/posts")
public class PostController {
    @Autowired
    private PostService postService;

    // Get Profile Feed with Slice<Post>
    @PostMapping("/profilefeed/{username}")
    public ResponseEntity<SliceResponse<PostDTO>> getPosts(
        @PathVariable(value="username", required=true) String username,
        @RequestBody(required=false) SliceRequestDTO sliceRequestDTO
    ) {
        SliceResponse<PostDTO> content = postService.getPostsByUsername(username, sliceRequestDTO);
        
        return ResponseEntity.ok(content);
    }

    // Create Post
    @PostMapping("/create")
    public ResponseEntity<JsonResponse> createPost(
        @RequestBody PostCreateDTO postCreateDTO
    ) {
        postService.createPost(postCreateDTO);

        return ResponseEntity.ok(new JsonResponse("created-post"));
    }
}