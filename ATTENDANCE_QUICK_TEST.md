# Quick Testing Guide - Attendance API

## API Routes Summary

### Teacher Endpoints
```
GET  /api/attendance/students              → List of students
POST /api/attendance/mark                  → Mark attendance
GET  /api/attendance/student/{id}          → Student's history
GET  /api/attendance/class/{id}?date=XX    → Class attendance
PUT  /api/attendance/{id}                  → Update record
DELETE /api/attendance/{id}                → Delete record
```

### Student Endpoints
```
GET /api/attendance/my                     → Own attendance
```

### General (Both)
```
GET /api/attendance/stats                  → Statistics
GET /api/attendance/report/monthly         → Monthly report
GET /api/attendance/report/bulk            → Date range report
```

## Test Flow - Teacher

**Step 1**: Login
```
POST /api/auth/login
Body: { "email": "teacher@school.com", "password": "xxx" }
Response: { "token": "eyJhbGc..." }
```

**Step 2**: Get students to mark
```
GET /api/attendance/students
Header: Authorization: Bearer {token}
Response: [{ "_id": "...", "name": "Student 1", ... }]
```

**Step 3**: Mark attendance
```
POST /api/attendance/mark
Header: Authorization: Bearer {token}
Body: {
  "student_id": "...",
  "date": "2024-01-15",
  "status": "Present"
}
Response: { "message": "Attendance marked successfully" }
```

## Test Flow - Student

**Step 1**: Login
```
POST /api/auth/login
Body: { "email": "student@school.com", "password": "xxx" }
Response: { "token": "eyJhbGc..." }
```

**Step 2**: View own attendance
```
GET /api/attendance/my
Header: Authorization: Bearer {token}
Response: [
  { "date": "2024-01-15", "status": "Present", "marked_by": "Teacher" },
  { "date": "2024-01-14", "status": "Absent", "marked_by": "Teacher" }
]
```

**Step 3**: Check statistics
```
GET /api/attendance/stats
Header: Authorization: Bearer {token}
Response: {
  "total": 20,
  "present": 18,
  "absent": 1,
  "late": 1,
  "leave": 0,
  "percentage": 90
}
```

## Valid Status Values
- `Present`
- `Absent`
- `Late`
- `Leave`

## Common Errors & Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| 401 Unauthorized | No/invalid token | Login again |
| 403 Forbidden | Wrong role | Use correct account |
| 404 Not Found | Endpoint missing | ✓ Now fixed |
| 500 Server Error | DB connection | Check MongoDB |

## Postman Collection
Can import the following endpoint template:

**Base URL**: `http://10.215.152.184:5000`

All endpoints require:
```
Header: Authorization: Bearer {token}
Header: Content-Type: application/json
```

## Success Indicators
- ✓ No 404 errors
- ✓ All endpoints return proper responses
- ✓ Status updates correctly
- ✓ Statistics calculate properly
- ✓ Students see their attendance
- ✓ Teachers can mark attendance
