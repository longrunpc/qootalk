package com.lrchan.qootalk.domain.user;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;
import com.lrchan.qootalk.domain.user.vo.UserName;

public class User extends BaseModel {
    
    private Email email;
    private Password password;
    private UserName name;
    private ProfileImageUrl profileImageUrl;
    private StatusMessage statusMessage;
    private UserRole role;

    private User(
            Long id,
            Email email,
            Password password,
            UserName name,
            ProfileImageUrl profileImageUrl,
            StatusMessage statusMessage,
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

    public static User create(Email email, Password password, UserName name) {
        return new User(null, email, password, name, null, null, UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    // DB 복구 전용 메서드
    public static User reconstruct(Long id, Email email, Password password, UserName name, ProfileImageUrl profileImageUrl, StatusMessage statusMessage, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new User(id, email, password, name, profileImageUrl, statusMessage, role, createdAt, updatedAt, deletedAt);
    }

    public Email email() {
        return email;
    }

    public UserName name() {
        return name;
    }

    public Password password() {
        return password;
    }

    public ProfileImageUrl profileImageUrl() {
        return profileImageUrl;
    }

    public StatusMessage statusMessage() {
        return statusMessage;
    }

    public UserRole role() {
        return role;
    }

    public void changeName(UserName name) {
        this.name = name;
        update();
    }

    public void changeProfileImageUrl(ProfileImageUrl profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        update();
    }

    public void changeStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
        update();
    }
}
