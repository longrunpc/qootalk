package com.lrchan.qootalk.presentation.api.chat.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.infrastructure.persistence.user.UserEntity;
import com.lrchan.qootalk.presentation.support.ApiIntegrationTestSupport;

class MessageUpdateControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Nested
    @DisplayName("메시지 수정")
    class UpdateMessageTest {
        @Test
        @DisplayName("작성자는 메시지를 수정할 수 있다")
        void updateMessageSuccess() throws Exception {
            UserEntity sender = createUser("edit-sender@qootalk.com", "Password123!", "수정발신자");
            UserEntity member = createUser("edit-member@qootalk.com", "Password123!", "수정멤버");

            Long roomId = createChatRoom(sender, member.getId());
            Long messageId = sendMessage(sender, roomId, "원본 메시지");

            mockMvc.perform(authorized(patch(CHAT_ROOM_API_PREFIX + "/" + roomId + "/messages/" + messageId), sender)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new UpdateMessageRequest("수정된 메시지"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.messageId").value(messageId))
                .andExpect(jsonPath("$.data.content").value("수정된 메시지"))
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
        }

        @Test
        @DisplayName("작성자가 아닌 사용자는 메시지를 수정할 수 없다")
        void updateMessageFailsWhenRequesterIsNotAuthor() throws Exception {
            UserEntity sender = createUser("edit2-sender@qootalk.com", "Password123!", "수정2발신자");
            UserEntity member = createUser("edit2-member@qootalk.com", "Password123!", "수정2멤버");

            Long roomId = createChatRoom(sender, member.getId());
            Long messageId = sendMessage(sender, roomId, "원본 메시지");

            mockMvc.perform(authorized(patch(CHAT_ROOM_API_PREFIX + "/" + roomId + "/messages/" + messageId), member)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new UpdateMessageRequest("수정 시도"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_030"));
        }
    }

    private Long createChatRoom(UserEntity requester, Long... participantIds) throws Exception {
        MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX), requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new CreateChatRoomRequest("수정방", RoomType.GROUP, participantIds, true))))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode json = objectMapper.readTree(responseBody(result));
        return json.path("data").path("id").asLong();
    }

    private Long sendMessage(UserEntity sender, Long roomId, String content) throws Exception {
        MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/messages"), sender)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new SendMessageRequest(content, MessageType.TEXT, new Long[] {}, null, new Long[] {}))))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode json = objectMapper.readTree(responseBody(result));
        return json.path("data").path("id").asLong();
    }

    private record CreateChatRoomRequest(
        String roomName,
        RoomType roomType,
        Long[] participantIds,
        boolean notificationEnabled
    ) {
    }

    private record SendMessageRequest(
        String content,
        MessageType messageType,
        Long[] mentions,
        Long parentMessageId,
        Long[] attachmentIds
    ) {
    }

    private record UpdateMessageRequest(String content) {
    }
}
