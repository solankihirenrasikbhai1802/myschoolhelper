package com.example.myschoolhelper.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
        const val KEY_SCHOOL_ID = "school_id"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(token: String, name: String, email: String, role: String, schoolId: String?) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_ROLE, role)
        editor.putString(KEY_SCHOOL_ID, schoolId)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUserName(): String = prefs.getString(KEY_NAME, "User") ?: "User"
    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun getUserRole(): String = prefs.getString(KEY_ROLE, "") ?: ""
    fun getSchoolId(): String? = prefs.getString(KEY_SCHOOL_ID, null)

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
