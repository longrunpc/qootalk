package com.lrchan.qootalk.domain.chat.participant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.chat.room.RoomRole;

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
            RoomRole role = RoomRole.MEMBER;

            // when
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

            // then
            assertThat(userChatRoom.userId()).isEqualTo(1L);
            assertThat(userChatRoom.roomId()).isEqualTo(100L);
            assertThat(userChatRoom.role()).isEqualTo(RoomRole.MEMBER);
        }

        @Test
        @DisplayName("유저 채팅방을 생성할 때 역할이 null이면 기본값으로 MEMBER가 설정되어야 한다")
        void should_SetMemberAsDefaultRole_When_RoleIsNull() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            RoomRole role = null;

            // when
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

            // then
            assertThat(userChatRoom.role()).isEqualTo(RoomRole.MEMBER);
        }

        @Test
        @DisplayName("유저 채팅방을 생성할 때 ID는 null이어야 한다")
        void should_HaveNullId_When_CreateUserChatRoom() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            RoomRole role = RoomRole.OWNER;

            // when
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

            // then
            assertThat(userChatRoom.id()).isNull();
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
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);
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
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

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
            RoomRole role = RoomRole.ADMIN;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

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
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

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
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

            // when
            Long result = userChatRoom.roomId();

            // then
            assertThat(result).isEqualTo(roomId);
        }

        @Test
        @DisplayName("역할을 조회할 때 올바른 역할이 반환되어야 한다")
        void should_ReturnRole_When_GetRole() {
            // given
            Long userId = 1L;
            Long roomId = 100L;
            RoomRole role = RoomRole.OWNER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

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
            RoomRole role = null;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

            // when
            RoomRole result = userChatRoom.role();

            // then
            assertThat(result).isEqualTo(RoomRole.MEMBER);
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
            RoomRole role = RoomRole.MEMBER;
            RoomParticipant userChatRoom = RoomParticipant.create(userId, roomId, role);

            // when
            userChatRoom.softDelete();

            // then
            assertThat(userChatRoom.isDeleted()).isTrue();
        }
    }
}

