package com.lrchan.qootalk.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User save(User user);
    boolean existsByEmail(String email);
}
