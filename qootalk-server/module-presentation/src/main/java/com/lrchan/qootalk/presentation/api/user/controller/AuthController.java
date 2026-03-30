package com.lrchan.qootalk.presentation.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lrchan.qootalk.application.user.dto.result.LoginResult;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.in.LoginUserUseCase;
import com.lrchan.qootalk.application.user.port.in.RegisterUserUseCase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.presentation.api.user.dto.request.UserLoginRequest;
import com.lrchan.qootalk.presentation.api.user.dto.request.UserSignupRequest;
import com.lrchan.qootalk.presentation.api.user.dto.response.UserLoginResponse;
import com.lrchan.qootalk.presentation.api.user.dto.response.UserSignupResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    @PostMapping("/signup")
    @Operation(
        summary = "회원가입",
        description = "이메일, 비밀번호, 이름으로 새 사용자를 생성합니다."
    )
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody UserSignupRequest request) {
        UserQueryResult result = registerUserUseCase.register(request.toCommand());
        return ResponseEntity.ok(ApiResponse.of(UserSignupResponse.of(result)));
    }

    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "이메일, 비밀번호로 사용자를 로그인합니다."
    )
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(
      @RequestBody UserLoginRequest request,
      HttpServletRequest httpServletRequest
    ) {
        LoginResult result = loginUserUseCase.login(request.toCommand());

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", result.token().accessToken().token())
            .maxAge(result.token().accessToken().expiresIn())
            .path("/")
            .secure(true)
            .httpOnly(true)
            .sameSite("Lax")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.token().refreshToken().token())
            .maxAge(result.token().refreshToken().expiresIn())
            .path("/")
            .secure(true)
            .httpOnly(true)
            .sameSite("Lax")
            .build();

        return ResponseEntity.ok()
            .header("Set-Cookie", accessCookie.toString())
            .header("Set-Cookie", refreshCookie.toString())
            .body(ApiResponse.of(UserLoginResponse.of(result)));
    }
}
