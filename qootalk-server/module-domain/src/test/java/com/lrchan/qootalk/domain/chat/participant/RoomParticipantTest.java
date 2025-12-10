package com.lrchan.qootalk.domain.chat.participant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RoomParticipant 도메인 테스트")
class RoomParticipantTest {

    @Nested
    @DisplayName("유저 채팅방 생성")
    class CreateUserChatRoomTest {

        @Test
        @DisplayName("유저 채팅방을 생성할 때 기본값이 올바르게 설정되어야 한다")
        void should_CreateUserChatRoom_When_ValidInput() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;

            // when
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // then
            assertThat(userChatRoom.userId()).isEqualTo(1L);
            assertThat(userChatRoom.roomId()).isEqualTo(100L);
            assertThat(userChatRoom.lastReadMessageId()).isEqualTo(0L);
            assertThat(userChatRoom.role()).isEqualTo(RoomRole.MEMBER);
        }

        @Test
        @DisplayName("유저 채팅방을 생성할 때 역할이 null이면 기본값으로 MEMBER가 설정되어야 한다")
        void should_SetMemberAsDefaultRole_When_RoleIsNull() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = null;

            // when
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // then
            assertThat(userChatRoom.role()).isEqualTo(RoomRole.MEMBER);
        }

        @Test
        @DisplayName("유저 채팅방을 생성할 때 ID는 null이어야 한다")
        void should_HaveNullId_When_CreateUserChatRoom() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.OWNER;

            // when
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // then
            assertThat(userChatRoom.id()).isNull();
        }

        @Test
        @DisplayName("유저 채팅방을 생성할 때 userId가 null이면 예외가 발생해야 한다")
        void should_ThrowException_When_UserIdIsNull() {
            // given
            Long userId = null;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;

            // when & then
            assertThatThrownBy(() -> RoomParticipant.create(userId, roomId, lastReadMessageId, role))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User ID cannot be null");
        }

        @Test
        @DisplayName("유저 채팅방을 생성할 때 roomId가 null이면 예외가 발생해야 한다")
        void should_ThrowException_When_RoomIdIsNull() {
            // given
            Long userId = 1L;
            Long roomId = null;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;

            // when & then
            assertThatThrownBy(() -> RoomParticipant.create(userId, roomId, lastReadMessageId, role))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Room ID cannot be null");
        }
    }

    @Nested
    @DisplayName("역할 변경")
    class ChangeRoleTest {

        @Test
        @DisplayName("역할을 변경할 때 역할이 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateRole_When_ChangeRole() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);
            RoomRole newRole = RoomRole.ADMIN;

            // when
            userChatRoom.changeRole(newRole);

            // then
            assertThat(userChatRoom.role()).isEqualTo(newRole);
        }

        @Test
        @DisplayName("여러 번 역할을 변경할 때 마지막 역할이 유지되어야 한다")
        void should_KeepLastRole_When_ChangeRoleMultipleTimes() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            userChatRoom.changeRole(RoomRole.ADMIN);
            userChatRoom.changeRole(RoomRole.OWNER);

            // then
            assertThat(userChatRoom.role()).isEqualTo(RoomRole.OWNER);
        }

        @Test
        @DisplayName("역할을 null로 변경할 때 기본값으로 MEMBER가 설정되어야 한다")
        void should_SetMemberAsDefaultRole_When_ChangeRoleToNull() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.ADMIN;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            userChatRoom.changeRole(null);

            // then
            assertThat(userChatRoom.role()).isEqualTo(RoomRole.MEMBER);
        }
    }

    @Nested
    @DisplayName("조회")
    class QueryTest {

        @Test
        @DisplayName("사용자 ID를 조회할 때 올바른 사용자 ID가 반환되어야 한다")
        void should_ReturnUserId_When_GetUserId() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            Long result = userChatRoom.userId();

            // then
            assertThat(result).isEqualTo(userId);
        }

        @Test
        @DisplayName("채팅방 ID를 조회할 때 올바른 채팅방 ID가 반환되어야 한다")
        void should_ReturnRoomId_When_GetRoomId() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            Long result = userChatRoom.roomId();

            // then
            assertThat(result).isEqualTo(roomId);
        }

        @Test
        @DisplayName("마지막 읽은 메시지 ID를 조회할 때 올바른 메시지 ID가 반환되어야 한다")
        void should_ReturnLastReadMessageId_When_GetLastReadMessageId() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 50L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            Long result = userChatRoom.lastReadMessageId();

            // then
            assertThat(result).isEqualTo(lastReadMessageId);
        }

        @Test
        @DisplayName("역할을 조회할 때 올바른 역할이 반환되어야 한다")
        void should_ReturnRole_When_GetRole() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.OWNER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            RoomRole result = userChatRoom.role();

            // then
            assertThat(result).isEqualTo(role);
        }

        @Test
        @DisplayName("역할을 조회할 때 기본값으로 MEMBER가 반환되어야 한다")
        void should_ReturnMemberRole_When_RoleIsNull() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = null;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            RoomRole result = userChatRoom.role();

            // then
            assertThat(result).isEqualTo(RoomRole.MEMBER);
        }
    }

    @Nested
    @DisplayName("마지막 읽은 메시지 ID 변경")
    class ChangeLastReadMessageIdTest {

        @Test
        @DisplayName("마지막 읽은 메시지 ID를 변경할 때 ID가 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateLastReadMessageId_When_ChangeLastReadMessageId() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);
            Long newLastReadMessageId = 100L;

            // when
            userChatRoom.updateReadReceipt(newLastReadMessageId);

            // then
            assertThat(userChatRoom.lastReadMessageId()).isEqualTo(newLastReadMessageId);
        }

        @Test
        @DisplayName("여러 번 마지막 읽은 메시지 ID를 변경할 때 마지막 ID가 유지되어야 한다")
        void should_KeepLastMessageId_When_ChangeLastReadMessageIdMultipleTimes() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            userChatRoom.updateReadReceipt(50L);
            userChatRoom.updateReadReceipt(100L);

            // then
            assertThat(userChatRoom.lastReadMessageId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("소프트 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("유저 채팅방을 소프트 삭제하면 삭제 상태가 되어야 한다")
        void should_MarkAsDeleted_When_SoftDelete() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            Long lastReadMessageId = 0L;
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, lastReadMessageId, role);

            // when
            userChatRoom.softDelete();

            // then
            assertThat(userChatRoom.isDeleted()).isTrue();
        }
    }
}

