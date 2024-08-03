package com.mywebsite.database_javaspring_reactjs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mywebsite.database_javaspring_reactjs.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
