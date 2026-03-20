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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.infrastructure.persistence.user.UserEntity;
import com.lrchan.qootalk.presentation.support.ApiIntegrationTestSupport;

class ChatRoomControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Nested
    @DisplayName("채팅방 생성 및 상세 조회")
    class CreateChatRoomTest {
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
        @DisplayName("존재하지 않는 참여자를 포함해 채팅방을 생성하면 실패한다")
        void createChatRoomFailsWhenParticipantNotFound() throws Exception {
            UserEntity owner = createUser("owner-missing@qootalk.com", "Password123!", "방장");

            mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX), owner)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new CreateChatRoomRequest(
                        "없는 참여자 방",
                        RoomType.GROUP,
                        new Long[] {999L},
                        true
                    ))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("USER_001"))
                .andExpect(jsonPath("$.error.message").value("사용자를 찾을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("채팅방 이름 수정")
    class UpdateChatRoomTest {
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
        @DisplayName("참여하지 않은 사용자는 채팅방 상세를 조회할 수 없다")
        void getChatRoomDetailFailsWhenNotParticipant() throws Exception {
            UserEntity owner = createUser("detail-owner@qootalk.com", "Password123!", "상세방장");
            UserEntity member = createUser("detail-member@qootalk.com", "Password123!", "상세멤버");
            UserEntity outsider = createUser("detail-outsider@qootalk.com", "Password123!", "외부인");

            Long roomId = createChatRoom(owner, member.getId());

            mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX + "/" + roomId), outsider))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_007"))
                .andExpect(jsonPath("$.error.message").value("채팅방 참여자를 찾을 수 없습니다."));
        }

        @Test
        @DisplayName("참여하지 않은 사용자는 채팅방 이름을 수정할 수 없다")
        void updateChatRoomFailsWhenNotParticipant() throws Exception {
            UserEntity owner = createUser("update2-owner@qootalk.com", "Password123!", "수정방장");
            UserEntity member = createUser("update2-member@qootalk.com", "Password123!", "수정멤버");
            UserEntity outsider = createUser("update2-outsider@qootalk.com", "Password123!", "외부인");

            Long roomId = createChatRoom(owner, member.getId());

            mockMvc.perform(authorized(patch(CHAT_ROOM_API_PREFIX + "/" + roomId), outsider)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(new UpdateChatRoomRequest("수정 불가"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_007"));
        }
    }

    @Nested
    @DisplayName("채팅방 삭제")
    class DeleteChatRoomTest {
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

        @Test
        @DisplayName("일반 참여자는 채팅방을 삭제할 수 없다")
        void deleteChatRoomFailsWhenRequesterIsMember() throws Exception {
            UserEntity owner = createUser("delete2-owner@qootalk.com", "Password123!", "삭제방장");
            UserEntity member = createUser("delete2-member@qootalk.com", "Password123!", "삭제멤버");

            Long roomId = createChatRoom(owner, member.getId());

            mockMvc.perform(authorized(delete(CHAT_ROOM_API_PREFIX + "/" + roomId), member))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_009"))
                .andExpect(jsonPath("$.error.message").value("채팅방 참여자 권한이 올바르지 않습니다."));
        }
    }

    @Nested
    @DisplayName("채팅방 목록 조회")
    class GetChatRoomsTest {
        @Test
        @DisplayName("채팅방 목록은 페이지 기준으로 나뉘어 조회된다")
        void getChatRoomsSupportsPaging() throws Exception {
            UserEntity owner = createUser("paging-owner@qootalk.com", "Password123!", "페이징방장");
            UserEntity member1 = createUser("paging-member1@qootalk.com", "Password123!", "페이징멤버1");
            UserEntity member2 = createUser("paging-member2@qootalk.com", "Password123!", "페이징멤버2");

            createChatRoom(owner, "첫 번째 방", member1.getId());
            createChatRoom(owner, "두 번째 방", member2.getId());

            mockMvc.perform(authorized(get(CHAT_ROOM_API_PREFIX), owner)
                    .param("page", "0")
                    .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2));
        }
    }

    private Long createChatRoom(UserEntity requester, Long... participantIds) throws Exception {
        return createChatRoom(requester, "백엔드 스터디", participantIds);
    }

    private Long createChatRoom(UserEntity requester, String roomName, Long... participantIds) throws Exception {
        MvcResult result = mockMvc.perform(authorized(post(CHAT_ROOM_API_PREFIX), requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new CreateChatRoomRequest(
                    roomName,
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
