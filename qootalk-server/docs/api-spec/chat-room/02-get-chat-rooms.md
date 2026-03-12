# 채팅방 목록 조회

- Method: `GET`
- Path: `/api/v1/chat-rooms`
- Auth: Bearer Token

## Query

- `page`
- `size`
- `sort`
- `roomType`
- `keyword`

## Response

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 10,
        "roomName": "백엔드 팀",
        "roomType": "GROUP",
        "lastMessage": "오늘 배포 일정 공유드립니다.",
        "unreadCount": 3,
        "updatedAt": "2026-03-12T11:05:00+09:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  },
  "error": null
}
```
