package com.lrchan.qootalk.application.user.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.user.User;

public record UserQueryResult(
    Long id,
    String email,
    String name,
    String profileImageUrl,
    String statusMessage,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {
    public static UserQueryResult of(User user) {
        return new UserQueryResult(
            user.id(),
            user.email().value(),
            user.name().value(),
            user.profileImageUrl().value(),
            user.statusMessage().value(),
            user.role().name(),
            user.createdAt(),
            user.updatedAt(),
            user.deletedAt()
        );
    }
}
