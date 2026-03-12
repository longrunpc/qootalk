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
    "name": "김쿠톡",
    "statusMessage": "업무 집중",
    "profileImageUrl": "https://cdn.qootalk.com/profiles/1-new.png",
    "updatedAt": "2026-03-12T10:10:00+09:00"
  },
  "error": null
}
```
