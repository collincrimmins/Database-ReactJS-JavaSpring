package com.mywebsite.database_javaspring_reactjs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.dto.PostCreateDTO;
import com.mywebsite.database_javaspring_reactjs.dto.PostDTO;
import com.mywebsite.database_javaspring_reactjs.dto.SliceRequestDTO;
import com.mywebsite.database_javaspring_reactjs.exceptions.PostNotFoundException;
import com.mywebsite.database_javaspring_reactjs.exceptions.StudentNotFoundException;
import com.mywebsite.database_javaspring_reactjs.exceptions.UserNotFoundException;
import com.mywebsite.database_javaspring_reactjs.model.Post;
import com.mywebsite.database_javaspring_reactjs.model.User;
import com.mywebsite.database_javaspring_reactjs.repository.PostRepository;
import com.mywebsite.database_javaspring_reactjs.repository.UserRepository;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;
import com.mywebsite.database_javaspring_reactjs.responses.SliceResponse;

import jakarta.validation.Valid;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    // Get Posts<> by Username using Pagination
    public SliceResponse<PostDTO> getPostsByUsername(String username, @Valid SliceRequestDTO sliceRequestDTO) {
        // Get User ID
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException());
        Long userID = user.getId();
        
        // Get Slice
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());;
        Slice<Post> slicePosts;
        if (sliceRequestDTO == null) {
            // Get Default First Slice
            slicePosts = postRepository.getAllPostsByUserID(userID, pageable);
        } else {
            // Get Slice after LastReadRecordID
            slicePosts = postRepository.getAllPostsByUserIDAfterLastRecordID(
                userID, 
                pageable, 
                sliceRequestDTO.getLastReadRecordID()
            );
        }

        // Get Content
        List<Post> listPosts = slicePosts.getContent();

        // Convert Post to PostDTO
        List<PostDTO> content = listPosts.stream()
            .map(post -> mapToDTO(post))
            .collect(Collectors.toList());

        // Create Pagination Response Object
        SliceResponse<PostDTO> responseSlice = new SliceResponse<>();
        responseSlice.setContent(content);
        responseSlice.setHasNext(slicePosts.hasNext());
        if (slicePosts.getContent().size() != 0) {
            responseSlice.setLastReadRecordID(content.getLast().getId());
        }

        return responseSlice;
    }

    // Create Post (Authorization)
    public void createPost(@Valid PostCreateDTO postCreateDTO) {
        // Get User
        Long userID = userService.getUserIDfromToken(postCreateDTO.getAuthtoken());
        User user = userRepository.findById(userID)
            .orElseThrow(() -> new PostNotFoundException());

        // Set Post Fields
        Post post = modelMapper.map(postCreateDTO, Post.class);
        post.setUser(user);

        postRepository.save(post);
    }











    // Map (Entity -> DTO)
    private PostDTO mapToDTO(Post post) {
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);

        return postDTO;
    }

    // Map (DTO -> Entity)
    private Post mapToEntity(PostDTO postDTO) {
        Post post = modelMapper.map(postDTO, Post.class);

        return post;
    }
}
