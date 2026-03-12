# 회원가입

- Method: `POST`
- Path: `/api/v1/auth/signup`
- Auth: Public

## Request

```json
{
  "email": "user@qootalk.com",
  "password": "P@ssw0rd!",
  "name": "홍길동"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@qootalk.com",
    "name": "홍길동",
    "profileImageUrl": null,
    "statusMessage": "",
    "role": "USER",
    "status": "ACTIVE",
    "emailVerified": false,
    "createdAt": "2026-03-12T10:00:00+09:00",
    "updatedAt": "2026-03-12T10:00:00+09:00"
  },
  "error": null
}
```
