# 내 프로필 조회

- Method: `GET`
- Path: `/api/v1/users/me`
- Auth: Bearer Token

## Response

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@qootalk.com",
    "name": "홍길동",
    "profileImageUrl": "https://cdn.qootalk.com/profiles/1.png",
    "statusMessage": "회의 중",
    "role": "USER",
    "createdAt": "2026-03-01T10:00:00",
    "updatedAt": "2026-03-12T10:00:00",
    "deletedAt": null
  },
  "error": null
}
```
