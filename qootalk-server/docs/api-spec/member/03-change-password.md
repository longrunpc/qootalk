# 비밀번호 변경

- Method: `PATCH`
- Path: `/api/v1/users/me/password`
- Auth: Bearer Token

## Request

```json
{
  "currentPassword": "P@ssw0rd!",
  "newPassword": "N3wP@ssw0rd!"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "changed": true
  },
  "error": null
}
```
