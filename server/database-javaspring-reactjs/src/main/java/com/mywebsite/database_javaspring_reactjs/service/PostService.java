package com.mywebsite.database_javaspring_reactjs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mywebsite.database_javaspring_reactjs.dto.PostDTO;
import com.mywebsite.database_javaspring_reactjs.model.Post;
import com.mywebsite.database_javaspring_reactjs.repository.PostRepository;
import com.mywebsite.database_javaspring_reactjs.responses.PaginationResponse;

import jakarta.validation.Valid;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Get Posts<> by User Username
    public void getPostsByUsername(String username) {
        
    }

    // Create Post
    public void createPost(@Valid PostDTO postDTO) {
        Post post = mapToEntity(postDTO);
        postRepository.save(post);
    }











    // Map (Entity -> DTO)
    private PostDTO mapToDTO(Post post) {
        PostDTO PostDTO = modelMapper.map(post, PostDTO.class);

        return PostDTO;
    }

    // Map (DTO -> Entity)
    private Post mapToEntity(PostDTO postDTO) {
        Post post = modelMapper.map(postDTO, Post.class);

        return post;
    }
}
