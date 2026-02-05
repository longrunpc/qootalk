package com.lrchan.qootalk.application.user.port.out;

import java.util.Optional;

import com.lrchan.qootalk.domain.user.User;

public interface LoadUserPort {
    Optional<User> findByEmail(String email);
}
