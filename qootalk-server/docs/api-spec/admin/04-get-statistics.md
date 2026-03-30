# 시스템 통계 조회

- Method: `GET`
- Path: `/api/v1/admin/statistics`
- Auth: Admin

## Response

```json
{
  "success": true,
  "data": {
    "userCount": 1523,
    "activeUserCount": 1310,
    "chatRoomCount": 402,
    "messageCountToday": 58210,
    "fileUploadCountToday": 392,
    "unreadNotificationCount": 118
  },
  "error": null
}
```
