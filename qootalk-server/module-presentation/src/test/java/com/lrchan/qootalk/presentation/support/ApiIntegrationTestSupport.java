package com.lrchan.qootalk.presentation.support;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrchan.qootalk.domain.user.UserRole;
import com.lrchan.qootalk.infrastructure.IntegrationTestSupport;
import com.lrchan.qootalk.infrastructure.persistence.chat.message.MessageJpaRepository;
import com.lrchan.qootalk.infrastructure.persistence.chat.participant.RoomParticipantEntity;
import com.lrchan.qootalk.infrastructure.persistence.chat.participant.RoomParticipantJpaRepository;
import com.lrchan.qootalk.infrastructure.persistence.user.UserEntity;
import com.lrchan.qootalk.infrastructure.persistence.user.UserJpaRepository;
import com.lrchan.qootalk.infrastructure.security.provider.JwtTokenGenerator;

import org.junit.jupiter.api.AfterEach;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@AutoConfigureMockMvc
public abstract class ApiIntegrationTestSupport extends IntegrationTestSupport {

    protected static final String AUTH_API_PREFIX = "/api/v1/auth";
    protected static final String USER_API_PREFIX = "/api/v1/api/v1/users/me";
    protected static final String CHAT_ROOM_API_PREFIX = "/api/v1/api/v1/chat-rooms";
    protected static final String FILE_API_PREFIX = "/api/v1/api/v1/files";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected RedisConnectionFactory redisConnectionFactory;

    @Autowired
    protected S3Client s3Client;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    protected UserJpaRepository userJpaRepository;

    @Autowired
    protected RoomParticipantJpaRepository roomParticipantJpaRepository;

    @Autowired
    protected MessageJpaRepository messageJpaRepository;

    @AfterEach
    void tearDownIntegrationState() {
        cleanDatabase();
        cleanRedis();
        cleanS3();
    }

    protected UserEntity createUser(String email, String rawPassword, String name) {
        UserEntity user = UserEntity.builder()
            .email(email)
            .password(passwordEncoder.encode(rawPassword))
            .name(name)
            .role(UserRole.USER)
            .build();
        return userJpaRepository.saveAndFlush(user);
    }

    protected String accessToken(UserEntity user) {
        return jwtTokenGenerator.createAccessToken(String.valueOf(user.getId()), user.getRole().name()).token();
    }

    protected MockHttpServletRequestBuilder authorized(MockHttpServletRequestBuilder builder, UserEntity user) {
        return builder.header("Authorization", "Bearer " + accessToken(user));
    }

    protected String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON serialization failed", e);
        }
    }

    protected MockMultipartFile multipartFile(String name, String originalFilename, String contentType, String content) {
        return new MockMultipartFile(
            name,
            originalFilename,
            contentType,
            content.getBytes(StandardCharsets.UTF_8)
        );
    }

    protected Long lastReadMessageId(Long userId, Long roomId) {
        RoomParticipantEntity participant = roomParticipantJpaRepository.findByUserIdAndRoomId(userId, roomId)
            .orElseThrow();
        return participant.getLastReadMessageId();
    }

    protected String responseBody(org.springframework.test.web.servlet.MvcResult result) {
        try {
            return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("Failed to read response body", e);
        }
    }

    private void cleanDatabase() {
        jdbcTemplate.execute("""
            TRUNCATE TABLE
                audit_logs,
                file_attachments,
                message_mentions,
                messages,
                room_participants,
                chat_rooms,
                users
            RESTART IDENTITY CASCADE
            """);
    }

    private void cleanRedis() {
        if (redisConnectionFactory.getConnection() != null) {
            redisConnectionFactory.getConnection().serverCommands().flushAll();
        }
    }

    private void cleanS3() {
        List<String> objectKeys = s3Client.listObjectsV2(
            ListObjectsV2Request.builder()
                .bucket("qootalk-s3-local")
                .build()
        ).contents().stream().map(item -> item.key()).toList();

        for (String key : objectKeys) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket("qootalk-s3-local")
                .key(key)
                .build());
        }
    }
}
