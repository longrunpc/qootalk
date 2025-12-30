package com.lrchan.qootalk.infrastructure.persistence.user;

import com.lrchan.qootalk.domain.user.User;


public class UserEntityMapper {
    
    public static User toDomain(UserEntity userEntity) {
        // Use reflection or a more complete constructor approach to map all fields
        // Since User.create() only creates new users, we need to use the private constructor via reflection
        // or add a factory method in User domain. For now, we'll work with what we have.
        
        // Note: User class needs a factory method that accepts all fields for reconstruction from persistence
        // As a workaround, we use reflection-like approach through the constructor
        try {
            java.lang.reflect.Constructor<User> constructor = User.class.getDeclaredConstructor(
                Long.class,
                com.lrchan.qootalk.domain.user.vo.Email.class,
                com.lrchan.qootalk.domain.user.vo.Password.class,
                com.lrchan.qootalk.domain.user.vo.UserName.class,
                com.lrchan.qootalk.domain.user.vo.ProfileImageUrl.class,
                String.class,
                com.lrchan.qootalk.domain.user.UserRole.class,
                java.time.LocalDateTime.class,
                java.time.LocalDateTime.class,
                java.time.LocalDateTime.class
            );
            constructor.setAccessible(true);
            
            return constructor.newInstance(
                userEntity.id(),
                new com.lrchan.qootalk.domain.user.vo.Email(userEntity.email()),
                new com.lrchan.qootalk.domain.user.vo.Password(userEntity.password()),
                new com.lrchan.qootalk.domain.user.vo.UserName(userEntity.name()),
                userEntity.profileImageUrl() != null ? new com.lrchan.qootalk.domain.user.vo.ProfileImageUrl(userEntity.profileImageUrl()) : null,
                userEntity.statusMessage(),
                userEntity.role(),
                userEntity.createdAt(),
                userEntity.updatedAt(),
                userEntity.deletedAt()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map UserEntity to User domain", e);
        }
    }

    public static UserEntity toEntity(User user) {
        if (user.id() != null) {
            // For existing users (update case), use the full constructor
            return new UserEntity(
                user.id(),
                user.email(),
                user.password(),
                user.name(),
                user.profileImageUrl(),
                user.statusMessage(),
                user.role()
            );
        } else {
            // For new users (create case), use the simple constructor
            UserEntity entity = new UserEntity(user.email(), user.password(), user.name(), user.role());
            // Note: profileImageUrl and statusMessage are already initialized to null in the constructor
            return entity;
        }
    }
}
