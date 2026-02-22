package com.lrchan.qootalk.application.user.port.out;

import java.util.Optional;

public interface RefreshTokenPort {
    void save(String userPk, String refreshToken);
    Optional<String> findByUserPk(String userPk);
    void deleteByUserPk(String userPk);
}
