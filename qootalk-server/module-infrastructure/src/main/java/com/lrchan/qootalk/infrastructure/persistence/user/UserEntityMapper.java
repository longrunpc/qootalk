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
            userEntity.id(),
            new Email(userEntity.email()),
            new Password(userEntity.password()),
            new UserName(userEntity.name()),
            userEntity.profileImageUrl() != null ? new ProfileImageUrl(userEntity.profileImageUrl()) : null,
            new StatusMessage(userEntity.statusMessage()),
            userEntity.role(),
            userEntity.createdAt(),
            userEntity.updatedAt(),
            userEntity.deletedAt()
        );
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
            user.id(), 
            user.email().value(),
            user.password().encryptedPassword(), 
            user.name().value(), 
            user.profileImageUrl() != null ? user.profileImageUrl().value() : null,
            user.statusMessage().value(),
            user.role(),
            user.createdAt(),
            user.updatedAt(),
            user.deletedAt()
        );
    }
}
