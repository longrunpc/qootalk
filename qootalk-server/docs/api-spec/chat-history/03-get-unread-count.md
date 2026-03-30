# 읽지 않은 메시지 수 조회

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/unread-count`
- Auth: Bearer Token

## Response

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
