# 사용자 목록 조회

- Method: `GET`
- Path: `/api/v1/admin/users`
- Auth: Admin

## Query

- `page`
- `size`
- `sort`
- `keyword`
- `status`
- `role`

## Response

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 12,
        "email": "user1@qootalk.com",
        "name": "사용자1",
        "role": "USER",
        "status": "ACTIVE",
        "createdAt": "2026-03-01T09:00:00+09:00"
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
