package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.UserId;

import java.util.Optional;

public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(UserId userId);
    
    Optional<User> findByEmail(Email email);
    
    boolean existsByEmail(Email email);
    
    void delete(User user);
    
    void deleteById(UserId userId);
}
