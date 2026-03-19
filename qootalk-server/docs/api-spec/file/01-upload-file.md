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
    "fileName": {
      "value": "architecture.pdf"
    },
    "fileType": "DOCUMENT",
    "contentType": {
      "value": "application/pdf"
    },
    "fileSize": {
      "value": 102400
    },
    "storageType": "LOCAL",
    "storagePath": {
      "value": "uploads/chat/10/attachments/1001/"
    },
    "createdAt": "2026-03-12T11:40:00"
  },
  "error": null
}
```
