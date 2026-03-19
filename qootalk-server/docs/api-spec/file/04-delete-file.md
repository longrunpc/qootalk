# 파일 삭제

- Method: `DELETE`
- Path: `/api/v1/files/{fileId}`
- Auth: Uploader, Owner, or Admin

## Response

```json
{
  "success": true,
  "data": {
    "id": 501,
    "deletedAt": "2026-03-12T11:45:00"
  },
  "error": null
}
```
