# 알림 읽음 처리

- Method: `PATCH`
- Path: `/api/v1/notifications/{notificationId}/read`
- Auth: Bearer Token

## Response

```json
{
  "success": true,
  "data": {
    "notificationId": 2001,
    "read": true,
    "readAt": "2026-03-12T11:31:00+09:00"
  },
  "error": null
}
```
