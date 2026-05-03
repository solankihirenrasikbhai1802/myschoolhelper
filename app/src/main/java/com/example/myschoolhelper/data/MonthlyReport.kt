package com.example.myschoolhelper.data

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
