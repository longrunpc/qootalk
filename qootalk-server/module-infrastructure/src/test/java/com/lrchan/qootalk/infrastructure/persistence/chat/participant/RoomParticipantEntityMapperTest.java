package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;

@DisplayName("RoomParticipantEntityMapper 테스트")
class RoomParticipantEntityMapperTest {

    @Nested
    @DisplayName("도메인으로 변환")
    class ToDomainTest {

        @Test
        @DisplayName("RoomParticipantEntity를 RoomParticipant 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ValidEntity() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deletedAt = LocalDateTime.now();
            RoomParticipantEntity entity = new RoomParticipantEntity(
                1L,
                10L,
                20L,
                30L,
                RoomRole.ADMIN,
                now,
                now,
                deletedAt
            );

            // when
            RoomParticipant domain = RoomParticipantEntityMapper.toDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(1L);
            assertThat(domain.userId()).isEqualTo(10L);
            assertThat(domain.roomId()).isEqualTo(20L);
            assertThat(domain.lastReadMessageId()).isEqualTo(30L);
            assertThat(domain.role()).isEqualTo(RoomRole.ADMIN);
            assertThat(domain.createdAt()).isEqualTo(now);
            assertThat(domain.updatedAt()).isEqualTo(now);
            assertThat(domain.deletedAt()).isEqualTo(deletedAt);
            assertThat(domain.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("role이 null인 RoomParticipantEntity를 RoomParticipant 도메인으로 변환하면 기본값이 적용된다")
        void should_ConvertToDomain_When_RoleIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();
            RoomParticipantEntity entity = new RoomParticipantEntity(
                1L,
                10L,
                20L,
                30L,
                null,
                now,
                now,
                null
            );

            // when
            RoomParticipant domain = RoomParticipantEntityMapper.toDomain(entity);

            // then
            assertThat(domain.role()).isEqualTo(RoomRole.MEMBER);
            assertThat(domain.deletedAt()).isNull();
            assertThat(domain.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("엔티티로 변환")
    class ToEntityTest {

        @Test
        @DisplayName("RoomParticipant 도메인을 RoomParticipantEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_ValidDomain() {
            // given
            LocalDateTime now = LocalDateTime.now();
            RoomParticipant domain = RoomParticipant.reconstruct(
                1L,
                10L,
                20L,
                30L,
                RoomRole.OWNER,
                now,
                now,
                null
            );

            // when
            RoomParticipantEntity entity = RoomParticipantEntityMapper.toEntity(domain);

            // then
            assertThat(entity.id()).isNull(); // 생성자에서 id를 받지 않음
            assertThat(entity.userId()).isEqualTo(10L);
            assertThat(entity.roomId()).isEqualTo(20L);
            assertThat(entity.lastReadMessageId()).isEqualTo(30L);
            assertThat(entity.role()).isEqualTo(RoomRole.OWNER);
        }

        @Test
        @DisplayName("role이 null인 RoomParticipant 도메인을 RoomParticipantEntity로 변환하면 기본값이 적용된다")
        void should_ConvertToEntity_When_RoleIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();
            RoomParticipant domain = RoomParticipant.reconstruct(
                1L,
                10L,
                20L,
                30L,
                null,
                now,
                now,
                null
            );

            // when
            RoomParticipantEntity entity = RoomParticipantEntityMapper.toEntity(domain);

            // then
            assertThat(entity.role()).isEqualTo(RoomRole.MEMBER);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class RoundTripTest {

        @Test
        @DisplayName("RoomParticipant 도메인을 RoomParticipantEntity로 변환하고 다시 RoomParticipant 도메인으로 변환하면 동일한 값이 유지된다")
        void should_MaintainValues_When_RoundTrip() {
            // given
            LocalDateTime now = LocalDateTime.now();
            RoomParticipant original = RoomParticipant.reconstruct(
                1L,
                10L,
                20L,
                30L,
                RoomRole.ADMIN,
                now,
                now,
                null
            );

            // when
            RoomParticipantEntity entity = RoomParticipantEntityMapper.toEntity(original);
            RoomParticipant converted = RoomParticipantEntityMapper.toDomain(entity);

            // then
            assertThat(converted.userId()).isEqualTo(original.userId());
            assertThat(converted.roomId()).isEqualTo(original.roomId());
            assertThat(converted.lastReadMessageId()).isEqualTo(original.lastReadMessageId());
            assertThat(converted.role()).isEqualTo(original.role());
        }
    }
}

