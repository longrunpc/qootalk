package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

@DisplayName("MessageEntityMapper 테스트")
class MessageEntityMapperTest {

    @Nested
    @DisplayName("도메인으로 변환")
    class ToDomainTest {

        @Test
        @DisplayName("MessageEntity를 Message 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ValidEntity() {
            // given
            LocalDateTime now = LocalDateTime.now();
            List<Long> mentions = Arrays.asList(1L, 2L, 3L);
            MessageEntity messageEntity = new MessageEntity(
                1L,
                10L,
                20L,
                "hello",
                MessageType.TEXT,
                mentions,
                null,
                now,
                now,
                null
            );

            // when
            Message message = MessageEntityMapper.toDomain(messageEntity);

            // then
            assertThat(message.id()).isEqualTo(1L);
            assertThat(message.roomId()).isEqualTo(10L);
            assertThat(message.userId()).isEqualTo(20L);
            assertThat(message.content()).isEqualTo("hello");
            assertThat(message.messageType()).isEqualTo(MessageType.TEXT);
            assertThat(message.mentions()).containsExactlyElementsOf(mentions);
            assertThat(message.parentMessageId()).isNull();
            assertThat(message.createdAt()).isEqualTo(now);
            assertThat(message.updatedAt()).isEqualTo(now);
            assertThat(message.deletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("엔티티로 변환")
    class ToEntityTest {

        @Test
        @DisplayName("Message 도메인을 MessageEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_ValidDomain() {
            // given
            LocalDateTime now = LocalDateTime.now();
            List<Long> mentions = Arrays.asList(1L, 2L, 3L);
            Message message = Message.reconstruct(
                1L,
                10L,
                20L,
                "hello",
                MessageType.TEXT,
                mentions,
                100L,
                now,
                now,
                null
            );

            // when
            MessageEntity messageEntity = MessageEntityMapper.toEntity(message);

            // then
            assertThat(messageEntity.id()).isEqualTo(1L);
            assertThat(messageEntity.roomId()).isEqualTo(10L);
            assertThat(messageEntity.userId()).isEqualTo(20L);
            assertThat(messageEntity.content()).isEqualTo("hello");
            assertThat(messageEntity.messageType()).isEqualTo(MessageType.TEXT);
            assertThat(messageEntity.mentions()).containsExactlyElementsOf(mentions);
            assertThat(messageEntity.parentMessageId()).isEqualTo(100L);
            assertThat(messageEntity.createdAt()).isEqualTo(now);
            assertThat(messageEntity.updatedAt()).isEqualTo(now);
            assertThat(messageEntity.deletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class RoundTripTest {

        @Test
        @DisplayName("Message 도메인을 MessageEntity로 변환하고 다시 Message 도메인으로 변환하면 동일한 값이 유지된다")
        void should_MaintainValues_When_RoundTrip() {
            // given
            LocalDateTime now = LocalDateTime.now();
            List<Long> mentions = Arrays.asList(1L, 2L);
            Message original = Message.reconstruct(
                1L,
                10L,
                20L,
                "hello",
                MessageType.TEXT,
                mentions,
                100L,
                now,
                now,
                null
            );

            // when
            MessageEntity messageEntity = MessageEntityMapper.toEntity(original);
            Message converted = MessageEntityMapper.toDomain(messageEntity);

            // then
            assertThat(converted.id()).isEqualTo(original.id());
            assertThat(converted.roomId()).isEqualTo(original.roomId());
            assertThat(converted.userId()).isEqualTo(original.userId());
            assertThat(converted.content()).isEqualTo(original.content());
            assertThat(converted.messageType()).isEqualTo(original.messageType());
            assertThat(converted.mentions()).containsExactlyElementsOf(original.mentions());
            assertThat(converted.parentMessageId()).isEqualTo(original.parentMessageId());
            assertThat(converted.createdAt()).isEqualTo(original.createdAt());
            assertThat(converted.updatedAt()).isEqualTo(original.updatedAt());
            assertThat(converted.deletedAt()).isEqualTo(original.deletedAt());
        }
    }
}

