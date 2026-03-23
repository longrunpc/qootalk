package com.lrchan.qootalk.presentation.api.chat.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

class ChatHistoryControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Nested
    @DisplayName("채팅 이력 조회")
    class LoadHistoriesTest {
        @Test
        @DisplayName("채팅 이력은 Slice 형태로 조회된다")
        void getChatHistoriesSuccess() throws Exception {
            UserEntity sender = createUser("history-sender@qootalk.com", "Password123!", "기록발신자");
            UserEntity member = createUser("history-member@qootalk.com", "Password123!", "기록멤버");

            Long roomId = createChatRoom(sender, member.getId());
            sendMessage(sender, roomId, "첫 번째 메시지");
            sendMessage(sender, roomId, "두 번째 메시지");

            mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX + "/" + roomId + "/histories"), sender)
                    .param("page", "0")
                    .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[0].content").value("두 번째 메시지"))
                .andExpect(jsonPath("$.data.hasNext").value(true));
        }

        @Test
        @DisplayName("대량 메시지도 fromMessageId 기반으로 안정적으로 페이징 조회할 수 있다")
        void getChatHistoriesWithFromMessageIdPaging() throws Exception {
            UserEntity sender = createUser("history-paging-sender@qootalk.com", "Password123!", "기록페이징발신자");
            UserEntity member = createUser("history-paging-member@qootalk.com", "Password123!", "기록페이징멤버");

            Long roomId = createChatRoom(sender, member.getId());

            Long lastMessageId = null;
            for (int i = 1; i <= 25; i++) {
                lastMessageId = sendMessage(sender, roomId, "message-" + i);
            }

            MvcResult firstPageResult = mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX + "/" + roomId + "/histories"), sender)
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(10)))
                .andExpect(jsonPath("$.data.content[0].content").value("message-25"))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andReturn();

            JsonNode firstPageJson = objectMapper.readTree(responseBody(firstPageResult));
            long fromMessageId = firstPageJson.path("data").path("content").get(9).path("id").asLong();

            mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX + "/" + roomId + "/histories"), sender)
                    .param("fromMessageId", String.valueOf(fromMessageId))
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(10)))
                .andExpect(jsonPath("$.data.content[0].content").value("message-15"))
                .andExpect(jsonPath("$.data.hasNext").value(true));
        }

        @Test
        @DisplayName("참여하지 않은 사용자는 채팅 이력을 조회할 수 없다")
        void getChatHistoriesFailsWhenNotParticipant() throws Exception {
            UserEntity owner = createUser("history-owner@qootalk.com", "Password123!", "기록방장");
            UserEntity member = createUser("history-room-member@qootalk.com", "Password123!", "기록참여자");
            UserEntity outsider = createUser("history-outsider@qootalk.com", "Password123!", "기록외부인");

            Long roomId = createChatRoom(owner, member.getId());

            mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX + "/" + roomId + "/histories"), outsider)
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_007"));
        }
    }

    @Nested
    @DisplayName("읽음 처리")
    class MarkReadTest {
        @Test
        @DisplayName("더 최신 메시지로 읽음 처리를 할 수 있다")
        void markAsReadSuccess() throws Exception {
            UserEntity sender = createUser("read-sender@qootalk.com", "Password123!", "읽음발신자");
            UserEntity member = createUser("read-member@qootalk.com", "Password123!", "읽음멤버");

            Long roomId = createChatRoom(sender, member.getId());
            Long messageId = sendMessage(sender, roomId, "읽음 대상 메시지");

            mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/read"), member)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new MarkMessageReadRequest(messageId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomId").value(roomId))
                .andExpect(jsonPath("$.data.lastReadMessageId").value(messageId))
                .andExpect(jsonPath("$.data.updated").value(true));
        }

        @Test
        @DisplayName("이미 읽은 메시지를 다시 읽음 처리하면 updated=false를 반환한다")
        void markAsReadReturnsFalseWhenAlreadyRead() throws Exception {
            UserEntity sender = createUser("read2-sender@qootalk.com", "Password123!", "읽음2발신자");
            UserEntity member = createUser("read2-member@qootalk.com", "Password123!", "읽음2멤버");

            Long roomId = createChatRoom(sender, member.getId());
            Long messageId = sendMessage(sender, roomId, "이미 읽은 메시지");

            mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/read"), member)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new MarkMessageReadRequest(messageId))))
                .andExpect(status().isOk());

            mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/read"), member)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new MarkMessageReadRequest(messageId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updated").value(false));
        }
    }

    private Long createChatRoom(UserEntity requester, Long... participantIds) throws Exception {
        MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX), requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new CreateChatRoomRequest(
                    "이력방",
                    RoomType.GROUP,
                    participantIds,
                    true
                ))))
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

    private record MarkMessageReadRequest(Long lastReadMessageId) {
    }
}
