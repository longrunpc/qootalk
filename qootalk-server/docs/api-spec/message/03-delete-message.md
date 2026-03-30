# 메시지 삭제

- Method: `DELETE`
- Path: `/api/v1/messages/{messageId}`
- Auth: Bearer Token

## Response

```json
{
  "success": true,
  "data": {
    "deleted": true,
    "messageId": 1001
  },
  "error": null
}
```
