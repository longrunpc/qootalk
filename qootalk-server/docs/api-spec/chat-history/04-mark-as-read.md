# 메시지 읽음 처리

- Method: `POST`
- Path: `/api/v1/chat-rooms/{roomId}/read`
- Auth: Bearer Token

## Request

```json
{
  "lastReadMessageId": 1001
}
```

## Response

```json
{
  "success": true,
  "data": {
    "roomId": 10,
    "lastReadMessageId": 1001,
    "updated": true
  },
  "error": null
}
```
