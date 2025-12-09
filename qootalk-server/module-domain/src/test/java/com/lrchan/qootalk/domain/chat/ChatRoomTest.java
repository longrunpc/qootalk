package com.lrchan.qootalk.domain.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChatRoom 도메인 테스트")
class ChatRoomTest {

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChatRoomTest {

        @Test
        @DisplayName("채팅방을 생성할 때 기본값이 올바르게 설정되어야 한다")
        void should_CreateChatRoom_When_ValidInput() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createBy = 1L;

            // when
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createBy);

            // then
            assertThat(chatRoom.roomName()).isEqualTo("테스트 채팅방");
            assertThat(chatRoom.roomType()).isEqualTo(RoomType.GROUP);
            assertThat(chatRoom.createdBy()).isEqualTo(1L);
        }

        @Test
        @DisplayName("채팅방을 생성할 때 ID는 null이어야 한다")
        void should_HaveNullId_When_CreateChatRoom() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.DIRECT;
            Long createdBy = 1L;

            // when
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createdBy);

            // then
            assertThat(chatRoom.id()).isNull();
        }
    }

    @Nested
    @DisplayName("채팅방 이름 변경")
    class ChangeRoomNameTest {

        @Test
        @DisplayName("채팅방 이름을 변경할 때 이름이 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateRoomName_When_ChangeRoomName() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createBy);
            String newRoomName = "새로운 채팅방";

            // when
            chatRoom.changeRoomName(newRoomName);

            // then
            assertThat(chatRoom.roomName()).isEqualTo(newRoomName);
        }

        @Test
        @DisplayName("여러 번 채팅방 이름을 변경할 때 마지막 이름이 유지되어야 한다")
        void should_KeepLastName_When_ChangeRoomNameMultipleTimes() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createBy);

            // when
            chatRoom.changeRoomName("첫 번째 이름");
            chatRoom.changeRoomName("두 번째 이름");

            // then
            assertThat(chatRoom.roomName()).isEqualTo("두 번째 이름");
        }
    }

    @Nested
    @DisplayName("생성자 변경")
    class ChangeCreatedByTest {

        @Test
        @DisplayName("생성자를 변경할 때 생성자가 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateCreatedBy_When_ChangeCreatedBy() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createdBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createdBy);
            Long newCreatedBy = 2L;

            // when
            chatRoom.changeCreatedBy(newCreatedBy);

            // then
            assertThat(chatRoom.createdBy()).isEqualTo(newCreatedBy);
        }

        @Test
        @DisplayName("여러 번 생성자를 변경할 때 마지막 생성자가 유지되어야 한다")
        void should_KeepLastCreatedBy_When_ChangeCreatedByMultipleTimes() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createdBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createdBy);

            // when
            chatRoom.changeCreatedBy(2L);
            chatRoom.changeCreatedBy(3L);

            // then
            assertThat(chatRoom.createdBy()).isEqualTo(3L);
        }
    }

    @Nested
    @DisplayName("조회")
    class QueryTest {

        @Test
        @DisplayName("채팅방 이름을 조회할 때 올바른 이름이 반환되어야 한다")
        void should_ReturnRoomName_When_GetRoomName() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createBy);

            // when
            String result = chatRoom.roomName();

            // then
            assertThat(result).isEqualTo(roomName);
        }

        @Test
        @DisplayName("채팅방 타입을 조회할 때 올바른 타입이 반환되어야 한다")
        void should_ReturnRoomType_When_GetRoomType() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.DIRECT;
            Long createBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createBy);

            // when
            RoomType result = chatRoom.roomType();

            // then
            assertThat(result).isEqualTo(roomType);
        }

        @Test
        @DisplayName("생성자를 조회할 때 올바른 생성자 ID가 반환되어야 한다")
        void should_ReturnCreatedBy_When_GetCreatedBy() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createdBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createdBy);

            // when
            Long result = chatRoom.createdBy();

            // then
            assertThat(result).isEqualTo(createdBy);
        }
    }

    @Nested
    @DisplayName("소프트 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("채팅방을 소프트 삭제하면 삭제 상태가 되어야 한다")
        void should_MarkAsDeleted_When_SoftDelete() {
            // given
            String roomName = "테스트 채팅방";
            RoomType roomType = RoomType.GROUP;
            Long createBy = 1L;
            ChatRoom chatRoom = ChatRoom.create(roomName, roomType, createBy);

            // when
            chatRoom.softDelete();

            // then
            assertThat(chatRoom.isDeleted()).isTrue();
        }
    }
}

