package com.lrchan.qootalk.application.user.port.out;

public interface TokenProvider {
    String createAccessToken(String userPk, String role);
    String createRefreshToken(String userPk);
}
