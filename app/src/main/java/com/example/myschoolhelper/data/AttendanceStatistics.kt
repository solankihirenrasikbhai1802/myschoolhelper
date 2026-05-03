package com.example.myschoolhelper.data

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
