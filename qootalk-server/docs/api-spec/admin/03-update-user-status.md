# 사용자 상태 변경

- Method: `PATCH`
- Path: `/api/v1/admin/users/{userId}/status`
- Auth: Admin

## Request

```json
{
  "status": "SUSPENDED",
  "reason": "운영 정책 위반"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "userId": 12,
    "status": "SUSPENDED",
    "updatedAt": "2026-03-12T10:30:00+09:00"
  },
  "error": null
}
```
