# 채팅방 삭제

- Method: `DELETE`
- Path: `/api/v1/chat-rooms/{roomId}`
- Auth: Owner or Admin

## Response

```json
{
  "success": true,
  "data": {
    "deleted": true,
    "roomId": 10
  },
  "error": null
}
```
