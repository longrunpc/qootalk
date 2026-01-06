package com.lrchan.qootalk.infrastructure.persistence.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.user.UserRole;
import com.lrchan.qootalk.infrastructure.persistence.common.BaseEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class UserEntity extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "status_message")
    private String statusMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    protected UserEntity() {
    }

    public UserEntity(String email, String password, String name, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.profileImageUrl = null;
        this.statusMessage = null;
    }

    public UserEntity(Long id, String email, String password, String name,
                      String profileImageUrl, String statusMessage, UserRole role,
                      LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.statusMessage = statusMessage;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    public String name() {
        return name;
    }

    public String profileImageUrl() {
        return profileImageUrl;
    }

    public String statusMessage() {
        return statusMessage;
    }

    public UserRole role() {
        return role;
    }
}
