package com.wesports.backend.infrastructure.persistence.repository;

import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.repository.UserRepository;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.infrastructure.persistence.jpa.SpringUserRepository;
import com.wesports.backend.infrastructure.persistence.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final SpringUserRepository springUserRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserRepositoryAdapter(SpringUserRepository springUserRepository, UserMapper userMapper) {
        this.springUserRepository = springUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        var entity = userMapper.toEntity(user);
        var savedEntity = springUserRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return springUserRepository.findById(userId.getValue())
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springUserRepository.findByEmail(email.getValue())
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springUserRepository.existsByEmail(email.getValue());
    }

    @Override
    public void delete(User user) {
        var entity = userMapper.toEntity(user);
        springUserRepository.delete(entity);
    }

    @Override
    public void deleteById(UserId userId) {
        springUserRepository.deleteById(userId.getValue());
    }
}
