package com.lrchan.qootalk.presentation.api.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.lrchan.qootalk.presentation.support.ApiIntegrationTestSupport;

class AuthControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    @DisplayName("회원가입에 성공한다")
    void signupSuccess() throws Exception {
        mockMvc.perform(post(AUTH_API_PREFIX + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new SignupRequest("tester@qootalk.com", "Password123!", "테스터"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.email").value("tester@qootalk.com"))
            .andExpect(jsonPath("$.data.name").value("테스터"))
            .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    @DisplayName("중복 이메일로 회원가입하면 실패한다")
    void signupFailsWhenEmailDuplicated() throws Exception {
        createUser("duplicated@qootalk.com", "Password123!", "중복유저");

        mockMvc.perform(post(AUTH_API_PREFIX + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new SignupRequest("duplicated@qootalk.com", "Password123!", "새유저"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("USER_002"))
            .andExpect(jsonPath("$.error.message").value("이미 존재하는 사용자입니다."));
    }

    @Test
    @DisplayName("로그인에 성공하면 토큰 쿠키를 내려준다")
    void loginSuccess() throws Exception {
        createUser("login@qootalk.com", "Password123!", "로그인유저");

        MvcResult result = mockMvc.perform(post(AUTH_API_PREFIX + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new LoginRequest("login@qootalk.com", "Password123!"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("login@qootalk.com"))
            .andExpect(cookie().exists("accessToken"))
            .andExpect(cookie().exists("refreshToken"))
            .andReturn();

        org.assertj.core.api.Assertions.assertThat(result.getResponse().getHeaders("Set-Cookie"))
            .hasSize(2)
            .anyMatch(value -> value.contains("accessToken="))
            .anyMatch(value -> value.contains("refreshToken="));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 실패한다")
    void loginFailsWhenPasswordInvalid() throws Exception {
        createUser("login-fail@qootalk.com", "Password123!", "로그인실패유저");

        mockMvc.perform(post(AUTH_API_PREFIX + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new LoginRequest("login-fail@qootalk.com", "WrongPassword123!"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("USER_001"))
            .andExpect(jsonPath("$.error.message").value("로그인에 실패했습니다."));
    }

    private record SignupRequest(String email, String password, String name) {
    }

    private record LoginRequest(String email, String password) {
    }
}
