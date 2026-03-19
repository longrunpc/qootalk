package com.lrchan.qootalk.presentation.api.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.lrchan.qootalk.infrastructure.persistence.user.UserEntity;
import com.lrchan.qootalk.presentation.support.ApiIntegrationTestSupport;

class UserProfileControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    @DisplayName("상태 메시지를 수정할 수 있다")
    void updateStatusMessageSuccess() throws Exception {
        UserEntity user = createUser("status@qootalk.com", "Password123!", "상태유저");

        mockMvc.perform(authorized(patch(USER_API_PREFIX + "/status-message"), user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new StatusMessageRequest("집중 모드"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(user.getId()))
            .andExpect(jsonPath("$.data.statusMessage").value("집중 모드"));
    }

    @Test
    @DisplayName("인증 없이 상태 메시지를 수정하면 실패한다")
    void updateStatusMessageFailsWithoutAuthentication() throws Exception {
        mockMvc.perform(patch(USER_API_PREFIX + "/status-message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new StatusMessageRequest("집중 모드"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("GLOBAL_003"));
    }

    @Test
    @DisplayName("프로필 이미지를 업로드하고 삭제할 수 있다")
    void uploadAndDeleteProfileImage() throws Exception {
        UserEntity user = createUser("profile@qootalk.com", "Password123!", "프로필유저");

        MvcResult uploadResult = mockMvc.perform(authorized(
                    multipart(USER_API_PREFIX + "/profile-image")
                        .file(multipartFile("file", "profile.png", MediaType.IMAGE_PNG_VALUE, "profile-image")),
                    user))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.profileImageUrl").value(containsString("qootalk-s3-local")))
            .andReturn();

        JsonNode uploadJson = objectMapper.readTree(responseBody(uploadResult));
        String profileImageUrl = uploadJson.path("data").path("profileImageUrl").asText();

        mockMvc.perform(authorized(delete(USER_API_PREFIX + "/profile-image"), user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new DeleteProfileImageRequest(profileImageUrl))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(user.getId()))
            .andExpect(jsonPath("$.data.profileImageUrl").isEmpty());
    }

    private record StatusMessageRequest(String statusMessage) {
    }

    private record DeleteProfileImageRequest(String profileImageUrl) {
    }
}
