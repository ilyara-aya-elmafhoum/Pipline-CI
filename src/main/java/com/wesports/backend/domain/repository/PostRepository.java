package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {
    
    Post save(Post post);
    
    Optional<Post> findById(UUID postId);
    
    Optional<Post> findByCode(String code);
    
    List<Post> findAll();
    
    boolean existsByCode(String code);
    
    void delete(Post post);
    
    void deleteById(UUID postId);
}
