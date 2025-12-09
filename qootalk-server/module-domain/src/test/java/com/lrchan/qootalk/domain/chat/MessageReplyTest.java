package com.lrchan.qootalk.domain.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MessageReply 도메인 테스트")
class MessageReplyTest {

    @Nested
    @DisplayName("메시지 답글 생성")
    class CreateMessageReplyTest {

        @Test
        @DisplayName("메시지 답글을 생성할 때 기본값이 올바르게 설정되어야 한다")
        void should_CreateMessageReply_When_ValidInput() {
            // given
            Long messageId = 100L;
            Long parentMessageId = 200L;

            // when
            MessageReply messageReply = MessageReply.create(messageId, parentMessageId);

            // then
            assertThat(messageReply.messageId()).isEqualTo(100L);
            assertThat(messageReply.parentMessageId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("메시지 답글을 생성할 때 ID는 null이어야 한다")
        void should_HaveNullId_When_CreateMessageReply() {
            // given
            Long messageId = 100L;
            Long parentMessageId = 200L;

            // when
            MessageReply messageReply = MessageReply.create(messageId, parentMessageId);

            // then
            assertThat(messageReply.id()).isNull();
        }

        @Test
        @DisplayName("messageId가 null이면 예외가 발생해야 한다")
        void should_ThrowException_When_MessageIdIsNull() {
            // given
            Long messageId = null;
            Long parentMessageId = 200L;

            // when & then
            assertThatThrownBy(() -> MessageReply.create(messageId, parentMessageId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Message ID cannot be null");
        }

        @Test
        @DisplayName("parentMessageId가 null이면 예외가 발생해야 한다")
        void should_ThrowException_When_ParentMessageIdIsNull() {
            // given
            Long messageId = 100L;
            Long parentMessageId = null;

            // when & then
            assertThatThrownBy(() -> MessageReply.create(messageId, parentMessageId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parent message ID cannot be null");
        }

        @Test
        @DisplayName("messageId와 parentMessageId가 같으면 예외가 발생해야 한다")
        void should_ThrowException_When_MessageIdEqualsParentMessageId() {
            // given
            Long messageId = 100L;
            Long parentMessageId = 100L;

            // when & then
            assertThatThrownBy(() -> MessageReply.create(messageId, parentMessageId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Message ID and parent message ID cannot be the same");
        }
    }

    @Nested
    @DisplayName("조회")
    class QueryTest {

        @Test
        @DisplayName("메시지 ID를 조회할 때 올바른 메시지 ID가 반환되어야 한다")
        void should_ReturnMessageId_When_GetMessageId() {
            // given
            Long messageId = 100L;
            Long parentMessageId = 200L;
            MessageReply messageReply = MessageReply.create(messageId, parentMessageId);

            // when
            Long result = messageReply.messageId();

            // then
            assertThat(result).isEqualTo(messageId);
        }

        @Test
        @DisplayName("부모 메시지 ID를 조회할 때 올바른 부모 메시지 ID가 반환되어야 한다")
        void should_ReturnParentMessageId_When_GetParentMessageId() {
            // given
            Long messageId = 100L;
            Long parentMessageId = 200L;
            MessageReply messageReply = MessageReply.create(messageId, parentMessageId);

            // when
            Long result = messageReply.parentMessageId();

            // then
            assertThat(result).isEqualTo(parentMessageId);
        }
    }

    @Nested
    @DisplayName("소프트 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("메시지 답글을 소프트 삭제하면 삭제 상태가 되어야 한다")
        void should_MarkAsDeleted_When_SoftDelete() {
            // given
            Long messageId = 100L;
            Long parentMessageId = 200L;
            MessageReply messageReply = MessageReply.create(messageId, parentMessageId);

            // when
            messageReply.softDelete();

            // then
            assertThat(messageReply.isDeleted()).isTrue();
        }
    }
}

