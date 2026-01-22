package com.lrchan.qootalk.infrastructure.persistence.user;

import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;
import com.lrchan.qootalk.domain.user.vo.UserName;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;

public final class UserEntityMapper {

    private UserEntityMapper() {
    }

    public static User toDomain(UserEntity userEntity) {
        return User.reconstruct(
            userEntity.getId(),
            new Email(userEntity.getEmail()),
            new Password(userEntity.getPassword()),
            new UserName(userEntity.getName()),
            userEntity.getProfileImageUrl() != null ? new ProfileImageUrl(userEntity.getProfileImageUrl()) : null,
            new StatusMessage(userEntity.getStatusMessage()),
            userEntity.getRole(),
            userEntity.getCreatedAt(),
            userEntity.getUpdatedAt(),
            userEntity.getDeletedAt()
        );
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
            .id(user.id())
            .email(user.email().value())
            .password(user.password().encryptedPassword())
            .name(user.name().value())
            .profileImageUrl(user.profileImageUrl() != null ? user.profileImageUrl().value() : null)
            .statusMessage(user.statusMessage().value())
            .role(user.role())
            .createdAt(user.createdAt())
            .updatedAt(user.updatedAt())
            .deletedAt(user.deletedAt())
            .build();
    }
}
