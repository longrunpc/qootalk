# 로그인

- Method: `POST`
- Path: `/api/v1/auth/login`
- Auth: Public

## Request

```json
{
  "email": "user@qootalk.com",
  "password": "P@ssw0rd!"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "email": "user@qootalk.com",
      "name": "홍길동",
      "role": "USER",
      "status": "ACTIVE"
    },
    "token": {
      "accessToken": {
        "value": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 3600000
      },
      "refreshToken": {
        "value": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 1209600000
      }
    }
  },
  "error": null
}
```
