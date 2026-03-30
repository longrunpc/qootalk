package com.lrchan.qootalk.application.user.dto.result;

public record LoginResult(
    UserQueryResult user,
    TokenResponse token
) {
    public static LoginResult of(UserQueryResult user, TokenResponse token) {
        return new LoginResult(user, token);
    }
}
