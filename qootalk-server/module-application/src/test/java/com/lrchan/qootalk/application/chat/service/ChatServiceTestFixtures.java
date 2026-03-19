package com.lrchan.qootalk.application.chat.service;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileSecurity;
import com.lrchan.qootalk.domain.chat.vo.FileSize;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.RoomName;
import com.lrchan.qootalk.domain.chat.vo.StorageType;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.UserRole;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;
import com.lrchan.qootalk.domain.user.vo.UserName;

final class ChatServiceTestFixtures {

    private ChatServiceTestFixtures() {
    }

    static User activeUser(Long id) {
        LocalDateTime now = LocalDateTime.now();
        return User.reconstruct(
            id,
            new Email("user" + id + "@qootalk.com"),
            new Password("encoded-password"),
            new UserName("user" + id),
            new ProfileImageUrl(null),
            new StatusMessage(""),
            UserRole.USER,
            now.minusDays(1),
            now.minusHours(1),
            null
        );
    }

    static User deletedUser(Long id) {
        LocalDateTime now = LocalDateTime.now();
        return User.reconstruct(
            id,
            new Email("deleted" + id + "@qootalk.com"),
            new Password("encoded-password"),
            new UserName("deleted" + id),
            new ProfileImageUrl(null),
            new StatusMessage(""),
            UserRole.USER,
            now.minusDays(2),
            now.minusDays(1),
            now
        );
    }

    static ChatRoom activeRoom(Long id, Long createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return ChatRoom.reconstruct(
            id,
            new RoomName("room-" + id),
            RoomType.GROUP,
            createdBy,
            now.minusDays(1),
            now.minusMinutes(10),
            null
        );
    }

    static ChatRoom deletedRoom(Long id, Long createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return ChatRoom.reconstruct(
            id,
            new RoomName("room-" + id),
            RoomType.GROUP,
            createdBy,
            now.minusDays(1),
            now.minusMinutes(10),
            now
        );
    }

    static RoomParticipant participant(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role, boolean notificationEnabled) {
        LocalDateTime now = LocalDateTime.now();
        return RoomParticipant.reconstruct(
            id,
            userId,
            roomId,
            lastReadMessageId,
            role,
            notificationEnabled,
            now.minusDays(1),
            now.minusMinutes(5),
            null
        );
    }

    static RoomParticipant deletedParticipant(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role) {
        LocalDateTime now = LocalDateTime.now();
        return RoomParticipant.reconstruct(
            id,
            userId,
            roomId,
            lastReadMessageId,
            role,
            true,
            now.minusDays(1),
            now.minusMinutes(5),
            now
        );
    }

    static Message message(Long id, Long roomId, Long userId, String content, MessageType type) {
        LocalDateTime now = LocalDateTime.now();
        return Message.reconstruct(
            id,
            roomId,
            userId,
            content,
            type,
            List.of(),
            null,
            now.minusMinutes(1),
            now.minusMinutes(1),
            null
        );
    }

    static FileAttachment attachment(Long id, Long roomId, Long messageId, Long uploaderId, String fileName, FileType fileType) {
        LocalDateTime now = LocalDateTime.now();
        FileMetadata metadata = new FileMetadata(
            new FileName(fileName),
            new FileName("stored-" + fileName),
            new ContentType("application/pdf"),
            new FileSize(1024L),
            new Path("uploads/chat/" + roomId + "/attachments/" + messageId + "/"),
            StorageType.LOCAL
        );

        return FileAttachment.reconstruct(
            id,
            roomId,
            messageId,
            uploaderId,
            metadata,
            fileType,
            FileSecurity.defaultPrivate(),
            now.minusDays(1),
            now.minusHours(1),
            null
        );
    }

    static FileAttachment deletedAttachment(Long id, Long roomId, Long messageId, Long uploaderId, String fileName, FileType fileType) {
        LocalDateTime now = LocalDateTime.now();
        FileMetadata metadata = new FileMetadata(
            new FileName(fileName),
            new FileName("stored-" + fileName),
            new ContentType("application/pdf"),
            new FileSize(1024L),
            new Path("uploads/chat/" + roomId + "/attachments/" + messageId + "/"),
            StorageType.LOCAL
        );

        return FileAttachment.reconstruct(
            id,
            roomId,
            messageId,
            uploaderId,
            metadata,
            fileType,
            FileSecurity.defaultPrivate(),
            now.minusDays(1),
            now.minusHours(1),
            now.minusMinutes(1)
        );
    }
}
