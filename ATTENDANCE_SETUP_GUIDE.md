# 🎯 Attendance System - Step-by-Step Setup Guide

## शुरुआत करें (Get Started)

यह guide आपको सभी errors को fix करने में मदद करेगी।

---

## Step 1️⃣: Gradle Sync करें

### Android Studio में:
1. **File** → **Sync Now** क्लिक करें
2. Sync complete होने का इंतजार करें
3. अगर error आए तो **Build** → **Rebuild Project** करें

### Terminal में (Alternative):
```bash
cd c:\Users\acer\AndroidStudioProjects\MYSCHOOLHELPER
./gradlew clean
./gradlew build
```

---

## Step 2️⃣: AndroidManifest.xml को Update करें

**File Location**: `app/src/main/AndroidManifest.xml`

यह content देखें और update करें:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example.myschoolhelper">

    <!-- ✅ ADD: Internet permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    <application
        android:name=".MySchoolHelperApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:debuggable="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MySchoolHelper"
        tools:targetApi="31">

        <!-- ✅ ADD: FileProvider for exports -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.example.myschoolhelper.fileprovider"
            android:exported="false">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

---

## Step 3️⃣: MySchoolHelperApp.kt को Update करें

**File Location**: `app/src/main/java/com/example/myschoolhelper/MySchoolHelperApp.kt`

```kotlin
package com.example.myschoolhelper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.util.Log

@HiltAndroidApp
class MySchoolHelperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MySchoolHelperApp", "App Started")
        // Initialize components
    }
}
```

---

## Step 4️⃣: MainActivity.kt को Update करें

**File Location**: `app/src/main/java/com/example/myschoolhelper/MainActivity.kt`

```kotlin
package com.example.myschoolhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Your existing UI or navigation
            MySchoolHelperTheme {
                // Add your navigation here
            }
        }
    }
}
```

---

## Step 5️⃣: file_paths.xml बनाएं

**Path**: `app/src/main/res/xml/file_paths.xml`

1. Android Studio में right-click करें: `app/src/main/res`
2. **New** → **Directory** चुनें
3. `xml` नाम दें
4. फिर **New** → **File** चुनें
5. `file_paths.xml` नाम दें

**Content**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <cache-path name="cache" path="/" />
    <files-path name="files" path="/" />
    <external-files-path name="external" path="/" />
    <external-cache-path name="external_cache" path="/" />
</paths>
```

---

## Step 6️⃣: RetrofitClient.kt में Base URL Update करें

**File Location**: `app/src/main/java/com/example/myschoolhelper/data/remote/RetrofitClient.kt`

यह line find करें:
```kotlin
private const val BASE_URL = "http://10.224.192.184:5000/"
```

अपना IP address डालें:
```kotlin
private const val BASE_URL = "http://YOUR_IP_ADDRESS:5000/"
```

**IP कैसे खोजें?**
```bash
# Windows में Command Prompt में:
ipconfig

# Linux/Mac में Terminal में:
ifconfig
```

---

## Step 7️⃣: Build & Test करें

### Android Studio में:
1. **Build** → **Build Project** क्लिक करें
2. Build successful होने का इंतजार करें
3. अगर कोई error नहीं है तो ✅

### Terminal में:
```bash
./gradlew build --info
```

---

## 🔧 Common Errors और समाधान

### Error 1: "Unresolved reference"
```
समाधान:
1. File → Invalidate Caches → Invalidate and Restart
2. फिर Gradle Sync करें
```

### Error 2: "Cannot resolve symbol 'HiltAndroidApp'"
```
समाधान:
1. MySchoolHelperApp.kt में import add करें
2. import dagger.hilt.android.HiltAndroidApp
3. @HiltAndroidApp annotation add करें
```

### Error 3: "Gradle build failed"
```
समाधान:
./gradlew clean
./gradlew build
```

### Error 4: "FileProvider not found"
```
समाधान:
1. file_paths.xml file create करें
2. AndroidManifest.xml में provider tag add करें
3. Gradle sync करें
```

---

## ✅ Verification Checklist

- [ ] Gradle Sync successful
- [ ] No error lines in code
- [ ] Build succeeds
- [ ] AndroidManifest.xml updated
- [ ] MySchoolHelperApp.kt has @HiltAndroidApp
- [ ] MainActivity.kt has @AndroidEntryPoint
- [ ] file_paths.xml created
- [ ] Base URL configured

---

## 🚀 Attendance Features को Use करना

### Navigation में add करें:

अपनी Navigation file (जहाँ आप सभी screens define करते हैं) में यह add करें:

```kotlin
import com.example.myschoolhelper.ui.screens.AttendanceSystemMainScreen
import com.example.myschoolhelper.ui.screens.AttendanceNavigationEvent

composable("attendance") {
    AttendanceSystemMainScreen(
        classId = "CLASS-001",
        studentId = "STUDENT-001",
        userRole = "teacher", // teacher, student, admin
        onNavigationEvent = { event ->
            when (event) {
                AttendanceNavigationEvent.Back -> navController.popBackStack()
                else -> {}
            }
        }
    )
}
```

### या Direct Screen कॉल करें:

```kotlin
// Mark Attendance
AttendanceMarkingScreen(
    classId = "CLASS-001",
    onBackClick = { /* back action */ }
)

// View Statistics
AttendanceStatsDashboard(
    studentId = "STUDENT-001",
    onBackClick = { /* back action */ }
)

// View Reports
AttendanceReportsScreen(
    classId = "CLASS-001",
    onBackClick = { /* back action */ }
)
```

---

## 💾 Backend Setup

अगर backend नहीं चल रहा:

```bash
# Backend folder में जाएं
cd backend

# Dependencies install करें
npm install

# .env file create करें
# Add करें:
# MONGODB_URI=mongodb://localhost:27017/myschoolhelper
# JWT_SECRET=your_secret_key
# API_PORT=5000

# Test data seed करें
node seedAttendanceData.js

# Server start करें
npm start
```

Server चलेगा: `http://localhost:5000`

---

## 🧪 Test करें

### Run करें:

```bash
# Project root में
./gradlew connectedAndroidTest
```

### या Android Studio में:
1. Device/Emulator connect करें
2. **Run** → **Run 'app'** क्लिक करें

---

## 📝 Important Notes

1. **API Base URL** सही होना चाहिए (Step 6 check करें)
2. **Backend** running होना चाहिए
3. **Network connectivity** required है
4. **Permissions** AndroidManifest में होने चाहिए
5. **Hilt annotations** सभी जगह होने चाहिए

---

## 🎯 Final Result

सब कुछ सही तरीके से setup करने के बाद:

✅ Attendance mark कर सकते हैं  
✅ Statistics देख सकते हैं  
✅ Reports generate कर सकते हैं  
✅ Data export कर सकते हैं  
✅ Smooth UI animations देख सकते हैं  

---

## 📞 अगर अभी भी Error है

1. **Logs check करें**: Android Studio के Logcat में
2. **Build Output check करें**: Build section में
3. **Errors and Warnings check करें**: सभी red lines को fix करें
4. **Documentation read करें**: ATTENDANCE_ERROR_FIX.md

---

## 🎉 Congratulations!

आप अब सभी setup कर चुके हैं। अब attendance system use कर सकते हैं!

---

**Next**: Read **ATTENDANCE_QUICK_START.md** for usage guide

**Status**: ✅ Setup Complete  
**Version**: 1.0.0  
**Date**: April 21, 2026
