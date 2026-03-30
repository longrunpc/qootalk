# 파일 공유 링크 생성

- Method: `POST`
- Path: `/api/v1/files/{fileId}/share-link`
- Auth: Bearer Token

## Request

```json
{
  "expiresAt": "2026-03-19T11:40:00+09:00",
  "password": "1234"
}
```

## Response

```json
{
  "success": true,
  "data": {
    "shareUrl": "https://qootalk.com/share/abcdef",
    "expiresAt": "2026-03-19T11:40:00+09:00"
  },
  "error": null
}
```
