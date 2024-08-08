package com.mywebsite.database_javaspring_reactjs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mywebsite.database_javaspring_reactjs.dto.PostCreateDTO;
import com.mywebsite.database_javaspring_reactjs.dto.PostDTO;
import com.mywebsite.database_javaspring_reactjs.dto.StudentDTO;
import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;
import com.mywebsite.database_javaspring_reactjs.responses.SliceResponse;
import com.mywebsite.database_javaspring_reactjs.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    // Get Page<Post>
    @GetMapping("/profilefeed/{username}")
    public ResponseEntity<SliceResponse<PostDTO>> getPosts(
        @PathVariable(value="username", required=true) String username,
        @RequestParam(value="pageNumber", defaultValue="0", required=false) int pageNumber
    ) {
        SliceResponse<PostDTO> content = postService.getPostsByUsername(username, pageNumber);
        
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