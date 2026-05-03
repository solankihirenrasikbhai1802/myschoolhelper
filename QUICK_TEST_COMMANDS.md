# Quick Testing Commands

## Verify Backend is Running

```powershell
# Check port 5000 is listening
netstat -an | findstr "5000"

# Expected: TCP    0.0.0.0:5000           0.0.0.0:0              LISTENING
```

## Test API Endpoints from Laptop

```bash
# Health Check
curl http://127.0.0.1:5000/

# With more details
curl -v http://127.0.0.1:5000/

# Expected response: "MySchoolHelper API is running..."
```

## Test from Another Device on Same Network

Replace `10.224.192.184` with your laptop's IP if different.

```bash
# Health Check
curl http://10.224.192.184:5000/

# Login API (example)
curl -X POST http://10.224.192.184:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Attendance API (requires auth token)
curl http://10.224.192.184:5000/api/attendance/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Check Firewall Rules

```powershell
# List all rules for port 5000
Get-NetFirewallRule -DisplayName "*5000*" | Select-Object DisplayName, Action, Direction, Enabled

# Create rule if missing
New-NetFirewallRule -DisplayName "Node.js Port 5000" -Direction Inbound -Action Allow -Protocol TCP -LocalPort 5000
```

## Rebuild Android App

```bash
# Navigate to project root
cd c:\Users\acer\AndroidStudioProjects\MYSCHOOLHELPER

# Clean build
./gradlew clean
./gradlew build

# Or in Android Studio:
# Build → Clean Project
# Build → Rebuild Project
```

## Deploy and Test

```bash
# Deploy to device
./gradlew installDebug

# Or use Android Studio:
# Select device
# Run → Run 'app'
```

## Check Logs in Android Studio

```
View → Tool Windows → Logcat

Filter by:
- RetrofitClient (to see HTTP client logs)
- AttendanceApi (to see API calls)
- AuthApi (to see auth calls)
- MySchoolHelper (to see app logs)

Look for successful connection logs:
- "Server running on http://0.0.0.0:5000"
- Successful HTTP responses (200, 201, etc.)
```

## Verify Mobile Can Reach Backend

**On your mobile device, open browser and visit**:
```
http://10.224.192.184:5000/
```

**Expected**: Should show "MySchoolHelper API is running..."

---

## Important IP Address

**Your Laptop IP for this session**: `10.224.192.184`

If IP changes, update:
```kotlin
// app/src/main/java/com/example/myschoolhelper/data/remote/RetrofitClient.kt
private const val BASE_URL = "http://NEW_IP_HERE:5000/"
```

