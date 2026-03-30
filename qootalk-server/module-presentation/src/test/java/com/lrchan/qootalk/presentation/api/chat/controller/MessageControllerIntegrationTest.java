package com.lrchan.qootalk.presentation.api.chat.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

class MessageControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Nested
    @DisplayName("메시지 전송")
    class SendMessageTest {
        @Test
        @DisplayName("참여 사용자는 텍스트 메시지를 전송할 수 있다")
        void sendTextMessageSuccess() throws Exception {
            UserEntity sender = createUser("sender@qootalk.com", "Password123!", "발신자");
            UserEntity member = createUser("receiver@qootalk.com", "Password123!", "수신자");

            Long roomId = createChatRoom(sender, member.getId());

            MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/messages"), sender)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new SendMessageRequest(
                        "안녕하세요",
                        MessageType.TEXT,
                        new Long[] {member.getId()},
                        null,
                        new Long[] {}
                    ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomId").value(roomId))
                .andExpect(jsonPath("$.data.senderId").value(sender.getId()))
                .andExpect(jsonPath("$.data.content").value("안녕하세요"))
                .andExpect(jsonPath("$.data.messageType").value(MessageType.TEXT.name()))
                .andReturn();

            JsonNode json = objectMapper.readTree(responseBody(result));
            Long messageId = json.path("data").path("id").asLong();

            org.assertj.core.api.Assertions.assertThat(messageJpaRepository.count()).isEqualTo(2L);
            org.assertj.core.api.Assertions.assertThat(lastReadMessageId(sender.getId(), roomId)).isEqualTo(messageId);
        }

        @Test
        @DisplayName("업로드한 첨부파일을 포함한 파일 메시지를 전송할 수 있다")
        void sendFileMessageSuccess() throws Exception {
            UserEntity sender = createUser("file-sender@qootalk.com", "Password123!", "파일발신자");
            UserEntity member = createUser("file-member@qootalk.com", "Password123!", "파일수신자");

            Long roomId = createChatRoom(sender, member.getId());
            long uploadedFileId = uploadFile(
                sender,
                roomId,
                lastReadMessageId(sender.getId(), roomId),
                "message-file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "message-file"
            );

            mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/messages"), sender)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new SendMessageRequest(
                        null,
                        MessageType.FILE,
                        new Long[] {},
                        null,
                        new Long[] {uploadedFileId}
                    ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.messageType").value(MessageType.FILE.name()))
                .andExpect(jsonPath("$.data.attachmentIds[0]").value(uploadedFileId));
        }

        @Test
        @DisplayName("참여하지 않은 사용자는 메시지를 전송할 수 없다")
        void sendMessageFailsWhenNotParticipant() throws Exception {
            UserEntity owner = createUser("owner-message@qootalk.com", "Password123!", "메시지방장");
            UserEntity member = createUser("member-message@qootalk.com", "Password123!", "메시지멤버");
            UserEntity outsider = createUser("outsider-message@qootalk.com", "Password123!", "외부인");

            Long roomId = createChatRoom(owner, member.getId());

            mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/messages"), outsider)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new SendMessageRequest(
                        "전송 실패",
                        MessageType.TEXT,
                        new Long[] {},
                        null,
                        new Long[] {}
                    ))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_007"))
                .andExpect(jsonPath("$.error.message").value("채팅방 참여자를 찾을 수 없습니다."));
        }

        @Test
        @DisplayName("내용과 첨부파일이 모두 없으면 메시지 전송에 실패한다")
        void sendMessageFailsWhenPayloadEmpty() throws Exception {
            UserEntity sender = createUser("empty-sender@qootalk.com", "Password123!", "빈메시지발신자");
            UserEntity member = createUser("empty-member@qootalk.com", "Password123!", "빈메시지수신자");

            Long roomId = createChatRoom(sender, member.getId());

            mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX + "/" + roomId + "/messages"), sender)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new SendMessageRequest(
                        "   ",
                        MessageType.TEXT,
                        new Long[] {},
                        null,
                        new Long[] {}
                    ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_021"))
                .andExpect(jsonPath("$.error.message").value("메시지 내용 또는 첨부파일이 필요합니다."));
        }
    }

    private Long createChatRoom(UserEntity requester, Long... participantIds) throws Exception {
        MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX), requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new CreateChatRoomRequest(
                    "메시지방",
                    RoomType.GROUP,
                    participantIds,
                    true
                ))))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode json = objectMapper.readTree(responseBody(result));
        return json.path("data").path("id").asLong();
    }

    private long uploadFile(
        UserEntity uploader,
        Long roomId,
        Long messageId,
        String fileName,
        String contentType,
        String content
    ) throws Exception {
        MvcResult uploadResult = mockMvc.perform(authorized(
                    multipart(FILE_API_PREFIX)
                        .file(multipartFile("file", fileName, contentType, content))
                        .param("roomId", String.valueOf(roomId))
                        .param("messageId", String.valueOf(messageId)),
                    uploader))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode uploadJson = objectMapper.readTree(responseBody(uploadResult));
        return uploadJson.path("data").path("id").asLong();
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
}
