package com.lrchan.qootalk.application.user.dto.result;

import java.time.LocalDateTime;

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
}
