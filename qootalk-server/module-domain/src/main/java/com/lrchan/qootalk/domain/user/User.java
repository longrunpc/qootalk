package com.lrchan.qootalk.domain.user;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.UserName;

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

    public static User create(Email email, String password, UserName name) {
        return new User(null, email, password, name, null, "", UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public Email email() {
        return email;
    }

    public UserName name() {
        return name;
    }

    public ProfileImageUrl profileImageUrl() {
        return profileImageUrl;
    }

    public String statusMessage() {
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

    public void changeStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        update();
    }
}
