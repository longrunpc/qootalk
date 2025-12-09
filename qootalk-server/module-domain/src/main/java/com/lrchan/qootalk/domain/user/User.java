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

    public static User create(String email, String password, String name) {
        return new User(null, new Email(email), password, new UserName(name), null, "", UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public String email() {
        return email.value();
    }

    public String name() {
        return name.value();
    }

    public String profileImageUrl() {
        return profileImageUrl.value();
    }

    public String statusMessage() {
        return statusMessage;
    }

    public UserRole role() {
        return role;
    }

    public void changeName(String name) {
        this.name = new UserName(name);
        update();
    }

    public void changeProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = new ProfileImageUrl(profileImageUrl);
        update();
    }

    public void changeStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        update();
    }
}
