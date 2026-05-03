package com.example.myschoolhelper.config

/**
 * Application Configuration
 * Update these values based on your environment
 */
object AppConfig {
    // Backend server configuration
    // Your Laptop IP Address for connecting from Android
    const val BACKEND_BASE_URL = "http://10.224.192.184:5000/"
    
    // API endpoints
    const val ATTENDANCE_ENDPOINT = "api/attendance"
    const val AUTH_ENDPOINT = "api/auth"
    const val USERS_ENDPOINT = "api/users"
    
    // Timeouts (in seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    
    // App settings
    const val SESSION_TIMEOUT = 15 * 60 // 15 minutes in seconds
    const val LOG_LEVEL = "BODY" // DEBUG, BODY, HEADERS, NONE
}
