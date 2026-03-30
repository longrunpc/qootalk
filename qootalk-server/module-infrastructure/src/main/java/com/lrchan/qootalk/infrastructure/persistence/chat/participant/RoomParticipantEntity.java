package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.infrastructure.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "room_participants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_participants_user_room", columnNames = {"user_id", "room_id"})
    }
)
@SQLDelete(sql = "UPDATE room_participants SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoomParticipantEntity extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "last_read_message_id", nullable = false)
    private Long lastReadMessageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoomRole role;

    @Column(name = "notification_enabled", nullable = false)
    @lombok.Builder.Default
    private boolean notificationEnabled = true;
}
