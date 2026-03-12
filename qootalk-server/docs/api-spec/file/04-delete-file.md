# 파일 삭제

- Method: `DELETE`
- Path: `/api/v1/files/{fileId}`
- Auth: Uploader, Owner, or Admin

## Response

```json
{
  "success": true,
  "data": {
    "deleted": true,
    "fileId": 501
  },
  "error": null
}
```
