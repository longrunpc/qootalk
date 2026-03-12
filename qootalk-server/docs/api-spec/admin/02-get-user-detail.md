# 사용자 상세 조회

- Method: `GET`
- Path: `/api/v1/admin/users/{userId}`
- Auth: Admin

## Response

```json
{
  "success": true,
  "data": {
    "id": 12,
    "email": "user1@qootalk.com",
    "name": "사용자1",
    "role": "USER",
    "status": "ACTIVE",
    "emailVerified": true,
    "lastLoginAt": "2026-03-12T08:00:00+09:00",
    "createdAt": "2026-03-01T09:00:00+09:00",
    "updatedAt": "2026-03-10T17:00:00+09:00"
  },
  "error": null
}
```
