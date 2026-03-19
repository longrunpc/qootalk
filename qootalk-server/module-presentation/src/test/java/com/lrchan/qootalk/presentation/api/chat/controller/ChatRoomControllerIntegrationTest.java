package com.lrchan.qootalk.presentation.api.chat.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.infrastructure.persistence.user.UserEntity;
import com.lrchan.qootalk.presentation.support.ApiIntegrationTestSupport;

class ChatRoomControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    @DisplayName("채팅방을 생성하고 목록과 상세를 조회할 수 있다")
    void createAndReadChatRoom() throws Exception {
        UserEntity owner = createUser("owner@qootalk.com", "Password123!", "방장");
        UserEntity member = createUser("member@qootalk.com", "Password123!", "멤버");

        Long roomId = createChatRoom(owner, member.getId());

        mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX), owner)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].id").value(roomId))
            .andExpect(jsonPath("$.data.content[0].roomName").value("백엔드 스터디"))
            .andExpect(jsonPath("$.data.content[0].lastMessage").value("채팅방을 생성했습니다."));

        mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX + "/" + roomId), owner))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(roomId))
            .andExpect(jsonPath("$.data.roomType").value(RoomType.GROUP.name()))
            .andExpect(jsonPath("$.data.notificationEnabled").value(true))
            .andExpect(jsonPath("$.data.participants", hasSize(2)))
            .andExpect(jsonPath("$.data.participants[*].userId", hasItems(owner.getId().intValue(), member.getId().intValue())));
    }

    @Test
    @DisplayName("채팅방 이름을 수정할 수 있다")
    void updateChatRoomSuccess() throws Exception {
        UserEntity owner = createUser("update-owner@qootalk.com", "Password123!", "수정방장");
        UserEntity member = createUser("update-member@qootalk.com", "Password123!", "수정멤버");

        Long roomId = createChatRoom(owner, member.getId());

        mockMvc.perform(authorized(patch(CHAT_ROOM_API_PREFIX + "/" + roomId), owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new UpdateChatRoomRequest("백엔드 플랫폼"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(roomId))
            .andExpect(jsonPath("$.data.roomName").value("백엔드 플랫폼"));
    }

    @Test
    @DisplayName("채팅방 소유자는 채팅방을 삭제할 수 있다")
    void deleteChatRoomSuccess() throws Exception {
        UserEntity owner = createUser("delete-owner@qootalk.com", "Password123!", "삭제방장");
        UserEntity member = createUser("delete-member@qootalk.com", "Password123!", "삭제멤버");

        Long roomId = createChatRoom(owner, member.getId());

        mockMvc.perform(authorized(delete(CHAT_ROOM_API_PREFIX + "/" + roomId), owner))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(roomId))
            .andExpect(jsonPath("$.data.roomName").value("백엔드 스터디"))
            .andExpect(jsonPath("$.data.deletedAt").isNotEmpty());
    }

    private Long createChatRoom(UserEntity requester, Long... participantIds) throws Exception {
        MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX), requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new CreateChatRoomRequest(
                    "백엔드 스터디",
                    RoomType.GROUP,
                    participantIds,
                    true
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.participantCount").value(participantIds.length + 1))
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

    private record UpdateChatRoomRequest(String roomName) {
    }
}
