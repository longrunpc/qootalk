package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.lrchan.qootalk.domain.chat.message.MessageType;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MessageJpaRepositoryTest {

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
    }
}
