package com.lrchan.qootalk.infrastructure.persistence.user;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.UserRepository;

@Component
public class UserRepositoryAdapter implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserEntityMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = UserEntityMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return UserEntityMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
    
}
