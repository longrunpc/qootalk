# 메시지 전송

- Method: `POST`
- Path: `/api/v1/chat-rooms/{roomId}/messages`
- Auth: Bearer Token

## Request

```json
{
  "content": "안녕하세요",
  "messageType": "TEXT",
  "mentions": [12, 15],
  "parentMessageId": null,
  "attachmentIds": []
}
```

## Response

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
    "attachmentIds": [],
    "createdAt": "2026-03-12T11:20:00"
  },
  "error": null
}
```
