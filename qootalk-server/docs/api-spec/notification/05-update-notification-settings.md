# 알림 설정 수정

- Method: `PATCH`
- Path: `/api/v1/notification-settings`
- Auth: Bearer Token

## Request

```json
{
  "chatEnabled": true,
  "systemEnabled": true,
  "emailEnabled": true,
  "pushEnabled": false,
  "muteStartAt": "2026-03-12T22:00:00+09:00",
  "muteEndAt": "2026-03-13T07:00:00+09:00"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "chatEnabled": true,
    "systemEnabled": true,
    "emailEnabled": true,
    "pushEnabled": false,
    "muteStartAt": "2026-03-12T22:00:00+09:00",
    "muteEndAt": "2026-03-13T07:00:00+09:00"
  },
  "error": null
}
```
