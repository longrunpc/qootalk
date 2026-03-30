# 회원 탈퇴

- Method: `DELETE`
- Path: `/api/v1/users/me`
- Auth: Bearer Token

## Request

```json
{
  "password": "N3wP@ssw0rd!",
  "reason": "서비스 미사용"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "deleted": true,
    "deletedAt": "2026-03-12T10:20:00+09:00"
  },
  "error": null
}
```
