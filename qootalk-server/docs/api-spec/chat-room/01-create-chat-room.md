# 채팅방 생성

- Method: `POST`
- Path: `/api/v1/chat-rooms`
- Auth: Bearer Token

## Request

```json
{
  "roomName": "백엔드 팀",
  "roomType": "GROUP",
  "participantIds": [1, 2, 3],
  "notificationEnabled": true
}
```

## Response

```json
{
  "success": true,
  "data": {
    "id": 10,
    "roomName": "백엔드 팀",
    "roomType": "GROUP",
    "createdBy": 1,
    "participantCount": 3,
    "createdAt": "2026-03-12T11:00:00+09:00"
  },
  "error": null
}
```
