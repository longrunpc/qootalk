package com.lrchan.qootalk.infrastructure.persistence.user;

public class UserEntityMapper {
    
    public static User toDomain(UserEntity userEntity) {
        return User.create(userEntity.email(), userEntity.password(), userEntity.name());
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(user.email(), user.password(), user.name());
    }
}
