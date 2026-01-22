package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.domain.chat.vo.RoomName;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChatRoomEntityMapper 테스트")
public class ChatRoomEntityMapperTest {

    @Nested
    @DisplayName("도메인으로 변환")
    class ToDomainTest {
        @Test
        @DisplayName("ChatRoomEntity를 ChatRoom 도메인으로 변환할 때 기본값이 올바르게 설정되어야 한다")
        void should_ConvertToDomain_When_DefaultValues() {
            // given
            ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(1L)
                .roomName("test_room")
                .roomType(null)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
            ChatRoom chatRoom = ChatRoomEntityMapper.toDomain(chatRoomEntity);

            // then
            assertThat(chatRoom.id()).isEqualTo(1L);
            assertThat(chatRoom.roomName()).isEqualTo("test_room");
            assertThat(chatRoom.roomType()).isEqualTo(RoomType.DIRECT);
            assertThat(chatRoom.createdBy()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ChatRoomEntity를 ChatRoom 도메인으로 변환할 때 Entity의 업데이트 시간이 올바르게 설정되어야 한다")
        void should_ConvertToDomain_When_UpdatedAtIsSet() {
            // given
            ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(1L)
                .roomName("test_room")
                .roomType(RoomType.DIRECT)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
            ChatRoom chatRoom = ChatRoomEntityMapper.toDomain(chatRoomEntity);

            // then
            assertThat(chatRoom.createdAt()).isEqualTo(chatRoomEntity.createdAt());
            assertThat(chatRoom.updatedAt()).isEqualTo(chatRoomEntity.updatedAt());
            assertThat(chatRoom.deletedAt()).isNull();
        }

        @Test
        @DisplayName("ChatRoomEntity를 ChatRoom 도메인으로 변환할 때 Entity의 삭제 시간이 올바르게 설정되어야 한다")
        void should_ConvertToDomain_When_DeletedAtIsSet() {
            // given
            ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(1L)
                .roomName("test_room")
                .roomType(RoomType.DIRECT)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
            ChatRoom chatRoom = ChatRoomEntityMapper.toDomain(chatRoomEntity);

            // then
            assertThat(chatRoom.deletedAt()).isEqualTo(chatRoomEntity.deletedAt());
            assertThat(chatRoom.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("엔티티로 변환")
    class ToEntityTest {
        @Test
        @DisplayName("ChatRoom 도메인을 ChatRoomEntity로 변환할 때 기본값이 올바르게 설정되어야 한다")
        void should_ConvertToEntity_When_DefaultValues() {
            // given
            ChatRoom chatRoom = ChatRoom.create("test_room", RoomType.DIRECT, 1L);
            ChatRoomEntity chatRoomEntity = ChatRoomEntityMapper.toEntity(chatRoom);

            // then
            assertThat(chatRoomEntity.id()).isEqualTo(chatRoom.id());
            assertThat(chatRoomEntity.roomName()).isEqualTo(chatRoom.roomName());
            assertThat(chatRoomEntity.roomType()).isEqualTo(chatRoom.roomType());
            assertThat(chatRoomEntity.createdBy()).isEqualTo(chatRoom.createdBy());
        }

        @Test
        @DisplayName("ChatRoom 도메인을 ChatRoomEntity로 변환할 때 Entity의 업데이트 시간이 올바르게 설정되어야 한다")
        void should_ConvertToEntity_When_UpdatedAtIsSet() {
            // given
            LocalDateTime now = LocalDateTime.now();
            ChatRoom chatRoom = ChatRoom.reconstruct(1L, new RoomName("test_room"), RoomType.DIRECT, 1L, now, now, null);
            ChatRoomEntity chatRoomEntity = ChatRoomEntityMapper.toEntity(chatRoom);

            // then
            assertThat(chatRoomEntity.createdAt()).isEqualTo(chatRoom.createdAt());
            assertThat(chatRoomEntity.updatedAt()).isEqualTo(chatRoom.updatedAt());
            assertThat(chatRoomEntity.deletedAt()).isNull();
        }

        @Test
        @DisplayName("ChatRoom 도메인을 ChatRoomEntity로 변환할 때 Entity의 삭제 시간이 올바르게 설정되어야 한다")
        void should_ConvertToEntity_When_DeletedAtIsSet() {
            // given
            ChatRoom chatRoom = ChatRoom.reconstruct(1L, new RoomName("test_room"), RoomType.DIRECT, 1L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
            ChatRoomEntity chatRoomEntity = ChatRoomEntityMapper.toEntity(chatRoom);

            // then
            assertThat(chatRoomEntity.deletedAt()).isEqualTo(chatRoom.deletedAt());
            assertThat(chatRoomEntity.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class RoundTripTest {
        @Test
        @DisplayName("ChatRoom 도메인을 ChatRoomEntity로 변환하고 다시 ChatRoom 도메인으로 변환하면 동일한 값이 유지되어야 한다")
        void should_MaintainValues_When_RoundTrip() {
            // given
            LocalDateTime now = LocalDateTime.now();
            ChatRoom chatRoom = ChatRoom.reconstruct(1L, new RoomName("test_room"), RoomType.DIRECT, 1L, now, now, null);
            ChatRoomEntity chatRoomEntity = ChatRoomEntityMapper.toEntity(chatRoom);
            ChatRoom convertedChatRoom = ChatRoomEntityMapper.toDomain(chatRoomEntity);

            // then
            assertThat(convertedChatRoom.id()).isEqualTo(chatRoom.id());
            assertThat(convertedChatRoom.roomName()).isEqualTo(chatRoom.roomName());
            assertThat(convertedChatRoom.roomType()).isEqualTo(chatRoom.roomType());
            assertThat(convertedChatRoom.createdBy()).isEqualTo(chatRoom.createdBy());
            assertThat(convertedChatRoom.createdAt()).isEqualTo(chatRoom.createdAt());
            assertThat(convertedChatRoom.updatedAt()).isEqualTo(chatRoom.updatedAt());
            assertThat(convertedChatRoom.deletedAt()).isEqualTo(chatRoom.deletedAt());
        }
    }
}
