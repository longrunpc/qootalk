# 파일 목록 조회

- Method: `GET`
- Path: `/api/v1/files`
- Auth: Bearer Token

## Query

- `roomId`
- `uploaderId`
- `fileType`
- `page`
- `size`

## Response

```json
{
  "success": true,
  "data": {
    "content": [
      {
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
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  },
  "error": null
}
```
