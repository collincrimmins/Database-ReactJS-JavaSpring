package com.mywebsite.database_javaspring_reactjs.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mywebsite.database_javaspring_reactjs.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Get Slice<Post>
    @Query(
        value = """
            SELECT * 
            FROM post 
            WHERE user_id = :userID
        """, 
        nativeQuery = true)
    Slice<Post> getAllPostsByUserID(Long userID, Pageable pageable);

    // Get the next Slice<Post> after LastReadRecordID
    @Query(
        value = """
            SELECT * 
            FROM post 
            WHERE 
                user_id = :userID
                AND id < :lastReadRecordID
        """, 
        nativeQuery = true)
    Slice<Post> getAllPostsByUserIDAfterLastRecordID(
        Long userID, Pageable pageable, Long lastReadRecordID
    );
}