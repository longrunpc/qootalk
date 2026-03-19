package com.lrchan.qootalk.presentation.api.chat.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.infrastructure.persistence.user.UserEntity;
import com.lrchan.qootalk.presentation.support.ApiIntegrationTestSupport;

class FileControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    @DisplayName("파일을 업로드하고 목록을 조회한 뒤 삭제할 수 있다")
    void uploadListAndDeleteFile() throws Exception {
        UserEntity uploader = createUser("uploader@qootalk.com", "Password123!", "업로더");
        UserEntity member = createUser("file-member@qootalk.com", "Password123!", "파일멤버");

        Long roomId = createChatRoom(uploader, member.getId());
        Long messageId = lastReadMessageId(uploader.getId(), roomId);

        MvcResult uploadResult = mockMvc.perform(authorized(
                    multipart(FILE_API_PREFIX)
                        .file(multipartFile("file", "hello.pdf", MediaType.APPLICATION_PDF_VALUE, "hello-qootalk"))
                        .param("roomId", String.valueOf(roomId))
                        .param("messageId", String.valueOf(messageId)),
                    uploader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.roomId").doesNotExist())
            .andExpect(jsonPath("$.data.messageId").value(messageId))
            .andExpect(jsonPath("$.data.uploaderId").value(uploader.getId()))
            .andExpect(jsonPath("$.data.fileName").value("hello.pdf"))
            .andExpect(jsonPath("$.data.fileType").value(FileType.DOCUMENT.name()))
            .andReturn();

        JsonNode uploadJson = objectMapper.readTree(responseBody(uploadResult));
        long fileId = uploadJson.path("data").path("id").asLong();

        mockMvc.perform(authorized(get(FILE_API_PREFIX), uploader)
                .param("roomId", String.valueOf(roomId))
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].id").value(fileId))
            .andExpect(jsonPath("$.data.content[0].fileName").value("hello.pdf"))
            .andExpect(jsonPath("$.data.content[0].fileType").value(FileType.DOCUMENT.name()));

        mockMvc.perform(authorized(delete(FILE_API_PREFIX + "/" + fileId), uploader)
                .param("roomId", String.valueOf(roomId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(fileId))
            .andExpect(jsonPath("$.data.deletedAt").isNotEmpty());
    }

    @Test
    @DisplayName("참여하지 않은 채팅방의 파일 목록은 조회할 수 없다")
    void getFilesFailsWhenNotParticipant() throws Exception {
        UserEntity owner = createUser("owner-file@qootalk.com", "Password123!", "파일방장");
        UserEntity member = createUser("member-file@qootalk.com", "Password123!", "파일참여자");
        UserEntity outsider = createUser("outsider-file@qootalk.com", "Password123!", "외부유저");

        Long roomId = createChatRoom(owner, member.getId());

        mockMvc.perform(authorized(get(FILE_API_PREFIX), outsider)
                .param("roomId", String.valueOf(roomId))
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("CHAT_007"));
    }

    private Long createChatRoom(UserEntity requester, Long... participantIds) throws Exception {
        MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX), requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new CreateChatRoomRequest(
                    "파일 공유방",
                    RoomType.GROUP,
                    participantIds,
                    true
                ))))
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
}
