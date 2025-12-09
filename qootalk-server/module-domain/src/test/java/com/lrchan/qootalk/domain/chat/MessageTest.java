package com.lrchan.qootalk.domain.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Message 도메인 테스트")
class MessageTest {

    @Nested
    @DisplayName("메시지 생성")
    class CreateMessageTest {

        @Test
        @DisplayName("메시지를 생성할 때 기본값이 올바르게 설정되어야 한다")
        void should_CreateMessage_When_ValidInput() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Arrays.asList(200L, 300L);

            // when
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // then
            assertThat(message.roomId()).isEqualTo(1L);
            assertThat(message.userId()).isEqualTo(100L);
            assertThat(message.content()).isEqualTo("안녕하세요");
            assertThat(message.messageType()).isEqualTo(MessageType.TEXT);
            assertThat(message.mentions()).containsExactly(200L, 300L);
        }

        @Test
        @DisplayName("메시지를 생성할 때 메시지 타입이 null이면 기본값으로 TEXT가 설정되어야 한다")
        void should_SetTextAsDefaultMessageType_When_MessageTypeIsNull() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = null;
            List<Long> mentions = Collections.emptyList();

            // when
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // then
            assertThat(message.messageType()).isEqualTo(MessageType.TEXT);
        }

        @Test
        @DisplayName("메시지를 생성할 때 ID는 null이어야 한다")
        void should_HaveNullId_When_CreateMessage() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();

            // when
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // then
            assertThat(message.id()).isNull();
        }

        @Test
        @DisplayName("roomId가 null이면 예외가 발생해야 한다")
        void should_ThrowException_When_RoomIdIsNull() {
            // given
            Long roomId = null;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();

            // when & then
            assertThatThrownBy(() -> Message.create(roomId, userId, content, messageType, mentions))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Room ID cannot be null");
        }

        @Test
        @DisplayName("userId가 null이면 예외가 발생해야 한다")
        void should_ThrowException_When_UserIdIsNull() {
            // given
            Long roomId = 1L;
            Long userId = null;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();

            // when & then
            assertThatThrownBy(() -> Message.create(roomId, userId, content, messageType, mentions))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null");
        }

        @Test
        @DisplayName("메시지를 생성할 때 멘션이 null이면 빈 리스트가 설정되어야 한다")
        void should_SetEmptyList_When_MentionsIsNull() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = null;

            // when
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // then
            assertThat(message.mentions()).isEmpty();
        }

        @Test
        @DisplayName("생성 시 전달한 멘션 리스트를 나중에 수정해도 메시지에 영향을 주지 않아야 한다")
        void should_NotAffectMessage_When_ModifyOriginalMentionsAfterCreate() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = new ArrayList<>(Arrays.asList(200L, 300L));

            // when
            Message message = Message.create(roomId, userId, content, messageType, mentions);
            mentions.add(400L);

            // then
            assertThat(message.mentions()).containsExactly(200L, 300L);
            assertThat(message.mentions()).doesNotContain(400L);
        }

        @Test
        @DisplayName("답글 메시지를 생성할 때 부모 메시지 ID가 올바르게 설정되어야 한다")
        void should_CreateReplyMessage_When_ParentMessageIdProvided() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "답글입니다";
            MessageType messageType = MessageType.REPLY;
            List<Long> mentions = Collections.emptyList();
            Long parentMessageId = 999L;

            // when
            Message message = Message.create(roomId, userId, content, messageType, mentions, parentMessageId);

            // then
            assertThat(message.parentMessageId()).isEqualTo(999L);
        }

        @Test
        @DisplayName("답글이 아닌 메시지를 생성할 때 부모 메시지 ID는 null이어야 한다")
        void should_HaveNullParentMessageId_When_CreateNormalMessage() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "일반 메시지";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();

            // when
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // then
            assertThat(message.parentMessageId()).isNull();
        }
    }

    @Nested
    @DisplayName("내용 변경")
    class ChangeContentTest {

        @Test
        @DisplayName("내용을 변경할 때 내용이 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateContent_When_ChangeContent() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);
            String newContent = "새로운 내용";

            // when
            message.changeContent(newContent);

            // then
            assertThat(message.content()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("여러 번 내용을 변경할 때 마지막 내용이 유지되어야 한다")
        void should_KeepLastContent_When_ChangeContentMultipleTimes() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            message.changeContent("첫 번째 내용");
            message.changeContent("두 번째 내용");

            // then
            assertThat(message.content()).isEqualTo("두 번째 내용");
        }
    }

    @Nested
    @DisplayName("메시지 타입 변경")
    class ChangeMessageTypeTest {

        @Test
        @DisplayName("메시지 타입을 변경할 때 타입이 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateMessageType_When_ChangeMessageType() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);
            MessageType newMessageType = MessageType.IMAGE;

            // when
            message.changeMessageType(newMessageType);

            // then
            assertThat(message.messageType()).isEqualTo(newMessageType);
        }

        @Test
        @DisplayName("여러 번 메시지 타입을 변경할 때 마지막 타입이 유지되어야 한다")
        void should_KeepLastMessageType_When_ChangeMessageTypeMultipleTimes() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            message.changeMessageType(MessageType.FILE);
            message.changeMessageType(MessageType.IMAGE);

            // then
            assertThat(message.messageType()).isEqualTo(MessageType.IMAGE);
        }

        @Test
        @DisplayName("메시지 타입을 null로 변경할 때 기본값으로 TEXT가 설정되어야 한다")
        void should_SetTextAsDefaultMessageType_When_ChangeMessageTypeToNull() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.IMAGE;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            message.changeMessageType(null);

            // then
            assertThat(message.messageType()).isEqualTo(MessageType.TEXT);
        }
    }

    @Nested
    @DisplayName("멘션 변경")
    class ChangeMentionsTest {

        @Test
        @DisplayName("멘션을 변경할 때 멘션이 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateMentions_When_ChangeMentions() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);
            List<Long> newMentions = Arrays.asList(200L, 300L);

            // when
            message.changeMentions(newMentions);

            // then
            assertThat(message.mentions()).containsExactly(200L, 300L);
        }

        @Test
        @DisplayName("여러 번 멘션을 변경할 때 마지막 멘션이 유지되어야 한다")
        void should_KeepLastMentions_When_ChangeMentionsMultipleTimes() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            message.changeMentions(Arrays.asList(200L));
            message.changeMentions(Arrays.asList(300L, 400L));

            // then
            assertThat(message.mentions()).containsExactly(300L, 400L);
        }

        @Test
        @DisplayName("멘션을 null로 변경할 때 빈 리스트가 설정되어야 한다")
        void should_SetEmptyList_When_ChangeMentionsToNull() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Arrays.asList(200L, 300L);
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            message.changeMentions(null);

            // then
            assertThat(message.mentions()).isEmpty();
        }

        @Test
        @DisplayName("멘션 변경 시 원본 리스트와 독립적인 복사본이 생성되어야 한다")
        void should_CreateIndependentCopy_When_ChangeMentions() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);
            List<Long> originalMentions = new ArrayList<>(Arrays.asList(200L, 300L));

            // when
            message.changeMentions(originalMentions);
            originalMentions.add(400L);

            // then
            assertThat(message.mentions()).containsExactly(200L, 300L);
            assertThat(message.mentions()).doesNotContain(400L);
        }
    }

    @Nested
    @DisplayName("조회")
    class QueryTest {

        @Test
        @DisplayName("채팅방 ID를 조회할 때 올바른 채팅방 ID가 반환되어야 한다")
        void should_ReturnRoomId_When_GetRoomId() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            Long result = message.roomId();

            // then
            assertThat(result).isEqualTo(roomId);
        }

        @Test
        @DisplayName("사용자 ID를 조회할 때 올바른 사용자 ID가 반환되어야 한다")
        void should_ReturnUserId_When_GetUserId() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            Long result = message.userId();

            // then
            assertThat(result).isEqualTo(userId);
        }

        @Test
        @DisplayName("내용을 조회할 때 올바른 내용이 반환되어야 한다")
        void should_ReturnContent_When_GetContent() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            String result = message.content();

            // then
            assertThat(result).isEqualTo(content);
        }

        @Test
        @DisplayName("메시지 타입을 조회할 때 올바른 메시지 타입이 반환되어야 한다")
        void should_ReturnMessageType_When_GetMessageType() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.IMAGE;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            MessageType result = message.messageType();

            // then
            assertThat(result).isEqualTo(messageType);
        }

        @Test
        @DisplayName("멘션을 조회할 때 올바른 멘션 리스트가 반환되어야 한다")
        void should_ReturnMentions_When_GetMentions() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Arrays.asList(200L, 300L);
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            List<Long> result = message.mentions();

            // then
            assertThat(result).containsExactly(200L, 300L);
        }

        @Test
        @DisplayName("조회한 멘션 리스트를 수정해도 원본에 영향을 주지 않아야 한다")
        void should_NotAffectOriginal_When_ModifyReturnedMentions() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Arrays.asList(200L, 300L);
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            List<Long> returnedMentions = message.mentions();
            returnedMentions.add(400L);

            // then
            assertThat(message.mentions()).containsExactly(200L, 300L);
            assertThat(message.mentions()).doesNotContain(400L);
        }
    }

    @Nested
    @DisplayName("소프트 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("메시지를 소프트 삭제하면 삭제 상태가 되어야 한다")
        void should_MarkAsDeleted_When_SoftDelete() {
            // given
            Long roomId = 1L;
            Long userId = 100L;
            String content = "안녕하세요";
            MessageType messageType = MessageType.TEXT;
            List<Long> mentions = Collections.emptyList();
            Message message = Message.create(roomId, userId, content, messageType, mentions);

            // when
            message.softDelete();

            // then
            assertThat(message.isDeleted()).isTrue();
        }
    }
}

