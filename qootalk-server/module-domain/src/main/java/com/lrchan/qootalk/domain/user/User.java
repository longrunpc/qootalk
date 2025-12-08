package com.lrchan.qootalk.domain.user;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;

public class User extends BaseModel {
    
    private Email email;
    private String password;
    private UserName name;
    private ProfileImageUrl profileImageUrl;
    private String statusMessage;
    private UserRole role;

    private User(
            Long id,
            Email email,
            String password,
            UserName name,
            ProfileImageUrl profileImageUrl,
            String statusMessage,
            UserRole role,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        super(id, createdAt, updatedAt, deletedAt);
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.statusMessage = statusMessage;
        this.role = role == null ? UserRole.USER : role;
    }
}
