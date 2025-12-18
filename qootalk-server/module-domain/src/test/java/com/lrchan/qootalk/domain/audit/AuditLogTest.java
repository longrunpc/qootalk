package com.lrchan.qootalk.domain.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuditLog 도메인 테스트")
class AuditLogTest {

    @Nested
    @DisplayName("AuditLog 생성")
    class CreateAuditLogTest {

        @Test
        @DisplayName("of 메서드로 AuditLog를 생성할 수 있다")
        void should_CreateAuditLog_When_UsingOf() {
            // given
            ActorType actorType = ActorType.USER;
            Long actorId = 1L;
            ActionType actionType = ActionType.USER_CREATED;
            TargetType targetType = TargetType.USER;
            Long targetId = 2L;
            String reason = "사용자 생성";

            // when
            AuditLog auditLog = AuditLog.of(actorType, actorId, actionType, targetType, targetId, reason);

            // then
            assertThat(auditLog).isNotNull();
        }

        @Test
        @DisplayName("simple 메서드로 AuditLog를 생성할 수 있다")
        void should_CreateAuditLog_When_UsingSimple() {
            // given
            ActorType actorType = ActorType.ADMIN;
            Long actorId = 10L;
            ActionType actionType = ActionType.MESSAGE_DELETED;
            TargetType targetType = TargetType.MESSAGE;
            Long targetId = 20L;

            // when
            AuditLog auditLog = AuditLog.simple(actorType, actorId, actionType, targetType, targetId);

            // then
            assertThat(auditLog).isNotNull();
        }

        @Test
        @DisplayName("actorType이 null이면 예외가 발생한다")
        void should_ThrowException_When_ActorTypeIsNull() {
            // when & then
            assertThatThrownBy(() -> AuditLog.of(null, 1L, ActionType.USER_CREATED, TargetType.USER, 1L, "reason"))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("actorId가 null이면 예외가 발생한다")
        void should_ThrowException_When_ActorIdIsNull() {
            // when & then
            assertThatThrownBy(() -> AuditLog.of(ActorType.USER, null, ActionType.USER_CREATED, TargetType.USER, 1L, "reason"))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("actionType이 null이면 예외가 발생한다")
        void should_ThrowException_When_ActionTypeIsNull() {
            // when & then
            assertThatThrownBy(() -> AuditLog.of(ActorType.USER, 1L, null, TargetType.USER, 1L, "reason"))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("targetType이 null이면 예외가 발생한다")
        void should_ThrowException_When_TargetTypeIsNull() {
            // when & then
            assertThatThrownBy(() -> AuditLog.of(ActorType.USER, 1L, ActionType.USER_CREATED, null, 1L, "reason"))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("targetId가 null이면 예외가 발생한다")
        void should_ThrowException_When_TargetIdIsNull() {
            // when & then
            assertThatThrownBy(() -> AuditLog.of(ActorType.USER, 1L, ActionType.USER_CREATED, TargetType.USER, null, "reason"))
                .isInstanceOf(NullPointerException.class);
        }
    }
}

