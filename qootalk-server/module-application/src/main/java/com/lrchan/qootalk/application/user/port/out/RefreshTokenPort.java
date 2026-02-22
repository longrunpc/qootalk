package com.lrchan.qootalk.application.user.port.out;

import java.util.Optional;

import com.lrchan.qootalk.domain.user.vo.Token;

public interface RefreshTokenPort {
    void save(String userPk, Token refreshToken);
    Optional<Token> findByUserPk(String userPk);
    void deleteByUserPk(String userPk);
}
