package com.example.myschoolhelper.data.model

import com.google.gson.annotations.SerializedName

data class StudentForAttendance(
    val _id: String,
    val name: String,
    val email: String,
    @SerializedName("class") val class_: String,
    val section: String,
    val user_id: String
)

data class AttendanceRecord(
    val date: String,
    val status: String,
    val marked_by: String
)

data class MarkAttendanceRequest(
    val student_id: String,
    val date: String,
    val status: String
)