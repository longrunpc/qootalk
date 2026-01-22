package com.lrchan.qootalk.infrastructure.persistence.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.audit.ActionType;
import com.lrchan.qootalk.domain.audit.ActorType;
import com.lrchan.qootalk.domain.audit.AuditLog;
import com.lrchan.qootalk.domain.audit.TargetType;

@DisplayName("AuditLogEntityMapper 테스트")
class AuditLogEntityMapperTest {

    @Nested
    @DisplayName("도메인으로 변환")
    class ToDomainTest {

        @Test
        @DisplayName("AuditLogEntity를 AuditLog 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ValidEntity() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLogEntity entity = AuditLogEntity.builder()
                    .id(1L)
                    .actorType(ActorType.USER)
                    .actorId(100L)
                    .actionType(ActionType.USER_CREATED)
                    .targetType(TargetType.USER)
                    .targetId(200L)
                    .reason("사용자 생성")
                    .occurredAt(occurredAt)
                    .build();

            // when
            AuditLog domain = AuditLogEntityMapper.toDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(1L);
            assertThat(domain.actorType()).isEqualTo(ActorType.USER);
            assertThat(domain.actorId()).isEqualTo(100L);
            assertThat(domain.actionType()).isEqualTo(ActionType.USER_CREATED);
            assertThat(domain.targetType()).isEqualTo(TargetType.USER);
            assertThat(domain.targetId()).isEqualTo(200L);
            assertThat(domain.reason()).isEqualTo("사용자 생성");
            assertThat(domain.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("reason이 null인 AuditLogEntity를 AuditLog 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ReasonIsNull() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLogEntity entity = AuditLogEntity.builder()
                    .id(1L)
                    .actorType(ActorType.ADMIN)
                    .actorId(50L)
                    .actionType(ActionType.MESSAGE_DELETED)
                    .targetType(TargetType.MESSAGE)
                    .targetId(300L)
                    .reason(null)
                    .occurredAt(occurredAt)
                    .build();

            // when
            AuditLog domain = AuditLogEntityMapper.toDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(1L);
            assertThat(domain.actorType()).isEqualTo(ActorType.ADMIN);
            assertThat(domain.reason()).isNull();
            assertThat(domain.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("SYSTEM 액터 타입의 AuditLogEntity를 AuditLog 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ActorTypeIsSystem() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLogEntity entity = AuditLogEntity.builder()
                    .id(1L)
                    .actorType(ActorType.SYSTEM)
                    .actorId(0L)
                    .actionType(ActionType.ROOM_CREATED)
                    .targetType(TargetType.ROOM)
                    .targetId(400L)
                    .reason("시스템 자동 생성")
                    .occurredAt(occurredAt)
                    .build();

            // when
            AuditLog domain = AuditLogEntityMapper.toDomain(entity);

            // then
            assertThat(domain.actorType()).isEqualTo(ActorType.SYSTEM);
            assertThat(domain.actorId()).isEqualTo(0L);
            assertThat(domain.actionType()).isEqualTo(ActionType.ROOM_CREATED);
            assertThat(domain.targetType()).isEqualTo(TargetType.ROOM);
        }
    }

    @Nested
    @DisplayName("엔티티로 변환")
    class ToEntityTest {

        @Test
        @DisplayName("AuditLog 도메인을 AuditLogEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_ValidDomain() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLog domain = AuditLog.reconstruct(
                    1L,
                    ActorType.USER,
                    100L,
                    ActionType.USER_UPDATED,
                    TargetType.USER,
                    200L,
                    "사용자 정보 수정",
                    occurredAt
            );

            // when
            AuditLogEntity entity = AuditLogEntityMapper.toEntity(domain);

            // then
            assertThat(entity.id()).isEqualTo(1L);
            assertThat(entity.actorType()).isEqualTo(ActorType.USER);
            assertThat(entity.actorId()).isEqualTo(100L);
            assertThat(entity.actionType()).isEqualTo(ActionType.USER_UPDATED);
            assertThat(entity.targetType()).isEqualTo(TargetType.USER);
            assertThat(entity.targetId()).isEqualTo(200L);
            assertThat(entity.reason()).isEqualTo("사용자 정보 수정");
            assertThat(entity.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("reason이 null인 AuditLog 도메인을 AuditLogEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_ReasonIsNull() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLog domain = AuditLog.reconstruct(
                    1L,
                    ActorType.ADMIN,
                    50L,
                    ActionType.ROOM_DELETED,
                    TargetType.ROOM,
                    300L,
                    null,
                    occurredAt
            );

            // when
            AuditLogEntity entity = AuditLogEntityMapper.toEntity(domain);

            // then
            assertThat(entity.id()).isEqualTo(1L);
            assertThat(entity.actorType()).isEqualTo(ActorType.ADMIN);
            assertThat(entity.reason()).isNull();
            assertThat(entity.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("다양한 ActionType을 가진 AuditLog 도메인을 AuditLogEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_DifferentActionTypes() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLog domain = AuditLog.reconstruct(
                    1L,
                    ActorType.USER,
                    100L,
                    ActionType.ROOM_PARTICIPANT_ADDED,
                    TargetType.ROOM,
                    400L,
                    "참가자 추가",
                    occurredAt
            );

            // when
            AuditLogEntity entity = AuditLogEntityMapper.toEntity(domain);

            // then
            assertThat(entity.actionType()).isEqualTo(ActionType.ROOM_PARTICIPANT_ADDED);
            assertThat(entity.targetType()).isEqualTo(TargetType.ROOM);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class RoundTripTest {

        @Test
        @DisplayName("AuditLog 도메인을 AuditLogEntity로 변환하고 다시 AuditLog 도메인으로 변환하면 동일한 값이 유지된다")
        void should_MaintainValues_When_RoundTrip() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLog original = AuditLog.reconstruct(
                    1L,
                    ActorType.USER,
                    100L,
                    ActionType.MESSAGE_SENT,
                    TargetType.MESSAGE,
                    500L,
                    "메시지 전송",
                    occurredAt
            );

            // when
            AuditLogEntity entity = AuditLogEntityMapper.toEntity(original);
            AuditLog converted = AuditLogEntityMapper.toDomain(entity);

            // then
            assertThat(converted.id()).isEqualTo(original.id());
            assertThat(converted.actorType()).isEqualTo(original.actorType());
            assertThat(converted.actorId()).isEqualTo(original.actorId());
            assertThat(converted.actionType()).isEqualTo(original.actionType());
            assertThat(converted.targetType()).isEqualTo(original.targetType());
            assertThat(converted.targetId()).isEqualTo(original.targetId());
            assertThat(converted.reason()).isEqualTo(original.reason());
            assertThat(converted.occurredAt()).isEqualTo(original.occurredAt());
        }

        @Test
        @DisplayName("reason이 null인 AuditLog 도메인을 양방향 변환해도 올바르게 처리된다")
        void should_HandleNullReason_When_RoundTrip() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLog original = AuditLog.reconstruct(
                    1L,
                    ActorType.SYSTEM,
                    0L,
                    ActionType.ROLE_CHANGED,
                    TargetType.USER,
                    600L,
                    null,
                    occurredAt
            );

            // when
            AuditLogEntity entity = AuditLogEntityMapper.toEntity(original);
            AuditLog converted = AuditLogEntityMapper.toDomain(entity);

            // then
            assertThat(converted.reason()).isNull();
            assertThat(converted.actorType()).isEqualTo(ActorType.SYSTEM);
            assertThat(converted.actionType()).isEqualTo(ActionType.ROLE_CHANGED);
        }

        @Test
        @DisplayName("모든 ActorType과 ActionType 조합을 양방향 변환해도 올바르게 처리된다")
        void should_HandleAllActorAndActionTypes_When_RoundTrip() {
            // given
            LocalDateTime occurredAt = LocalDateTime.now();
            AuditLog original = AuditLog.reconstruct(
                    1L,
                    ActorType.ADMIN,
                    10L,
                    ActionType.ROOM_PARTICIPANT_KICKED,
                    TargetType.ROOM,
                    700L,
                    "관리자에 의한 강제 퇴장",
                    occurredAt
            );

            // when
            AuditLogEntity entity = AuditLogEntityMapper.toEntity(original);
            AuditLog converted = AuditLogEntityMapper.toDomain(entity);

            // then
            assertThat(converted.actorType()).isEqualTo(ActorType.ADMIN);
            assertThat(converted.actionType()).isEqualTo(ActionType.ROOM_PARTICIPANT_KICKED);
            assertThat(converted.targetType()).isEqualTo(TargetType.ROOM);
            assertThat(converted.reason()).isEqualTo("관리자에 의한 강제 퇴장");
        }
    }
}
