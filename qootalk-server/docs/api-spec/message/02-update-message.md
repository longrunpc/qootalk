# 메시지 수정

- Method: `PATCH`
- Path: `/api/v1/messages/{messageId}`
- Auth: Bearer Token

## Request

```json
{
  "content": "안녕하세요. 수정본입니다."
}
```

## Response

```json
{
  "success": true,
  "data": {
    "messageId": 1001,
    "content": "안녕하세요. 수정본입니다.",
    "updatedAt": "2026-03-12T11:21:00+09:00"
  },
  "error": null
}
```
