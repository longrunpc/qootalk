# QooTalk API Spec Index

## 응답 형식

**성공**

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

**실패**

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

**페이징**

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 50,
  "totalPages": 5
}
```

## API 목록

| # | Method | Path | 설명 | 명세 |
|---|--------|------|------|------|
| 1 | POST | `/api/v1/auth/signup` | 회원가입 | [01-signup.md](./auth/01-signup.md) |
| 2 | POST | `/api/v1/auth/login` | 로그인 | [02-login.md](./auth/02-login.md) |
| 3 | POST | `/api/v1/auth/logout` | 로그아웃 | [03-logout.md](./auth/03-logout.md) |
| 4 | POST | `/api/v1/auth/refresh` | 토큰 재발급 | [04-refresh-token.md](./auth/04-refresh-token.md) |
| 5 | GET | `/api/v1/users/me` | 내 프로필 조회 | [01-get-my-profile.md](./member/01-get-my-profile.md) |
| 6 | PATCH | `/api/v1/users/me` | 내 프로필 수정 | [02-update-my-profile.md](./member/02-update-my-profile.md) |
| 7 | PATCH | `/api/v1/users/me/password` | 비밀번호 변경 | [03-change-password.md](./member/03-change-password.md) |
| 8 | DELETE | `/api/v1/users/me` | 회원 탈퇴 | [04-delete-my-account.md](./member/04-delete-my-account.md) |
| 9 | GET | `/api/v1/admin/users` | 사용자 목록 조회 | [01-get-users.md](./admin/01-get-users.md) |
| 10 | GET | `/api/v1/admin/users/{userId}` | 사용자 상세 조회 | [02-get-user-detail.md](./admin/02-get-user-detail.md) |
| 11 | PATCH | `/api/v1/admin/users/{userId}/status` | 사용자 상태 변경 | [03-update-user-status.md](./admin/03-update-user-status.md) |
| 12 | GET | `/api/v1/admin/statistics` | 시스템 통계 조회 | [04-get-statistics.md](./admin/04-get-statistics.md) |
| 13 | POST | `/api/v1/chat-rooms` | 채팅방 생성 | [01-create-chat-room.md](./chat-room/01-create-chat-room.md) |
| 14 | GET | `/api/v1/chat-rooms` | 채팅방 목록 조회 | [02-get-chat-rooms.md](./chat-room/02-get-chat-rooms.md) |
| 15 | GET | `/api/v1/chat-rooms/{roomId}` | 채팅방 상세 조회 | [03-get-chat-room-detail.md](./chat-room/03-get-chat-room-detail.md) |
| 16 | PATCH | `/api/v1/chat-rooms/{roomId}` | 채팅방 수정 | [04-update-chat-room.md](./chat-room/04-update-chat-room.md) |
| 17 | DELETE | `/api/v1/chat-rooms/{roomId}` | 채팅방 삭제 | [05-delete-chat-room.md](./chat-room/05-delete-chat-room.md) |
| 18 | POST | `/api/v1/chat-rooms/{roomId}/messages` | 메시지 전송 | [01-send-message.md](./message/01-send-message.md) |
| 19 | PATCH | `/api/v1/messages/{messageId}` | 메시지 수정 | [02-update-message.md](./message/02-update-message.md) |
| 20 | DELETE | `/api/v1/messages/{messageId}` | 메시지 삭제 | [03-delete-message.md](./message/03-delete-message.md) |
| 21 | GET | `/api/v1/chat-rooms/{roomId}/histories` | 채팅 기록 조회 | [01-get-chat-history.md](./chat-history/01-get-chat-history.md) |
| 22 | GET | `/api/v1/chat-rooms/{roomId}/histories/search` | 채팅 기록 검색 | [02-search-chat-history.md](./chat-history/02-search-chat-history.md) |
| 23 | GET | `/api/v1/chat-rooms/{roomId}/unread-count` | 읽지 않은 메시지 수 조회 | [03-get-unread-count.md](./chat-history/03-get-unread-count.md) |
| 24 | POST | `/api/v1/chat-rooms/{roomId}/read` | 메시지 읽음 처리 | [04-mark-as-read.md](./chat-history/04-mark-as-read.md) |
| 25 | GET | `/api/v1/notifications` | 알림 목록 조회 | [01-get-notifications.md](./notification/01-get-notifications.md) |
| 26 | PATCH | `/api/v1/notifications/{notificationId}/read` | 알림 읽음 처리 | [02-read-notification.md](./notification/02-read-notification.md) |
| 27 | POST | `/api/v1/notifications/read-all` | 알림 일괄 읽음 처리 | [03-read-all-notifications.md](./notification/03-read-all-notifications.md) |
| 28 | GET | `/api/v1/notification-settings` | 알림 설정 조회 | [04-get-notification-settings.md](./notification/04-get-notification-settings.md) |
| 29 | PATCH | `/api/v1/notification-settings` | 알림 설정 수정 | [05-update-notification-settings.md](./notification/05-update-notification-settings.md) |
| 30 | POST | `/api/v1/files` | 파일 업로드 | [01-upload-file.md](./file/01-upload-file.md) |
| 31 | GET | `/api/v1/files` | 파일 목록 조회 | [02-get-files.md](./file/02-get-files.md) |
| 32 | GET | `/api/v1/files/{fileId}/download` | 파일 다운로드 | [03-download-file.md](./file/03-download-file.md) |
| 33 | DELETE | `/api/v1/files/{fileId}` | 파일 삭제 | [04-delete-file.md](./file/04-delete-file.md) |
| 34 | POST | `/api/v1/files/{fileId}/share-link` | 파일 공유 링크 생성 | [05-create-share-link.md](./file/05-create-share-link.md) |
