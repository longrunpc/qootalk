package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.infrastructure.PostgresDBIntegrationTestSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class RoomParticipantJpaRepositoryTest extends PostgresDBIntegrationTestSupport {

    @Autowired
    private RoomParticipantJpaRepository roomParticipantJpaRepository;

    @Test
    @DisplayName("RoomParticipantEntity 저장 및 조회 테스트")
    void should_saveAndFind_when_validParticipant() {
        // given
        RoomParticipantEntity participantEntity = RoomParticipantEntity.builder()
            .userId(1L)
            .roomId(10L)
            .lastReadMessageId(100L)
            .role(RoomRole.MEMBER)
            .build();

        // when
        RoomParticipantEntity savedEntity = roomParticipantJpaRepository.save(participantEntity);

        // then
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getUserId()).isEqualTo(1L);
        assertThat(savedEntity.getRoomId()).isEqualTo(10L);
        assertThat(savedEntity.getLastReadMessageId()).isEqualTo(100L);
        assertThat(savedEntity.getRole()).isEqualTo(RoomRole.MEMBER);
        assertThat(savedEntity.getCreatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isNotNull();
    }

    @Nested
    @DisplayName("사용자/방 조회 테스트")
    class FindByUserIdAndRoomIdTest {
        @Test
        @DisplayName("사용자/방 존재 시")
        void should_findByUserIdAndRoomId_when_validIds() {
            // given
            RoomParticipantEntity participantEntity = RoomParticipantEntity.builder()
                .userId(1L)
                .roomId(10L)
                .lastReadMessageId(100L)
                .role(RoomRole.MEMBER)
                .build();
            roomParticipantJpaRepository.save(participantEntity);

            // when
            RoomParticipantEntity foundEntity = roomParticipantJpaRepository
                .findByUserIdAndRoomId(1L, 10L)
                .orElseThrow();

            // then
            assertThat(foundEntity.getId()).isNotNull();
            assertThat(foundEntity.getUserId()).isEqualTo(1L);
            assertThat(foundEntity.getRoomId()).isEqualTo(10L);
            assertThat(foundEntity.getLastReadMessageId()).isEqualTo(100L);
            assertThat(foundEntity.getRole()).isEqualTo(RoomRole.MEMBER);
        }

        @Test
        @DisplayName("사용자/방 조회 실패 시 빈 값 반환")
        void should_returnEmpty_when_invalidIds() {
            // when & then
            assertThat(roomParticipantJpaRepository.findByUserIdAndRoomId(1L, 999L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("사용자/방 존재 여부 테스트")
    class ExistsByUserIdAndRoomIdTest {
        @Test
        @DisplayName("사용자/방 존재 시")
        void should_existsByUserIdAndRoomId_when_validIds() {
            // given
            RoomParticipantEntity participantEntity = RoomParticipantEntity.builder()
                .userId(1L)
                .roomId(10L)
                .lastReadMessageId(100L)
                .role(RoomRole.MEMBER)
                .build();
            roomParticipantJpaRepository.save(participantEntity);

            // when
            boolean exists = roomParticipantJpaRepository.existsByUserIdAndRoomId(1L, 10L);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("사용자/방 존재하지 않을 시")
        void should_existsByUserIdAndRoomId_when_invalidIds() {
            // when
            boolean exists = roomParticipantJpaRepository.existsByUserIdAndRoomId(1L, 999L);

            // then
            assertThat(exists).isFalse();
        }
    }
}
