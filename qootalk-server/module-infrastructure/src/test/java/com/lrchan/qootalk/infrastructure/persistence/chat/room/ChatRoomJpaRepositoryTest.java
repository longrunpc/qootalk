package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.lrchan.qootalk.domain.chat.room.RoomType;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ChatRoomJpaRepositoryTest {

    @Autowired
    private ChatRoomJpaRepository chatRoomJpaRepository;

    @Test
    @DisplayName("ChatRoomEntity 저장 및 조회 테스트")
    void should_saveAndFind_when_validChatRoom() {
        // given
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .roomName("test_room")
            .roomType(RoomType.DIRECT)
            .createdBy(1L)
            .build();

        // when
        ChatRoomEntity savedChatRoomEntity = chatRoomJpaRepository.save(chatRoomEntity);

        // then
        assertThat(savedChatRoomEntity.getId()).isNotNull();
        assertThat(savedChatRoomEntity.getRoomName()).isEqualTo("test_room");
        assertThat(savedChatRoomEntity.getRoomType()).isEqualTo(RoomType.DIRECT);
        assertThat(savedChatRoomEntity.getCreatedBy()).isEqualTo(1L);
    }

    @Nested
    @DisplayName("방 이름 조회 테스트")
    class FindByRoomNameTest {
        @Test
        @DisplayName("방 이름 존재 시")
        void should_findByRoomName_when_validRoomName() {
            // given
            ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .roomName("test_room")
                .roomType(RoomType.DIRECT)
                .createdBy(1L)
                .build();
            chatRoomJpaRepository.save(chatRoomEntity);

            // when
            ChatRoomEntity foundChatRoomEntity = chatRoomJpaRepository.findByRoomName("test_room").orElseThrow();

            // then
            assertThat(foundChatRoomEntity.getId()).isNotNull();
            assertThat(foundChatRoomEntity.getRoomName()).isEqualTo("test_room");
            assertThat(foundChatRoomEntity.getRoomType()).isEqualTo(RoomType.DIRECT);
            assertThat(foundChatRoomEntity.getCreatedBy()).isEqualTo(1L);
        }

        @Test
        @DisplayName("방 이름 조회 실패 시 빈 값 반환")
        void should_returnEmpty_when_invalidRoomName() {
            // when & then
            assertThat(chatRoomJpaRepository.findByRoomName("invalid_room")).isEmpty();
        }
    }

    @Nested
    @DisplayName("방 이름 존재 여부 테스트")
    class ExistsByRoomNameTest {
        @Test
        @DisplayName("방 이름 존재 시")
        void should_existsByRoomName_when_validRoomName() {
            // given
            ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .roomName("test_room")
                .roomType(RoomType.DIRECT)
                .createdBy(1L)
                .build();
            chatRoomJpaRepository.save(chatRoomEntity);

            // when
            boolean exists = chatRoomJpaRepository.existsByRoomName("test_room");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("방 이름 존재하지 않을 시")
        void should_existsByRoomName_when_invalidRoomName() {
            // when
            boolean exists = chatRoomJpaRepository.existsByRoomName("invalid_room");

            // then
            assertThat(exists).isFalse();
        }
    }
}
