package com.lrchan.qootalk.application.user.port.out;

import com.lrchan.qootalk.domain.user.vo.Token;

public interface TokenProvider {
    Token createAccessToken(String userPk, String role);
    Token createRefreshToken(String userPk);
}
