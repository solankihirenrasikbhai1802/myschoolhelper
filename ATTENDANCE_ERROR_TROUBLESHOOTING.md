# 🔴 Attendance System - Error Troubleshooting Guide

## सभी संभावित Errors और उनके समाधान

---

## ❌ Error 1: "Cannot resolve symbol 'Attendance'"

### कारण:
Data classes सही जगह पर नहीं हैं या import गलत है।

### समाधान:

**Step 1**: Check करें कि file exist करती है:
```
app/src/main/java/com/example/myschoolhelper/data/Attendance.kt
```

**Step 2**: File में correct package है:
```kotlin
package com.example.myschoolhelper.data
```

**Step 3**: Import करते समय सही path use करें:
```kotlin
import com.example.myschoolhelper.data.Attendance
```

**Step 4**: Gradle Sync करें:
```bash
./gradlew clean
./gradlew build
```

---

## ❌ Error 2: "Cannot resolve symbol 'AttendanceViewModel'"

### कारण:
ViewModel file missing या package गलत है।

### समाधान:

**Check करें**:
```
✅ app/src/main/java/com/example/myschoolhelper/viewmodel/AttendanceViewModel.kt
```

**Import करें**:
```kotlin
import com.example.myschoolhelper.viewmodel.AttendanceViewModel
```

**Screen में use करें**:
```kotlin
val viewModel: AttendanceViewModel = hiltViewModel()
```

---

## ❌ Error 3: "Cannot resolve symbol 'AttendanceRepository'"

### कारण:
Repository file गलत जगह है।

### समाधान:

**Check करें**:
```
✅ app/src/main/java/com/example/myschoolhelper/repository/AttendanceRepository.kt
```

**Package verify करें**:
```kotlin
package com.example.myschoolhelper.repository
```

**Gradle Sync करें**:
```bash
./gradlew --refresh-dependencies
./gradlew build
```

---

## ❌ Error 4: "Hilt: Unresolved reference to bin..."

### कारण:
Hilt dependency injection सही setup नहीं है।

### समाधान:

**1. Check MySchoolHelperApp.kt:**
```kotlin
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // ✅ यह होना चाहिए
class MySchoolHelperApp : Application() {
    // ...
}
```

**2. Check MainActivity.kt:**
```kotlin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint  // ✅ यह होना चाहिए
class MainActivity : ComponentActivity() {
    // ...
}
```

**3. Invalidate Cache करें:**
- File → Invalidate Caches
- Invalidate and Restart क्लिक करें
- Gradle sync करें

**4. Build करें:**
```bash
./gradlew build
```

---

## ❌ Error 5: "Cannot resolve Retrofit"

### कारण:
Retrofit dependency gradle में नहीं है।

### समाधान:

**Check करें** `build.gradle.kts`:
```kotlin
dependencies {
    implementation libs.retrofit  // ✅ होना चाहिए
    implementation libs.retrofit.gson  // ✅ होना चाहिए
    implementation libs.okhttp.logging  // ✅ होना चाहिए
}
```

**Gradle Sync करें**:
```bash
./gradlew build
```

---

## ❌ Error 6: "Cannot resolve 'hiltViewModel()'"

### कारण:
Compose Hilt navigation dependency missing है।

### समाधान:

**Check करें** `gradle/libs.versions.toml`:
```toml
hilt-navigation-compose = "1.2.0"  # ✅ होना चाहिए
```

**Check करें** `build.gradle.kts`:
```kotlin
implementation libs.hilt.navigation.compose
```

**फिर Sync करें**:
```bash
./gradlew build
```

---

## ❌ Error 7: "Execution failed for task ':app:kaptDebugKotlin'"

### कारण:
Kotlin Symbol Processing (KSP) में issue है।

### समाधान:

**Step 1**: gradle/libs.versions.toml check करें:
```toml
ksp = "2.0.21-1.0.25"
hilt = "2.52"
```

**Step 2**: build.gradle.kts में:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

dependencies {
    ksp "com.google.dagger:hilt-compiler:2.52"
}
```

**Step 3**: Clean करें:
```bash
./gradlew clean
./gradlew build
```

---

## ❌ Error 8: "Network error - Connection refused"

### कारण:
Backend server running नहीं है या BASE_URL गलत है।

### समाधान:

**1. Backend check करें:**
```bash
cd backend
npm start
```

Server चलना चाहिए: `http://localhost:5000`

**2. RetrofitClient.kt में BASE_URL check करें:**
```kotlin
private const val BASE_URL = "http://YOUR_IP:5000/"
```

सही IP डालें (localhost नहीं, actual IP)

**3. AndroidManifest.xml में permission check करें:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**4. Emulator settings check करें:**
- Internet permission enabled?
- Network connected?

---

## ❌ Error 9: "Unresolved reference to 'com.example.myschoolhelper'"

### कारण:
Package name गलत है।

### समाधान:

**Check करें** AndroidManifest.xml:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myschoolhelper">  <!-- ✅ यह सही होना चाहिए
```

**Check करें** build.gradle.kts:
```kotlin
android {
    namespace = "com.example.myschoolhelper"
}
```

**Match करें** सभी जगह same package name:
- AndroidManifest.xml
- build.gradle.kts
- सभी Kotlin files में

---

## ❌ Error 10: "FileProvider not found"

### कारण:
FileProvider entry AndroidManifest.xml में नहीं है।

### समाधान:

**1. file_paths.xml create करें:**
```
app/src/main/res/xml/file_paths.xml
```

**Content**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <cache-path name="cache" path="/" />
    <files-path name="files" path="/" />
</paths>
```

**2. AndroidManifest.xml में add करें:**
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="com.example.myschoolhelper.fileprovider"
    android:exported="false">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

**3. Gradle Sync करें**:
```bash
./gradlew build
```

---

## ❌ Error 11: "Deprecated API usage"

### कारण:
पुरानी library version use हो रही है।

### समाधान:

**Update करें** gradle/libs.versions.toml:
```toml
composeBom = "2024.12.01"  # Latest
material3 = "1.3.0"  # Latest
```

**फिर Sync करें**:
```bash
./gradlew build
```

---

## ❌ Error 12: "Test execution failed"

### कारण:
Test framework properly setup नहीं है।

### समाधान:

**1. build.gradle.kts में check करें:**
```kotlin
dependencies {
    testImplementation libs.junit
    androidTestImplementation libs.androidx.test.junit
    androidTestImplementation libs.compose.ui.test.junit4
}
```

**2. Test run करें:**
```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## ⚙️ Step-by-Step Debugging Process

### जब कोई error आए:

**Step 1**: Error message पूरा read करें
```
Error: [पूरा message]
Location: [file name:line number]
```

**Step 2**: File open करें और location check करें

**Step 3**: Import statements verify करें
```kotlin
import com.example.myschoolhelper.data.Attendance  // ✅
```

**Step 4**: Package names match करें
```kotlin
package com.example.myschoolhelper.data  // ✅
```

**Step 5**: Gradle Sync करें
```bash
./gradlew clean
./gradlew build
```

**Step 6**: Invalidate Cache करें
- File → Invalidate Caches
- Restart

**Step 7**: फिर से build करें
```bash
./gradlew build
```

---

## 📋 Error Prevention Checklist

- [ ] सभी files correct location पर हैं
- [ ] Package names everywhere match हैं
- [ ] सभी imports correct हैं
- [ ] Gradle dependencies सभी हैं
- [ ] build.gradle.kts updated है
- [ ] AndroidManifest.xml correct है
- [ ] MySchoolHelperApp.kt में @HiltAndroidApp है
- [ ] MainActivity.kt में @AndroidEntryPoint है
- [ ] Gradle Sync successful है
- [ ] Build succeeds है

---

## 🆘 अगर अभी भी नहीं समझ आया

### Logs को देखें:

**1. Logcat में**:
- Android Studio → Logcat tab
- Error message find करें
- Full stack trace देखें

**2. Build Output में**:
- Android Studio → Build section
- Error details देखें
- Line number note करें

**3. Terminal में**:
```bash
./gradlew build --info
./gradlew build --debug
```

---

## 🎯 Quick Fix Commands

```bash
# सबसे common issue
./gradlew clean
./gradlew build
./gradlew connectedAndroidTest

# Cache problem
rm -rf .gradle
rm -rf app/build
./gradlew build

# Sync problem
./gradlew --refresh-dependencies
./gradlew build

# Hilt problem
./gradlew clean
./gradlew build
# फिर File → Invalidate Caches करें
```

---

## 📚 Additional Resources

- **Main Guide**: ATTENDANCE_SYSTEM_README.md
- **Quick Start**: ATTENDANCE_QUICK_START.md
- **Setup Guide**: ATTENDANCE_SETUP_GUIDE.md
- **Project Structure**: ATTENDANCE_PROJECT_STRUCTURE.md
- **Component Index**: ATTENDANCE_COMPONENT_INDEX.md

---

## ✅ Success Indicators

अगर यह सब देख रहे हैं तो आप ready हैं:

- ✅ No red error lines
- ✅ Build successful
- ✅ Gradle sync complete
- ✅ APK generate होता है
- ✅ App runs on device/emulator
- ✅ API responses आते हैं
- ✅ UI screens दिखते हैं
- ✅ Data save होता है

---

**Remember**: Errors normal हैं! ये सिर्फ informational messages हैं जो सुधार किए जा सकते हैं।

Happy Debugging! 🐛✅

**Status**: Complete Guide  
**Date**: April 21, 2026
