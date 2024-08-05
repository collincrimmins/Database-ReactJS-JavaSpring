package com.mywebsite.database_javaspring_reactjs.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mywebsite.database_javaspring_reactjs.model.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

}