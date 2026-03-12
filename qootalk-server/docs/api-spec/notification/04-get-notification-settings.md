# 알림 설정 조회

- Method: `GET`
- Path: `/api/v1/notification-settings`
- Auth: Bearer Token

## Response

```json
{
  "success": true,
  "data": {
    "chatEnabled": true,
    "systemEnabled": true,
    "emailEnabled": false,
    "pushEnabled": true,
    "muteStartAt": null,
    "muteEndAt": null
  },
  "error": null
}
```
