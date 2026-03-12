# 채팅방 수정

- Method: `PATCH`
- Path: `/api/v1/chat-rooms/{roomId}`
- Auth: Bearer Token

## Request

```json
{
  "roomName": "백엔드 플랫폼 팀"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "id": 10,
    "roomName": "백엔드 플랫폼 팀",
    "updatedAt": "2026-03-12T11:10:00+09:00"
  },
  "error": null
}
```
