# 채팅방 상세 조회

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}`
- Auth: Bearer Token

## Response

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
        "joinedAt": "2026-03-12T11:00:00+09:00"
      }
    ],
    "notificationEnabled": true,
    "createdAt": "2026-03-12T11:00:00+09:00"
  },
  "error": null
}
```
