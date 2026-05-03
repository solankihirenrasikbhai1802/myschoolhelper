# 🚀 MySchoolHelper - Startup & Testing Guide

**Last Updated**: December 2024  
**Status**: Production Ready  
**Project**: Full-Stack Attendance Management System

---

## ⚡ Quick Start (5 Minutes)

### Prerequisites
- Node.js v14+ installed
- MongoDB running locally OR MongoDB Atlas URI ready
- 2 terminal windows

### Backend Setup

**Terminal 1:**
```bash
cd backend
npm install
cp .env.example .env
# Edit .env with your MONGO_URI and JWT_SECRET
npm run dev
```

**Expected Output:**
```
Starting MySchoolHelper backend...
Port: 5000
Registering API routes...
Loaded authRoutes
Loaded classRoutes
Loaded studentRoutes
Loaded attendanceRoutes
Loaded adminRoutes
API routes registered.
Server running on http://0.0.0.0:5000
MongoDB Connected ✓
```

### Frontend Setup

**Terminal 2:**
```bash
cd frontend
npm install
cp .env.example .env
npm run dev
```

**Expected Output:**
```
Local:   http://localhost:3000
press q to quit
```

### Access Application
Open browser: **http://localhost:3000**

---

## 🔓 Demo Accounts

Use these credentials to test different roles:

### Teacher Login
```
Email:    teacher@school.com
Password: password123
```

### Student Login
```
Email:    student@school.com
Password: password123
```

### Admin Login
```
Email:    admin@school.com
Password: password123
```

---

## 📋 Step-by-Step Test Scenarios

### Scenario 1: Teacher - Mark Attendance

**Steps:**
1. Login with teacher account
2. Select a class from dropdown
3. Select a section
4. See list of students
5. Mark attendance (click Present/Absent/Late/Leave for each)
6. Click "Submit Attendance"
7. See success message

**Expected Results:**
- ✅ All students visible in table
- ✅ Attendance status changes on button click
- ✅ Submit shows success toast
- ✅ Attendance records saved in database

**Test for Duplicate Prevention:**
- Go back to same class/section
- Select today's date again
- Should see "Attendance already marked"
- Click "View Existing" button
- Should show previous markings

### Scenario 2: Teacher - View & Export Attendance

**Steps:**
1. Login as teacher
2. Navigate to "View Attendance"
3. Select class and section
4. Select date range
5. Click "Export as CSV"
6. Open CSV in Excel/Sheets

**Expected Results:**
- ✅ Records display in table
- ✅ Filter works correctly
- ✅ CSV file downloads
- ✅ CSV contains all attendance data

### Scenario 3: Student - View Personal Attendance

**Steps:**
1. Login with student account
2. View dashboard (should show stats)
3. Navigate to "View My Attendance"
4. Select a month
5. See all records for that month
6. Click "Download Report"

**Expected Results:**
- ✅ Dashboard shows attendance percentage
- ✅ Shows Present/Absent/Late counts
- ✅ Monthly filter works
- ✅ Records display correctly
- ✅ CSV downloads with summary

### Scenario 4: Admin - Manage Users

**Steps:**
1. Login as admin
2. Navigate to "Manage Users"
3. Click "Add New User"
4. Enter name, email, password, role
5. Click "Save"
6. User appears in list
7. Click edit to modify
8. Click delete to remove

**Expected Results:**
- ✅ Form validates input
- ✅ User created in database
- ✅ Edit updates user info
- ✅ Delete removes user
- ✅ Role dropdown has 3 options

### Scenario 5: Admin - View Attendance Reports

**Steps:**
1. Login as admin
2. Navigate to "Attendance Reports"
3. Search by student name
4. Filter by class and date
5. See comprehensive statistics
6. Click export button

**Expected Results:**
- ✅ Search finds students
- ✅ Filters narrow results
- ✅ Stats show totals (Present/Absent/Late)
- ✅ Export downloads complete report

### Scenario 6: Security - Unauthorized Access

**Steps:**
1. Login as student
2. Try to visit `/admin` path
3. Try to visit `/teacher` path
4. Try to call admin API directly (DevTools)

**Expected Results:**
- ✅ Redirected to student dashboard
- ✅ Cannot access other role pages
- ✅ API returns 403 Forbidden
- ✅ No data leaks to unauthorized users

---

## 🔧 Manual API Testing (Postman/Curl)

### Test Login Endpoint

```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teacher@school.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "user": {
    "id": "...",
    "name": "Teacher Name",
    "email": "teacher@school.com",
    "role": "teacher"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR..."
}
```

### Test Mark Attendance

```bash
curl -X POST http://localhost:5000/api/attendance/mark \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "class_id": "CLASS_ID",
    "section_id": "SECTION_ID",
    "students": [
      { "student_id": "STU1", "status": "Present" },
      { "student_id": "STU2", "status": "Absent" }
    ],
    "date": "2024-01-15"
  }'
```

### Test Get Students

```bash
curl -X GET "http://localhost:5000/api/students?classId=CLASS_ID&sectionId=SECTION_ID" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🧪 Test Coverage Checklist

### Authentication Tests
- [ ] Login with correct credentials → Success
- [ ] Login with wrong password → Error
- [ ] Login with non-existent email → Error
- [ ] Access protected route without token → 401 Unauthorized
- [ ] Access protected route with expired token → 401 Unauthorized
- [ ] Logout clears token → True

### Authorization Tests
- [ ] Student cannot access teacher endpoints → Forbidden
- [ ] Teacher cannot access admin endpoints → Forbidden
- [ ] Teacher can access teacher endpoints → Success
- [ ] Admin can access all endpoints → Success

### Attendance Tests
- [ ] Mark attendance for students → Success
- [ ] Try to mark duplicate → Error (already marked)
- [ ] Update existing attendance → Success
- [ ] View attendance records → Success
- [ ] Filter by date range → Success
- [ ] Export CSV → File downloads

### User Management Tests
- [ ] Create new user → Success
- [ ] Create user with duplicate email → Error
- [ ] Edit user details → Success
- [ ] Delete user → Success
- [ ] Delete user removes all references → Clean up

### UI Tests
- [ ] All buttons respond to clicks → Yes
- [ ] Forms validate input → Yes
- [ ] Error messages display → Yes
- [ ] Success messages display → Yes
- [ ] Loading states show → Yes
- [ ] Mobile responsive → Yes

### Performance Tests
- [ ] Login completes < 2 seconds → Yes
- [ ] Mark attendance < 1 second → Yes
- [ ] Load student list < 1 second → Yes
- [ ] Export CSV < 2 seconds → Yes
- [ ] No console errors → Yes

---

## 🐛 Debugging Tips

### Backend Issues

**Check Backend Logs:**
```bash
# Terminal 1 - Backend logs appear here
# Look for error messages with timestamps
```

**MongoDB Connection Issues:**
```bash
# Test MongoDB connection
mongo "mongodb://localhost:27017"
# or
mongosh "mongodb://localhost:27017"
```

**JWT Token Issues:**
```javascript
// In browser console:
localStorage.getItem('token')  // Should show token
localStorage.getItem('user')   // Should show user info
```

### Frontend Issues

**Check Network Requests:**
1. Open DevTools (F12)
2. Go to Network tab
3. Perform action (e.g., login)
4. Check requests and responses
5. Look for errors or unexpected responses

**Check Console Errors:**
1. Open DevTools (F12)
2. Go to Console tab
3. Look for red error messages
4. Click to expand and see full error

**Clear Cache & Reload:**
```javascript
// In browser console:
localStorage.clear()
location.reload()
```

---

## 📊 Expected Database Collections

After first run, you should see these collections in MongoDB:

```javascript
db.users              // User accounts
db.students           // Student profiles
db.teachers           // Teacher profiles
db.classes            // Classes
db.sections           // Sections within classes
db.attendances        // Attendance records
```

**View collections:**
```bash
# MongoDB shell
use myschoolhelper
show collections
```

---

## 🔐 Security Testing

### Test Password Hashing
```bash
# In backend, check user password in DB
# It should be: $2a$12$... (bcrypt hash)
# NOT plain text
```

### Test JWT Token
```javascript
// Decode token (don't use for security, just debugging)
// Go to jwt.io
// Paste token in left panel
// Should see: { sub, email, role, iat, exp }
```

### Test CORS
```bash
# CORS should work for requests from http://localhost:3000
# Try same request from different origin should fail
```

---

## 📱 Mobile Testing

### Desktop Browser
```
DevTools → F12 → Ctrl+Shift+M
or
Toggle device toolbar icon
```

**Test Devices:**
- iPhone 12 (390 × 844)
- iPad (768 × 1024)
- Android Phone (360 × 800)
- Responsive (drag to resize)

### Responsive Checklist
- [ ] Menu items stack vertically
- [ ] Buttons are touch-friendly (44px+ height)
- [ ] Tables scroll horizontally
- [ ] Forms fit on screen
- [ ] Text is readable
- [ ] Images scale correctly

---

## 🚢 Pre-Deployment Checklist

### Backend
- [ ] `.env` configured with production values
- [ ] `NODE_ENV=production`
- [ ] MongoDB Atlas URI added
- [ ] JWT_SECRET is strong (32+ characters)
- [ ] FRONTEND_URL points to production URL
- [ ] No console.log in critical paths
- [ ] Error handling complete
- [ ] Security headers configured

### Frontend
- [ ] `.env` configured with production API URL
- [ ] `VITE_API_URL=https://api.production.com`
- [ ] Build completes without warnings
- [ ] No sensitive data in code
- [ ] No console errors on load
- [ ] Auth token handling secure
- [ ] All API calls use .env baseURL

### Database
- [ ] MongoDB Atlas setup
- [ ] Indexes created
- [ ] Backups configured
- [ ] Connection string tested
- [ ] Read/write permissions set

### Monitoring
- [ ] Error tracking setup (Sentry, etc.)
- [ ] Performance monitoring (New Relic, etc.)
- [ ] Logs collection setup
- [ ] Alerts configured
- [ ] Health checks setup

---

## 📞 Troubleshooting by Symptom

### "Cannot login"
**Possible Causes:**
1. Backend not running → Start with `npm run dev`
2. Wrong credentials → Check demo credentials above
3. User doesn't exist → Check database has users
4. JWT_SECRET missing → Add to .env

**Fix:**
```bash
# Check backend is running
curl http://localhost:5000

# Check users exist
mongo → use myschoolhelper → db.users.find()
```

### "CORS Error"
**Possible Causes:**
1. Backend not running
2. FRONTEND_URL wrong in backend .env
3. Backend CORS not configured

**Fix:**
```javascript
// In backend/server.js - verify CORS config:
app.use(cors({
  origin: '*',  // or specific URL
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
```

### "Database connection failed"
**Possible Causes:**
1. MongoDB not running locally
2. MONGO_URI wrong in .env
3. MongoDB server down

**Fix:**
```bash
# Start MongoDB locally (macOS):
brew services start mongodb-community

# Or use MongoDB Atlas (cloud):
# Create free cluster at mongodb.com/cloud
# Copy connection string to .env
```

### "Attendance mark fails with duplicate error"
**This is expected behavior!**

The system has a unique index preventing duplicate marks for the same student on the same date.

**Options:**
1. Click "View Existing" to see previous marking
2. Use "Edit Attendance" to update
3. Use UPDATE endpoint instead

---

## 🎯 Success Indicators

When everything works, you should see:

✅ **Login Success**
- No errors in console
- Redirected to dashboard
- Token stored in localStorage
- User info displayed in header

✅ **Mark Attendance Success**
- Students list loads
- Status buttons respond
- Submit completes
- Success toast appears
- Records appear in database

✅ **View Records Success**
- Previous records display
- Filters work
- CSV exports successfully
- No console errors

✅ **Admin Functions Success**
- Users can be created
- Users can be edited
- Users can be deleted
- Reports generate correctly

---

## 📊 Performance Benchmarks

**Expected Response Times:**
- Login: < 500ms
- Get Students: < 500ms
- Mark Attendance: < 1000ms
- View Records: < 500ms
- Export CSV: < 2000ms

**Browser Performance:**
- First Load: < 3 seconds
- Page Switch: < 500ms
- Form Submit: < 1 second

**Network:**
- Requests: 20-30 on first load
- Bundle Size: < 300KB gzipped
- API Calls: < 50KB average

---

## 🎓 Next Steps After Testing

1. **Document Issues** → Create GitHub issues
2. **Create Seed Data** → Run demo data script
3. **Setup Monitoring** → Configure error tracking
4. **Deploy Backend** → Push to Heroku/Railway
5. **Deploy Frontend** → Push to Vercel/Netlify
6. **Setup Custom Domain** → Point to production
7. **Enable HTTPS** → Get SSL certificate
8. **Monitor Performance** → Watch dashboards

---

## 📞 Support

**If something doesn't work:**

1. Check this guide's troubleshooting section
2. Review the SETUP_GUIDE.md
3. Check backend console for error messages
4. Check browser console (F12) for errors
5. Verify MongoDB is running
6. Verify all .env files configured
7. Try hard refresh (Ctrl+Shift+R)

---

## 🎉 Ready to Go!

Your attendance system is production-ready and fully functional.

**Start with:** `npm run dev` in both terminal windows, then open http://localhost:3000

**Happy testing! 🚀**

---

**Document Version**: 1.0  
**Last Updated**: December 2024  
**Status**: ✅ Production Ready
