package com.lrchan.qootalk.presentation.api.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.lrchan.qootalk.infrastructure.persistence.user.UserEntity;
import com.lrchan.qootalk.presentation.support.ApiIntegrationTestSupport;

class UserProfileControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Nested
    @DisplayName("상태 메시지 수정")
    class UpdateStatusMessageTest {
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
        @DisplayName("상태 메시지가 너무 길면 수정에 실패한다")
        void updateStatusMessageFailsWhenTooLong() throws Exception {
            UserEntity user = createUser("status-long@qootalk.com", "Password123!", "긴상태유저");

            mockMvc.perform(authorized(patch(USER_API_PREFIX + "/status-message"), user)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new StatusMessageRequest("a".repeat(101)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("USER_007"))
                .andExpect(jsonPath("$.error.message").value("상태 메시지 형식이 올바르지 않습니다."));
        }
    }
    
    @Nested
    @DisplayName("프로필 이미지 업로드")
    class UploadProfileImageTest {
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

        @Test
        @DisplayName("프로필 이미지를 다시 업로드하면 기존 이미지가 교체된다")
        void uploadProfileImageReplacesExistingImage() throws Exception {
            UserEntity user = createUser("profile-replace@qootalk.com", "Password123!", "교체유저");

            String firstImageUrl = uploadProfileImage(user, "profile-1.png", "first-image");
            String secondImageUrl = uploadProfileImage(user, "profile-2.png", "second-image");

            org.assertj.core.api.Assertions.assertThat(secondImageUrl)
                .contains("qootalk-s3-local")
                .isNotEqualTo(firstImageUrl);
        }
    }

    @Nested
    @DisplayName("프로필 이미지 삭제")
    class DeleteProfileImageTest {
        @Test
        @DisplayName("현재 프로필 이미지 URL과 다른 값으로 삭제를 요청하면 실패한다")
        void deleteProfileImageFailsWhenUrlMismatch() throws Exception {
            UserEntity user = createUser("profile-mismatch@qootalk.com", "Password123!", "불일치유저");
            uploadProfileImage(user, "profile.png", "profile-image");

            mockMvc.perform(authorized(delete(USER_API_PREFIX + "/profile-image"), user)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new DeleteProfileImageRequest("https://example.com/another-image.png"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("USER_002"))
                .andExpect(jsonPath("$.error.message").value("프로필 이미지 URL이 일치하지 않습니다."));
        }

        @Test
        @DisplayName("프로필 이미지가 없으면 삭제 요청이 와도 그대로 성공한다")
        void deleteProfileImageSucceedsWhenImageDoesNotExist() throws Exception {
            UserEntity user = createUser("profile-empty@qootalk.com", "Password123!", "빈프로필유저");

            mockMvc.perform(authorized(delete(USER_API_PREFIX + "/profile-image"), user)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new DeleteProfileImageRequest("https://example.com/not-used.png"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.profileImageUrl").isEmpty());
        }
    }

    private String uploadProfileImage(UserEntity user, String fileName, String content) throws Exception {
        MvcResult uploadResult = mockMvc.perform(authorized(
                    multipart(USER_API_PREFIX + "/profile-image")
                        .file(multipartFile("file", fileName, MediaType.IMAGE_PNG_VALUE, content)),
                    user))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.profileImageUrl").value(containsString("qootalk-s3-local")))
            .andReturn();

        JsonNode uploadJson = objectMapper.readTree(responseBody(uploadResult));
        return uploadJson.path("data").path("profileImageUrl").asText();
    }

    private record StatusMessageRequest(String statusMessage) {
    }

    private record DeleteProfileImageRequest(String profileImageUrl) {
    }
}
