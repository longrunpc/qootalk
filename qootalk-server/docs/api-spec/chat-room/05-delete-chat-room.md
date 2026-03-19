# 채팅방 삭제

- Method: `DELETE`
- Path: `/api/v1/chat-rooms/{roomId}`
- Auth: Owner or Admin

## Response

```json
{
  "success": true,
  "data": {
    "id": 10,
    "roomName": "백엔드 팀",
    "deletedAt": "2026-03-12T11:15:00"
  },
  "error": null
}
```
