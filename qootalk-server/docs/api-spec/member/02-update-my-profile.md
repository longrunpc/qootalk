# 내 프로필 수정

- Method: `PATCH`
- Path: `/api/v1/users/me`
- Auth: Bearer Token

## Request

```json
{
  "name": "김쿠톡",
  "statusMessage": "업무 집중",
  "profileImageUrl": "https://cdn.qootalk.com/profiles/1-new.png"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@qootalk.com",
    "name": "김쿠톡",
    "profileImageUrl": "https://cdn.qootalk.com/profiles/1-new.png",
    "statusMessage": "업무 집중",
    "role": "USER",
    "createdAt": "2026-03-01T10:00:00",
    "updatedAt": "2026-03-12T10:10:00",
    "deletedAt": null
  },
  "error": null
}
```
