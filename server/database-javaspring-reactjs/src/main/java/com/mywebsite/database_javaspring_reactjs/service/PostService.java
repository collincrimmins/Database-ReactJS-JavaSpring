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

import com.mywebsite.database_javaspring_reactjs.dto.PostDTO;
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
    private ModelMapper modelMapper;

    // Get Posts<> by Username using Pagination
    public SliceResponse<PostDTO> getPostsByUsername(String username, int pageNumber) {
        // Get User ID
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException());
        Long userID = user.getId();

        // Slice Pagination sorted by createdDate
        Pageable pageable = PageRequest.of(pageNumber, 5, Sort.by("createdDate").descending());
        Slice<Post> slicePosts = postRepository.getAllPostsByUserID(userID, pageable);

        // Get Content
        List<Post> listPosts = slicePosts.getContent();

        // Convert Post to PostDTO
        List<PostDTO> content = listPosts.stream()
            .map(post -> mapToDTO(post))
            .collect(Collectors.toList());

        // Create Pagination Response Object
        SliceResponse<PostDTO> responseSlice = new SliceResponse<>();
        responseSlice.setContent(content);
        responseSlice.setPageNumber(slicePosts.getNumber());
        responseSlice.setHasNext(slicePosts.hasNext());
        
        return responseSlice;
    }

    // Create Post (Authorization)
    public void createPost(Long userID, @Valid PostDTO postDTO) {
        // Get User from UserID
        User user = userRepository.findById(userID)
            .orElseThrow(() -> new PostNotFoundException());

        // Set Post Fields
        Post post = mapToEntity(postDTO);
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
