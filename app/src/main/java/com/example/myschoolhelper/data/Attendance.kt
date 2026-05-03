package com.example.myschoolhelper.data

data class Attendance(
    val id: String,
    val date: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val classId: String,
    val status: String,
    val remarks: String
)
