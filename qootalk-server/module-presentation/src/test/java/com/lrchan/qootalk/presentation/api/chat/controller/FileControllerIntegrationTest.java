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

    @Test
    @DisplayName("참여하지 않은 사용자는 파일을 업로드할 수 없다")
    void uploadFileFailsWhenNotParticipant() throws Exception {
        UserEntity owner = createUser("upload-owner@qootalk.com", "Password123!", "업로드방장");
        UserEntity member = createUser("upload-member@qootalk.com", "Password123!", "업로드멤버");
        UserEntity outsider = createUser("upload-outsider@qootalk.com", "Password123!", "외부업로더");

        Long roomId = createChatRoom(owner, member.getId());
        Long messageId = lastReadMessageId(owner.getId(), roomId);

        mockMvc.perform(authorized(
                    multipart(FILE_API_PREFIX)
                        .file(multipartFile("file", "blocked.pdf", MediaType.APPLICATION_PDF_VALUE, "blocked"))
                        .param("roomId", String.valueOf(roomId))
                        .param("messageId", String.valueOf(messageId)),
                    outsider))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("CHAT_007"))
            .andExpect(jsonPath("$.error.message").value("채팅방 참여자를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("파일 목록은 업로더와 타입으로 필터링할 수 있다")
    void getFilesSupportsUploaderAndTypeFilters() throws Exception {
        UserEntity owner = createUser("filter-owner@qootalk.com", "Password123!", "필터방장");
        UserEntity member = createUser("filter-member@qootalk.com", "Password123!", "필터멤버");

        Long roomId = createChatRoom(owner, member.getId());

        uploadFile(owner, roomId, lastReadMessageId(owner.getId(), roomId), "owner-doc.pdf", MediaType.APPLICATION_PDF_VALUE, "owner-doc");
        uploadFile(member, roomId, lastReadMessageId(member.getId(), roomId), "member-image.png", MediaType.IMAGE_PNG_VALUE, "member-image");

        mockMvc.perform(authorized(get(FILE_API_PREFIX), owner)
                .param("roomId", String.valueOf(roomId))
                .param("uploaderId", String.valueOf(member.getId()))
                .param("fileType", FileType.IMAGE.name())
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].uploaderId").value(member.getId()))
            .andExpect(jsonPath("$.data.content[0].fileType").value(FileType.IMAGE.name()))
            .andExpect(jsonPath("$.data.content[0].fileName").value("member-image.png"));
    }

    @Test
    @DisplayName("채팅방 소유자는 다른 참여자가 업로드한 파일도 삭제할 수 있다")
    void deleteFileSuccessWhenRequesterIsOwner() throws Exception {
        UserEntity owner = createUser("delete-file-owner@qootalk.com", "Password123!", "파일삭제방장");
        UserEntity member = createUser("delete-file-member@qootalk.com", "Password123!", "파일삭제멤버");

        Long roomId = createChatRoom(owner, member.getId());
        long fileId = uploadFile(
            member,
            roomId,
            lastReadMessageId(member.getId(), roomId),
            "member-doc.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "member-doc"
        );

        mockMvc.perform(authorized(delete(FILE_API_PREFIX + "/" + fileId), owner)
                .param("roomId", String.valueOf(roomId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(fileId))
            .andExpect(jsonPath("$.data.deletedAt").isNotEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 파일을 삭제하면 실패한다")
    void deleteFileFailsWhenFileNotFound() throws Exception {
        UserEntity owner = createUser("missing-file-owner@qootalk.com", "Password123!", "없는파일방장");
        UserEntity member = createUser("missing-file-member@qootalk.com", "Password123!", "없는파일멤버");

        Long roomId = createChatRoom(owner, member.getId());

        mockMvc.perform(authorized(delete(FILE_API_PREFIX + "/999"), owner)
                .param("roomId", String.valueOf(roomId)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("CHAT_018"))
            .andExpect(jsonPath("$.error.message").value("파일 첨부파일을 찾을 수 없습니다."));
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
            .andExpect(jsonPath("$.success").value(true))
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
}
