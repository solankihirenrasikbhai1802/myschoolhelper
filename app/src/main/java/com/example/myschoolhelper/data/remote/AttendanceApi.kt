package com.example.myschoolhelper.data.remote

import com.example.myschoolhelper.data.Attendance
import com.example.myschoolhelper.data.AttendanceStatistics
import com.example.myschoolhelper.data.MonthlyReport
import com.example.myschoolhelper.data.model.AttendanceRecord
import com.example.myschoolhelper.data.model.MarkAttendanceRequest
import com.example.myschoolhelper.data.model.StudentForAttendance
import retrofit2.Response
import retrofit2.http.*

interface AttendanceApi {
    // Get students for marking attendance
    @GET("api/attendance/students")
    suspend fun getStudentsForAttendance(): Response<List<StudentForAttendance>>

    // Get current student's own attendance
    @GET("api/attendance/my")
    suspend fun getMyAttendance(): Response<List<AttendanceRecord>>

    // Mark attendance for a single student
    @POST("api/attendance/mark")
    suspend fun markAttendance(@Body request: MarkAttendanceRequest): Response<Map<String, Any>>

    // Get specific student's attendance records
    @GET("api/attendance/student/{id}")
    suspend fun getStudentAttendance(
        @Path("id") studentId: String,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): Response<List<Attendance>>

    // Get attendance for a specific class on a date
    @GET("api/attendance/class/{id}")
    suspend fun getClassAttendance(
        @Path("id") classId: String,
        @Query("date") date: String
    ): Response<List<Attendance>>

    // Get attendance statistics
    @GET("api/attendance/stats")
    suspend fun getAttendanceStats(
        @Query("studentId") studentId: String?,
        @Query("classId") classId: String?,
        @Query("month") month: String?
    ): Response<AttendanceStatistics>

    // Get monthly attendance report
    @GET("api/attendance/report/monthly")
    suspend fun getMonthlyReport(
        @Query("studentId") studentId: String?,
        @Query("classId") classId: String?,
        @Query("month") month: String
    ): Response<MonthlyReport>

    // Update attendance record
    @PUT("api/attendance/{id}")
    suspend fun updateAttendance(
        @Path("id") attendanceId: String,
        @Body body: Map<String, String>
    ): Response<Attendance>

    // Delete attendance record
    @DELETE("api/attendance/{id}")
    suspend fun deleteAttendance(
        @Path("id") attendanceId: String
    ): Response<Map<String, Any>>

    // Get bulk attendance report for date range
    @GET("api/attendance/report/bulk")
    suspend fun getBulkAttendanceReport(
        @Query("classId") classId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<List<MonthlyReport>>
}
