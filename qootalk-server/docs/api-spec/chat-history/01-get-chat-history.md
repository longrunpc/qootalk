# 채팅 기록 조회

- Method: `GET`
- Path: `/api/v1/chat-rooms/{roomId}/histories`
- Auth: Bearer Token

## Query

- `page`
- `size`
- `sort`
- `fromMessageId`

## Response

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
        "createdAt": "2026-03-12T11:20:00+09:00"
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
