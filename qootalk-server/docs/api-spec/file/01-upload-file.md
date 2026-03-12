# 파일 업로드

- Method: `POST`
- Path: `/api/v1/files`
- Auth: Bearer Token
- Content-Type: `multipart/form-data`

## Form Data

- `file`
- `roomId`
- `messageId` (optional)

## Response

```json
{
  "success": true,
  "data": {
    "id": 501,
    "messageId": 1001,
    "uploaderId": 1,
    "fileName": "architecture.pdf",
    "fileType": "DOCUMENT",
    "contentType": "application/pdf",
    "fileSize": 102400,
    "storageType": "S3",
    "path": "chat/10/2026/03/architecture.pdf",
    "downloadUrl": "https://cdn.qootalk.com/files/501",
    "createdAt": "2026-03-12T11:40:00+09:00"
  },
  "error": null
}
```
