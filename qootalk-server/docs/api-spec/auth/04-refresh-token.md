# 토큰 재발급

- Method: `POST`
- Path: `/api/v1/auth/refresh`
- Auth: `X-Refresh-Token`

## Request

```json
{}
```

## Response

```json
{
  "success": true,
  "data": {
    "accessToken": {
      "value": "eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 3600000
    },
    "refreshToken": {
      "value": "eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 1209600000
    }
  },
  "error": null
}
```
