package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.infrastructure.IntegrationTestSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class MessageJpaRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MessageJpaRepository messageJpaRepository;

    @Test
    @DisplayName("MessageEntity 저장 및 조회 테스트")
    void should_saveAndFind_when_validMessage() {
        // given
        MessageEntity messageEntity = MessageEntity.builder()
            .roomId(1L)
            .userId(2L)
            .content("안녕하세요")
            .messageType(MessageType.TEXT)
            .build();

        // when
        MessageEntity savedMessageEntity = messageJpaRepository.save(messageEntity);

        // then
        assertThat(savedMessageEntity.getId()).isNotNull();
        assertThat(savedMessageEntity.getRoomId()).isEqualTo(1L);
        assertThat(savedMessageEntity.getUserId()).isEqualTo(2L);
        assertThat(savedMessageEntity.getContent()).isEqualTo("안녕하세요");
        assertThat(savedMessageEntity.getMessageType()).isEqualTo(MessageType.TEXT);
        assertThat(savedMessageEntity.getCreatedAt()).isNotNull();
        assertThat(savedMessageEntity.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("대량 메시지 환경에서도 roomId와 fromMessageId 기준 Slice 조회가 가능하다")
    void should_sliceMessages_when_largeHistoryExists() {
        for (int i = 1; i <= 120; i++) {
            messageJpaRepository.save(MessageEntity.builder()
                .roomId(10L)
                .userId(2L)
                .content("message-" + i)
                .messageType(MessageType.TEXT)
                .build());
        }

        Slice<MessageEntity> firstSlice = messageJpaRepository.findSliceByRoomId(10L, null, PageRequest.of(0, 20));

        assertThat(firstSlice.getContent()).hasSize(20);
        assertThat(firstSlice.hasNext()).isTrue();
        assertThat(firstSlice.getContent().get(0).getContent()).isEqualTo("message-120");
        assertThat(firstSlice.getContent().get(19).getContent()).isEqualTo("message-101");

        Long fromMessageId = firstSlice.getContent().get(19).getId();

        Slice<MessageEntity> secondSlice = messageJpaRepository.findSliceByRoomId(10L, fromMessageId, PageRequest.of(0, 20));

        assertThat(secondSlice.getContent()).hasSize(20);
        assertThat(secondSlice.hasNext()).isTrue();
        assertThat(secondSlice.getContent().get(0).getContent()).isEqualTo("message-100");
        assertThat(secondSlice.getContent().get(19).getContent()).isEqualTo("message-81");
    }
}
