# 알림 목록 조회

- Method: `GET`
- Path: `/api/v1/notifications`
- Auth: Bearer Token

## Query

- `page`
- `size`
- `sort`
- `read`
- `type`

## Response

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 2001,
        "type": "CHAT",
        "title": "새 메시지",
        "content": "백엔드 팀 채팅방에 새 메시지가 도착했습니다.",
        "read": false,
        "createdAt": "2026-03-12T11:30:00+09:00"
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
