# Attendance System - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### Prerequisites
- Node.js 14+ and npm
- Android Studio 4.2+
- MongoDB installed and running
- Kotlin 1.8+

### Backend Setup

#### 1. Install Dependencies
```bash
cd backend
npm install
```

#### 2. Configure Environment
```bash
# Create .env file
MONGODB_URI=mongodb://localhost:27017/myschoolhelper
JWT_SECRET=your_jwt_secret_here
API_PORT=5000
NODE_ENV=development
```

#### 3. Seed Test Data
```bash
node seedAttendanceData.js
```

#### 4. Start Backend Server
```bash
npm start
```
Server will run on `http://localhost:5000`

### Android Setup

#### 1. Add Dependencies (build.gradle.kts)
```kotlin
dependencies {
    // Core
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.foundation:foundation:1.5.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    
    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    
    // Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
    testImplementation("junit:junit:4.13.2")
}

plugins {
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}
```

#### 2. Update AndroidManifest.xml
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myschoolhelper">
    
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        android:name=".MySchoolHelperApp"
        ...>
        <!-- Your activities -->
    </application>
</manifest>
```

#### 3. Create Application Class
```kotlin
// MySchoolHelperApp.kt
@HiltAndroidApp
class MySchoolHelperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize app
    }
}
```

#### 4. Update MainActivity
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySchoolHelperTheme {
                AttendanceSystemMainScreen(
                    classId = "CLASS-001",
                    studentId = "STUDENT-001",
                    userRole = "teacher",
                    onNavigationEvent = { event ->
                        when (event) {
                            AttendanceNavigationEvent.Back -> onBackPressedDispatcher.onBackPressed()
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}
```

## 📱 Using the Attendance System

### Teacher Workflow

#### Mark Attendance
1. Open Attendance System
2. Click "Mark Attendance"
3. Select Date (defaults to today)
4. Click on student status to change
5. Use "Mark All Present/Absent" for bulk operations
6. Submit attendance

#### View Statistics
1. Click "View Statistics"
2. Select Month
3. View attendance percentage and breakdown
4. Check warnings if any

#### Generate Reports
1. Click "Attendance Reports"
2. Choose report type (Monthly/Bulk)
3. Select date range or month
4. View or export report

### Student Workflow

#### Check Personal Attendance
1. View Statistics (shows personal data)
2. Monthly reports show attendance percentage
3. Alerts show if attendance is low

## 🔧 API Endpoints Reference

```
# Mark Attendance
POST /api/attendance/mark
Body: {
    "classId": "CLASS-001",
    "date": "2024-01-15",
    "attendance": [
        {
            "studentId": "STU-001",
            "status": "Present"
        }
    ]
}

# Get Class Attendance
GET /api/attendance/class/CLASS-001?date=2024-01-15

# Get Student Attendance
GET /api/attendance/student/STU-001?startDate=2024-01-01&endDate=2024-01-31

# Get Attendance Statistics
GET /api/attendance/stats?studentId=STU-001&month=01-2024

# Get Monthly Report
GET /api/attendance/report/monthly?classId=CLASS-001&month=01-2024

# Get Bulk Report
GET /api/attendance/report/bulk?classId=CLASS-001&startDate=2024-01-01&endDate=2024-01-31

# Update Attendance
PUT /api/attendance/ATT-001
Body: {
    "status": "Late",
    "remarks": "Late arrival"
}

# Delete Attendance
DELETE /api/attendance/ATT-001
```

## 🎨 Key Features

### Interactive Marking
- ✅ Real-time status selection
- ✅ Bulk operations
- ✅ Quick filters
- ✅ Live statistics

### Statistics Dashboard
- ✅ Overall percentage
- ✅ Monthly breakdown
- ✅ Trend analysis
- ✅ Alert system

### Reports & Export
- ✅ Monthly reports
- ✅ Bulk reports
- ✅ Export to CSV/PDF
- ✅ Share functionality

### Smart Features
- ✅ Date validation
- ✅ Threshold checking
- ✅ Status-based coloring
- ✅ Animated transitions

## 📊 Data Models

### Attendance Record
```kotlin
data class Attendance(
    val id: String,
    val date: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val classId: String,
    val status: String, // Present, Absent, Late, Leave
    val remarks: String
)
```

### Statistics
```kotlin
data class AttendanceStatistics(
    val presentPercentage: Double,
    val absentPercentage: Double,
    val latePercentage: Double,
    val leavePercentage: Double,
    val presentDays: Int,
    val absentDays: Int,
    val lateDays: Int,
    val leaveDays: Int,
    val totalDays: Int,
    val trend: List<Map<String, Any>>,
    val warnings: List<String>
)
```

### Monthly Report
```kotlin
data class MonthlyReport(
    val month: String,
    val presentDays: Int,
    val absentDays: Int,
    val lateDays: Int,
    val leaveDays: Int,
    val totalDays: Int,
    val attendancePercentage: Double,
    val trend: String,
    val remarks: String
)
```

## 🔒 Security

- JWT token authentication
- Role-based access control
- Input validation
- SQL injection prevention
- CORS configuration

## 📝 Common Tasks

### Change API Base URL
```kotlin
// In your Retrofit setup
val retrofit = Retrofit.Builder()
    .baseUrl("http://your-api.com/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

### Filter Attendance by Status
```kotlin
val filteredList = attendanceList.filter { it.status == "Present" }
```

### Calculate Attendance Percentage
```kotlin
val percentage = AttendanceUtils.calculateAttendancePercentage(
    presentDays = 18,
    totalDays = 21
)
```

### Export Report
```kotlin
val file = AttendanceUtils.generateCSVReport(
    fileName = "attendance_report",
    attendanceList = attendanceList
)
AttendanceUtils.shareFile(context, file, "text/csv")
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| API connection failed | Check backend is running on correct port |
| Authentication error | Verify JWT token is set in API interceptor |
| UI not updating | Ensure ViewModel StateFlow is collected |
| Slow performance | Implement pagination for large lists |
| Date format error | Use "yyyy-MM-dd" format for all dates |

## 📚 Documentation

- Full guide: [ATTENDANCE_SYSTEM_GUIDE.md](./ATTENDANCE_SYSTEM_GUIDE.md)
- API documentation: See backend routes
- Compose documentation: [official docs](https://developer.android.com/jetpack/compose)

## 🎯 Next Steps

1. ✅ Backend setup and running
2. ✅ Android project configured
3. ✅ Start the app and test
4. ✅ Customize for your needs

## 📞 Support

For issues or questions:
1. Check error logs in Android Studio
2. Review API response in backend
3. Verify network connectivity
4. Check data format and validation

## 🎉 You're Ready!

The attendance system is now ready for use. Happy coding!

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Status**: Production Ready
