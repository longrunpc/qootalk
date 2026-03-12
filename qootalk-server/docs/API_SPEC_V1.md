# QooTalk API Specification v1

## 1. Overview

- Base URL: `/api/v1`
- Content-Type: `application/json`
- Authentication: `Authorization: Bearer {accessToken}`
- Time format: `ISO-8601` (`2026-03-11T10:30:00+09:00`)
- This document is the implementation target specification for APIs not yet exposed in `module-presentation`.

## 2. Common Conventions

### 2.1 Common Response

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

### 2.2 Common Error Response

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "USER_001",
    "message": "사용자를 찾을 수 없습니다."
  }
}
```

### 2.3 Common Pagination

Request query:

- `page`: 0-based page index
- `size`: page size
- `sort`: `createdAt,desc`

Response shape:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 135,
  "totalPages": 7
}
```

### 2.4 Common Headers

- `Authorization: Bearer {accessToken}`
- `X-Refresh-Token: {refreshToken}` when token refresh or logout is needed
- `X-Admin-Access: true` for admin-only clients if separated at gateway level

### 2.5 Main Enum Values

- `userRole`: `USER`, `ADMIN`
- `userStatus`: `ACTIVE`, `INACTIVE`, `SUSPENDED`, `DELETED`
- `roomType`: `DIRECT`, `GROUP`
- `roomRole`: `OWNER`, `MEMBER`
- `messageType`: `TEXT`, `FILE`, `IMAGE`, `SYSTEM`, `NOTICE`, `REPLY`
- `notificationType`: `CHAT`, `SYSTEM`, `FILE`, `ADMIN`, `MENTION`
- `fileType`: `IMAGE`, `VIDEO`, `DOCUMENT`, `AUDIO`, `OTHER`
- `fileStorageType`: `LOCAL`, `S3`

## 3. Authentication / Member APIs

### 3.1 Sign Up

- Method: `POST`
- Path: `/api/v1/auth/signup`
- Auth: public

Request

```json
{
  "email": "user@qootalk.com",
  "password": "P@ssw0rd!",
  "name": "홍길동"
}
```

Response `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@qootalk.com",
    "name": "홍길동",
    "profileImageUrl": null,
    "statusMessage": "",
    "role": "USER",
    "status": "ACTIVE",
    "emailVerified": false,
    "createdAt": "2026-03-11T10:30:00+09:00",
    "updatedAt": "2026-03-11T10:30:00+09:00"
  },
  "error": null
}
```

### 3.2 Login

- Method: `POST`
- Path: `/api/v1/auth/login`
- Auth: public

Request

```json
{
  "email": "user@qootalk.com",
  "password": "P@ssw0rd!"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "email": "user@qootalk.com",
      "name": "홍길동",
      "profileImageUrl": null,
      "statusMessage": "",
      "role": "USER",
      "status": "ACTIVE"
    },
    "token": {
      "accessToken": {
        "value": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 3600000
      },
      "refreshToken": {
        "value": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 1209600000
      }
    }
  },
  "error": null
}
```

### 3.3 Logout

- Method: `POST`
- Path: `/api/v1/auth/logout`
- Auth: required

Request

```json
{}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "loggedOut": true
  },
  "error": null
}
```

### 3.4 Refresh Token

- Method: `POST`
- Path: `/api/v1/auth/refresh`
- Auth: `X-Refresh-Token`

Request

```json
{}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "accessToken": {
      "value": "eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 3600000
    },
    "refreshToken": {
      "value": "eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 1209600000
    }
  },
  "error": null
}
```

### 3.5 My Profile

- Method: `GET`
- Path: `/api/v1/users/me`
- Auth: required

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@qootalk.com",
    "name": "홍길동",
    "profileImageUrl": "https://cdn.qootalk.com/profiles/1.png",
    "statusMessage": "회의 중",
    "role": "USER",
    "status": "ACTIVE",
    "emailVerified": true,
    "lastLoginAt": "2026-03-11T09:10:00+09:00",
    "createdAt": "2026-03-01T10:30:00+09:00",
    "updatedAt": "2026-03-11T10:30:00+09:00"
  },
  "error": null
}
```

### 3.6 Update My Profile

- Method: `PATCH`
- Path: `/api/v1/users/me`
- Auth: required

Request

```json
{
  "name": "김쿠톡",
  "statusMessage": "업무 집중",
  "profileImageUrl": "https://cdn.qootalk.com/profiles/1-new.png"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "김쿠톡",
    "statusMessage": "업무 집중",
    "profileImageUrl": "https://cdn.qootalk.com/profiles/1-new.png",
    "updatedAt": "2026-03-11T10:40:00+09:00"
  },
  "error": null
}
```

### 3.7 Change Password

- Method: `PATCH`
- Path: `/api/v1/users/me/password`
- Auth: required

Request

```json
{
  "currentPassword": "P@ssw0rd!",
  "newPassword": "N3wP@ssw0rd!"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "changed": true
  },
  "error": null
}
```

### 3.8 Delete Account

- Method: `DELETE`
- Path: `/api/v1/users/me`
- Auth: required

Request

```json
{
  "password": "N3wP@ssw0rd!",
  "reason": "서비스 미사용"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "deleted": true,
    "deletedAt": "2026-03-11T10:45:00+09:00"
  },
  "error": null
}
```

### 3.9 Send Email Verification

- Method: `POST`
- Path: `/api/v1/auth/email-verifications`
- Auth: public

Request

```json
{
  "email": "user@qootalk.com"
}
```

Response `202 Accepted`

```json
{
  "success": true,
  "data": {
    "sent": true
  },
  "error": null
}
```

### 3.10 Confirm Email Verification

- Method: `POST`
- Path: `/api/v1/auth/email-verifications/confirm`
- Auth: public

Request

```json
{
  "email": "user@qootalk.com",
  "verificationCode": "482951"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "emailVerified": true
  },
  "error": null
}
```

### 3.11 Find Login ID

- Method: `POST`
- Path: `/api/v1/auth/find-id`
- Auth: public

Request

```json
{
  "name": "홍길동",
  "email": "user@qootalk.com"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "loginId": "user@qootalk.com"
  },
  "error": null
}
```

### 3.12 Request Password Reset

- Method: `POST`
- Path: `/api/v1/auth/password-reset`
- Auth: public

Request

```json
{
  "email": "user@qootalk.com"
}
```

Response `202 Accepted`

```json
{
  "success": true,
  "data": {
    "sent": true
  },
  "error": null
}
```

### 3.13 Confirm Password Reset

- Method: `POST`
- Path: `/api/v1/auth/password-reset/confirm`
- Auth: public

Request

```json
{
  "resetToken": "reset-token",
  "newPassword": "N3wP@ssw0rd!"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "reset": true
  },
  "error": null
}
```

### 3.14 Update Member Status

- Method: `PATCH`
- Path: `/api/v1/users/{userId}/status`
- Auth: admin

Request

```json
{
  "status": "SUSPENDED",
  "reason": "운영 정책 위반"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "userId": 12,
    "status": "SUSPENDED",
    "updatedAt": "2026-03-11T11:00:00+09:00"
  },
  "error": null
}
```

## 4. Admin APIs

### 4.1 Admin Login

- Method: `POST`
- Path: `/api/v1/admin/auth/login`
- Auth: public

Request

```json
{
  "email": "admin@qootalk.com",
  "password": "AdminP@ss!"
}
```

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "admin": {
      "id": 1,
      "email": "admin@qootalk.com",
      "name": "관리자",
      "role": "SUPER_ADMIN"
    },
    "token": {
      "accessToken": {
        "value": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 3600000
      },
      "refreshToken": {
        "value": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 1209600000
      }
    }
  },
  "error": null
}
```

### 4.2 User List

- Method: `GET`
- Path: `/api/v1/admin/users`
- Auth: admin

Query

- `page`, `size`, `sort`
- `keyword`
- `status`
- `role`

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 12,
        "email": "user1@qootalk.com",
        "name": "사용자1",
        "role": "USER",
        "status": "ACTIVE",
        "createdAt": "2026-03-01T09:00:00+09:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  },
  "error": null
}
```

### 4.3 User Detail

- Method: `GET`
- Path: `/api/v1/admin/users/{userId}`
- Auth: admin

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 12,
    "email": "user1@qootalk.com",
    "name": "사용자1",
    "role": "USER",
    "status": "ACTIVE",
    "emailVerified": true,
    "lastLoginAt": "2026-03-11T08:00:00+09:00",
    "createdAt": "2026-03-01T09:00:00+09:00",
    "updatedAt": "2026-03-10T17:00:00+09:00"
  },
  "error": null
}
```

### 4.4 Change User Status

- Method: `PATCH`
- Path: `/api/v1/admin/users/{userId}/status`
- Auth: admin

Request

```json
{
  "status": "SUSPENDED",
  "reason": "스팸 메시지 반복 발송"
}
```

### 4.5 Chat Room List

- Method: `GET`
- Path: `/api/v1/admin/chat-rooms`
- Auth: admin

Query

- `page`, `size`, `sort`
- `roomType`
- `keyword`

### 4.6 Chat Room Detail

- Method: `GET`
- Path: `/api/v1/admin/chat-rooms/{roomId}`
- Auth: admin

### 4.7 Delete Chat Room

- Method: `DELETE`
- Path: `/api/v1/admin/chat-rooms/{roomId}`
- Auth: admin

Request

```json
{
  "reason": "운영 정책 위반"
}
```

### 4.8 System Statistics

- Method: `GET`
- Path: `/api/v1/admin/statistics`
- Auth: admin

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "userCount": 1523,
    "activeUserCount": 1310,
    "chatRoomCount": 402,
    "messageCountToday": 58210,
    "fileUploadCountToday": 392,
    "unreadNotificationCount": 118
  },
  "error": null
}
```

### 4.9 Audit Log List

- Method: `GET`
- Path: `/api/v1/admin/logs`
- Auth: admin

Query

- `page`, `size`, `sort`
- `actorType`
- `actionType`
- `targetType`
- `from`
- `to`

### 4.10 Admin Role Management

- Method: `PATCH`
- Path: `/api/v1/admin/admins/{adminId}/role`
- Auth: super admin

Request

```json
{
  "role": "SUPER_ADMIN"
}
```

## 5. Chat APIs

## 5.1 WebSocket Connection

- Protocol: WebSocket with STOMP
- Handshake path: `/api/v1/ws`
- Publish prefix: `/pub`
- Subscribe prefix: `/sub`
- Auth: `Authorization` header or STOMP connect header

Connect example

```json
{
  "Authorization": "Bearer {accessToken}"
}
```

### 5.2 Send Message

- REST fallback
- Method: `POST`
- Path: `/api/v1/chat-rooms/{roomId}/messages`
- Auth: required

Request

```json
{
  "content": "안녕하세요",
  "messageType": "TEXT",
  "mentions": [12, 15],
  "parentMessageId": null,
  "attachmentIds": []
}
```

Response `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 1001,
    "roomId": 10,
    "senderId": 1,
    "content": "안녕하세요",
    "messageType": "TEXT",
    "mentions": [12, 15],
    "parentMessageId": null,
    "attachments": [],
    "readCount": 0,
    "createdAt": "2026-03-11T11:05:00+09:00"
  },
  "error": null
}
```

### 5.3 Receive / Broadcast Message

- Topic: `/sub/chat-rooms/{roomId}`

Broadcast payload

```json
{
  "eventType": "MESSAGE_CREATED",
  "data": {
    "id": 1001,
    "roomId": 10,
    "senderId": 1,
    "content": "안녕하세요",
    "messageType": "TEXT",
    "mentions": [12, 15],
    "createdAt": "2026-03-11T11:05:00+09:00"
  }
}
```

### 5.4 Typing Indicator

- Publish topic: `/pub/chat-rooms/{roomId}/typing`
- Subscribe topic: `/sub/chat-rooms/{roomId}/typing`

Request payload

```json
{
  "typing": true
}
```

Broadcast payload

```json
{
  "eventType": "TYPING",
  "data": {
    "roomId": 10,
    "userId": 1,
    "typing": true,
    "sentAt": "2026-03-11T11:05:10+09:00"
  }
}
```

### 5.5 Online Status

- Subscribe topic: `/sub/presence`

Broadcast payload

```json
{
  "eventType": "PRESENCE_CHANGED",
  "data": {
    "userId": 1,
    "status": "ONLINE",
    "lastSeenAt": "2026-03-11T11:05:15+09:00"
  }
}
```

### 5.6 Update Message

- Method: `PATCH`
- Path: `/api/v1/messages/{messageId}`
- Auth: required

Request

```json
{
  "content": "안녕하세요. 수정본입니다."
}
```

### 5.7 Delete Message

- Method: `DELETE`
- Path: `/api/v1/messages/{messageId}`
- Auth: required

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "deleted": true,
    "messageId": 1001
  },
  "error": null
}
```

## 6. Chat History APIs

### 6.1 Chat History List

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/histories`
- Auth: required

Query

- `page`, `size`, `sort`
- `fromMessageId`

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1001,
        "roomId": 10,
        "senderId": 1,
        "content": "안녕하세요",
        "messageType": "TEXT",
        "createdAt": "2026-03-11T11:05:00+09:00"
      }
    ],
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "error": null
}
```

### 6.2 Chat History By Period

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/histories/by-period`
- Auth: required

Query

- `from=2026-03-01T00:00:00+09:00`
- `to=2026-03-11T23:59:59+09:00`

### 6.3 Search Chat History

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/histories/search`
- Auth: required

Query

- `keyword`
- `senderId`
- `messageType`
- `from`
- `to`
- `page`
- `size`

### 6.4 Delete Chat History

- Method: `DELETE`
- Path: `/api/v1/chat-rooms/{roomId}/histories/{messageId}`
- Auth: required

### 6.5 Export Chat History

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/histories/export`
- Auth: required

Query

- `format=json|csv|txt`
- `from`
- `to`

Response `200 OK`

- `application/json`, `text/csv`, `text/plain`

### 6.6 Unread Message Count

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/unread-count`
- Auth: required

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "roomId": 10,
    "unreadCount": 24,
    "lastReadMessageId": 998
  },
  "error": null
}
```

### 6.7 Mark Message As Read

- Method: `POST`
- Path: `/api/v1/chat-rooms/{roomId}/read`
- Auth: required

Request

```json
{
  "lastReadMessageId": 1001
}
```

### 6.8 Chat History Statistics

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/histories/statistics`
- Auth: required

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "roomId": 10,
    "messageCount": 1024,
    "fileMessageCount": 84,
    "imageMessageCount": 42,
    "participantCount": 8
  },
  "error": null
}
```

## 7. Chat Room APIs

### 7.1 Create Chat Room

- Method: `POST`
- Path: `/api/v1/chat-rooms`
- Auth: required

Request

```json
{
  "roomName": "백엔드 팀",
  "roomType": "GROUP",
  "participantIds": [1, 2, 3],
  "notificationEnabled": true
}
```

Response `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 10,
    "roomName": "백엔드 팀",
    "roomType": "GROUP",
    "createdBy": 1,
    "participantCount": 3,
    "createdAt": "2026-03-11T11:10:00+09:00"
  },
  "error": null
}
```

### 7.2 Chat Room List

- Method: `GET`
- Path: `/api/v1/chat-rooms`
- Auth: required

Query

- `page`, `size`, `sort`
- `roomType`
- `keyword`

### 7.3 Chat Room Detail

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}`
- Auth: required

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 10,
    "roomName": "백엔드 팀",
    "roomType": "GROUP",
    "createdBy": 1,
    "participants": [
      {
        "userId": 1,
        "roomRole": "OWNER",
        "joinedAt": "2026-03-11T11:10:00+09:00"
      }
    ],
    "notificationEnabled": true,
    "createdAt": "2026-03-11T11:10:00+09:00"
  },
  "error": null
}
```

### 7.4 Update Chat Room

- Method: `PATCH`
- Path: `/api/v1/chat-rooms/{roomId}`
- Auth: required

Request

```json
{
  "roomName": "백엔드 플랫폼 팀"
}
```

### 7.5 Delete Chat Room

- Method: `DELETE`
- Path: `/api/v1/chat-rooms/{roomId}`
- Auth: owner or admin

### 7.6 Add Participant

- Method: `POST`
- Path: `/api/v1/chat-rooms/{roomId}/participants`
- Auth: owner or admin

Request

```json
{
  "userIds": [4, 5]
}
```

### 7.7 Remove Participant

- Method: `DELETE`
- Path: `/api/v1/chat-rooms/{roomId}/participants/{userId}`
- Auth: owner or admin

### 7.8 Leave Chat Room

- Method: `POST`
- Path: `/api/v1/chat-rooms/{roomId}/leave`
- Auth: required

### 7.9 Invite To Chat Room

- Method: `POST`
- Path: `/api/v1/chat-rooms/{roomId}/invites`
- Auth: owner or admin

Request

```json
{
  "userIds": [7, 8],
  "message": "프로젝트 채팅방에 초대합니다."
}
```

### 7.10 Change Room Role

- Method: `PATCH`
- Path: `/api/v1/chat-rooms/{roomId}/participants/{userId}/role`
- Auth: owner

Request

```json
{
  "roomRole": "OWNER"
}
```

### 7.11 Update Room Notification Setting

- Method: `PATCH`
- Path: `/api/v1/chat-rooms/{roomId}/notification-setting`
- Auth: required

Request

```json
{
  "enabled": false,
  "muteUntil": "2026-03-12T09:00:00+09:00"
}
```

## 8. Notification APIs

### 8.1 Create Notification

- Method: `POST`
- Path: `/api/v1/notifications`
- Auth: system or admin

Request

```json
{
  "userId": 12,
  "type": "SYSTEM",
  "title": "점검 안내",
  "content": "오늘 23시에 시스템 점검이 진행됩니다.",
  "linkUrl": "/notices/1"
}
```

### 8.2 Notification List

- Method: `GET`
- Path: `/api/v1/notifications`
- Auth: required

Query

- `page`, `size`, `sort`
- `read`
- `type`

### 8.3 Mark Notification As Read

- Method: `PATCH`
- Path: `/api/v1/notifications/{notificationId}/read`
- Auth: required

### 8.4 Delete Notification

- Method: `DELETE`
- Path: `/api/v1/notifications/{notificationId}`
- Auth: required

### 8.5 Get Notification Setting

- Method: `GET`
- Path: `/api/v1/notification-settings`
- Auth: required

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "chatEnabled": true,
    "systemEnabled": true,
    "emailEnabled": false,
    "pushEnabled": true,
    "muteStartAt": null,
    "muteEndAt": null
  },
  "error": null
}
```

### 8.6 Update Notification Setting

- Method: `PATCH`
- Path: `/api/v1/notification-settings`
- Auth: required

Request

```json
{
  "chatEnabled": true,
  "systemEnabled": true,
  "emailEnabled": true,
  "pushEnabled": false,
  "muteStartAt": "2026-03-11T22:00:00+09:00",
  "muteEndAt": "2026-03-12T07:00:00+09:00"
}
```

### 8.7 Send Push Notification

- Method: `POST`
- Path: `/api/v1/notifications/push`
- Auth: system or admin

### 8.8 Send Email Notification

- Method: `POST`
- Path: `/api/v1/notifications/email`
- Auth: system or admin

### 8.9 Unread Notification Count

- Method: `GET`
- Path: `/api/v1/notifications/unread-count`
- Auth: required

Response `200 OK`

```json
{
  "success": true,
  "data": {
    "unreadCount": 6
  },
  "error": null
}
```

### 8.10 Mark All Notifications As Read

- Method: `POST`
- Path: `/api/v1/notifications/read-all`
- Auth: required

## 9. File APIs

### 9.1 Upload File

- Method: `POST`
- Path: `/api/v1/files`
- Auth: required
- Content-Type: `multipart/form-data`

Form fields

- `file`: binary
- `roomId`: number
- `messageId`: number, optional

Response `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 501,
    "messageId": 1001,
    "uploaderId": 1,
    "fileName": "architecture.pdf",
    "fileType": "DOCUMENT",
    "contentType": "application/pdf",
    "fileSize": 102400,
    "storageType": "S3",
    "path": "chat/10/2026/03/architecture.pdf",
    "downloadUrl": "https://cdn.qootalk.com/files/501",
    "createdAt": "2026-03-11T11:20:00+09:00"
  },
  "error": null
}
```

### 9.2 Download File

- Method: `GET`
- Path: `/api/v1/files/{fileId}/download`
- Auth: required

Response `200 OK`

- binary stream

### 9.3 File List

- Method: `GET`
- Path: `/api/v1/files`
- Auth: required

Query

- `roomId`
- `uploaderId`
- `fileType`
- `page`
- `size`

### 9.4 Delete File

- Method: `DELETE`
- Path: `/api/v1/files/{fileId}`
- Auth: uploader, owner, or admin

### 9.5 File Metadata

- Method: `GET`
- Path: `/api/v1/files/{fileId}`
- Auth: required

### 9.6 File Validation Rule

- Max file size
  - image: `10MB`
  - video: `100MB`
  - document/audio/other: `50MB`
- Allowed extensions
  - image: `jpg`, `jpeg`, `png`, `gif`, `webp`
  - video: `mp4`, `mov`
  - document: `pdf`, `doc`, `docx`, `xls`, `xlsx`, `ppt`, `pptx`, `txt`
  - audio: `mp3`, `wav`, `m4a`
- Malicious scan status
  - `PENDING`, `PASSED`, `FAILED`

### 9.7 File Share Link

- Method: `POST`
- Path: `/api/v1/files/{fileId}/share-link`
- Auth: required

Request

```json
{
  "expiresAt": "2026-03-18T11:20:00+09:00",
  "password": "1234"
}
```

Response `201 Created`

```json
{
  "success": true,
  "data": {
    "shareUrl": "https://qootalk.com/share/abcdef",
    "expiresAt": "2026-03-18T11:20:00+09:00"
  },
  "error": null
}
```

## 10. Recommended Success / Error Codes

### 10.1 Success Codes

- `200 OK`: 조회, 수정, 삭제 성공
- `201 Created`: 생성 성공
- `202 Accepted`: 메일 발송, 비동기 작업 요청 성공

### 10.2 Common Error Codes

- `GLOBAL_001`: internal server error
- `GLOBAL_002`: invalid input
- `GLOBAL_003`: unauthorized
- `GLOBAL_004`: forbidden
- `USER_001`: user not found or login failed depending on application/domain layer mapping
- `USER_002`: user already exists or profile image mismatch depending on application/domain layer mapping
- `USER_003`: invalid password
- `USER_004`: invalid email
- `USER_005`: invalid name
- `USER_006`: invalid profile image url
- `USER_007`: invalid status message
- `USER_008`: invalid role
- `USER_009`: deleted user
- `USER_010`: invalid issued token
- `USER_011`: expired token
- `CHAT_001`: chat room not found
- `CHAT_002`: chat room already exists
- `CHAT_003`: invalid chat room name
- `CHAT_004`: invalid chat room type
- `CHAT_005`: invalid last read message id
- `CHAT_006` to `CHAT_012`: file metadata and security validation errors

## 11. Domain Mapping Notes

- `User` response fields follow current application DTO: `id`, `email`, `name`, `profileImageUrl`, `statusMessage`, `role`, `createdAt`, `updatedAt`.
- `ChatRoom` maps to current domain fields: `roomName`, `roomType`, `createdBy`.
- `Message` maps to current domain fields: `roomId`, `userId`, `content`, `messageType`, `mentions`, `parentMessageId`.
- `FileAttachment` maps to current domain fields: `messageId`, `uploaderId`, `metadata`, `fileType`, `fileSecurity`.
- `userStatus`, `notificationType`, `roomRole`, admin role, and notification setting entities are introduced as target specification because they are not implemented yet in the current domain module.

## 12. Recommended Next Step

- Define request/response DTOs in `module-presentation`
- Add controller packages by domain: `auth`, `user`, `admin`, `chat`, `notification`, `file`
- Convert this document into Swagger/OpenAPI YAML after endpoint DTOs are fixed
