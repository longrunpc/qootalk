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
        "fileName": "architecture.pdf",
        "fileType": "DOCUMENT",
        "fileSize": 102400,
        "uploaderId": 1,
        "createdAt": "2026-03-12T11:40:00+09:00"
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
