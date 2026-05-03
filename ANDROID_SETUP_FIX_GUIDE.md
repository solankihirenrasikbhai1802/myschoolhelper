# Android App Setup Guide - Red Line Error Fixes

## 🔴 If you're seeing red lines in Android Studio

Red lines usually appear due to:

### 1. **Backend URL Configuration**
- The app uses a hardcoded backend URL which may not match your environment
- **Fix**: Edit this file: `app/src/main/java/com/example/myschoolhelper/config/AppConfig.kt`
- Change `BACKEND_BASE_URL` to your backend address:

```kotlin
// For emulator testing:
const val BACKEND_BASE_URL = "http://10.0.2.2:5000/"

// For physical device on same network:
const val BACKEND_BASE_URL = "http://192.168.1.YOUR_PC_IP:5000/"

// For specific device/network:
const val BACKEND_BASE_URL = "http://YOUR_BACKEND_IP:5000/"
```

### 2. **Sync Gradle**
- Click: `File > Sync Now` in Android Studio
- Or: `Ctrl + Shift + S`

### 3. **Invalidate Caches**
If errors persist:
- Click: `File > Invalidate Caches`
- Select: `Invalidate and Restart`
- Click: `Invalidate and Restart`

### 4. **Clean Build**
```bash
# In Android Studio Terminal:
./gradlew clean build
```

### 5. **Rebuild Project**
- Click: `Build > Rebuild Project`
- Or: `Ctrl + Shift + F9`

---

## 📱 Backend Connection Test

### Check if Backend is Running
```bash
# Test if backend is accessible:
curl http://YOUR_BACKEND_IP:5000/api/health
# Expected response: 200 OK
```

### Backend IP Detection
- **Windows (Find Your PC's IP)**:
```powershell
ipconfig | findstr "IPv4"
```

- **Use that IP in AppConfig.kt**:
```kotlin
const val BACKEND_BASE_URL = "http://192.168.X.X:5000/"
```

---

## 🔧 Troubleshooting Red Lines

| Red Line | Cause | Fix |
|----------|-------|-----|
| `Cannot resolve symbol` | Missing imports or build issue | Sync Gradle (`Ctrl+Shift+S`) |
| `Type mismatch` | API response model issue | Check RetrofitClient.kt imports |
| `Unresolved reference` | Hilt dependency missing | Click "Build > Rebuild Project" |
| `Package not found` | Gradle sync failed | Run `./gradlew clean build` |

---

## ✅ Verification

After fixes, you should see:
- ✅ No red underlines in `.kt` files
- ✅ All imports resolved
- ✅ Build successful
- ✅ Can run app on emulator/device

---

## 🚀 Running the App

```bash
# Build APK
./gradlew build

# Run on emulator/device
./gradlew installDebug

# Or use Android Studio: Run > Run 'app'
```

---

## 📝 Backend Requirements

Ensure your backend server is running and accessible:
- Node.js backend should be running
- MongoDB should be connected
- All APIs should be responding

Test with:
```bash
curl http://YOUR_BACKEND_IP:5000/api/auth/health
```
