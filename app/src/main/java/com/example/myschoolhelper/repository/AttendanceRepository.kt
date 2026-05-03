package com.example.myschoolhelper.repository

import android.util.Log
import com.example.myschoolhelper.data.remote.AttendanceApi
import com.example.myschoolhelper.data.Attendance
import com.example.myschoolhelper.data.AttendanceStatistics
import com.example.myschoolhelper.data.MonthlyReport
import com.example.myschoolhelper.data.model.AttendanceRecord
import com.example.myschoolhelper.data.model.MarkAttendanceRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class AttendanceRepository(private val attendanceApi: AttendanceApi) {

    // Get current logged-in student's own attendance (uses token for identification)
    fun getMyAttendance(): Flow<Result<List<AttendanceRecord>>> = flow {
        try {
            val response = attendanceApi.getMyAttendance()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch attendance: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getStudentsForAttendance(): Flow<Result<List<Any>>> = flow {
        try {
            val response = attendanceApi.getStudentsForAttendance()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch students: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getStudentAttendance(
        studentId: String,
        startDate: String? = null,
        endDate: String? = null
    ): Flow<Result<List<Attendance>>> = flow {
        try {
            val response = attendanceApi.getStudentAttendance(
                studentId,
                startDate,
                endDate
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch attendance: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // Updated to handle bulk attendance marking by looping through the list
    fun markAttendance(
        date: String,
        attendanceList: List<Map<String, Any>>
    ): Flow<Result<List<Map<String, Any>>>> = flow {
        try {
            val results = mutableListOf<Map<String, Any>>()
            for (item in attendanceList) {
                val studentId = item["studentId"] as? String ?: continue
                val status = item["status"] as? String ?: "Present"
                val request = MarkAttendanceRequest(
                    student_id = studentId,
                    date = date,
                    status = status
                )
                val response = attendanceApi.markAttendance(request)
                if (response.isSuccessful && response.body() != null) {
                    results.add(response.body()!!)
                } else {
                    emit(Result.failure(Exception("Failed to mark attendance for $studentId: ${response.message()}")))
                    return@flow
                }
            }
            emit(Result.success(results))
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun updateAttendance(
        attendanceId: String,
        status: String,
        remarks: String? = null
    ): Flow<Result<Attendance>> = flow {
        try {
            val response = attendanceApi.updateAttendance(
                attendanceId,
                mapOf(
                    "status" to status,
                    "remarks" to (remarks ?: "")
                )
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to update attendance: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getClassAttendance(
        classId: String,
        date: String
    ): Flow<Result<List<Attendance>>> = flow {
        try {
            val response = attendanceApi.getClassAttendance(classId, date)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch class attendance: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getAttendanceStats(
        studentId: String? = null,
        classId: String? = null,
        month: String? = null
    ): Flow<Result<AttendanceStatistics>> = flow {
        try {
            val response = attendanceApi.getAttendanceStats(studentId, classId, month)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch statistics: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getMonthlyReport(
        studentId: String? = null,
        classId: String? = null,
        month: String,
        year: String? = null
    ): Flow<Result<MonthlyReport>> = flow {
        try {
            val monthParam = if (year != null) "$year-$month" else month
            val response = attendanceApi.getMonthlyReport(studentId, classId, monthParam)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch monthly report: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteAttendance(attendanceId: String): Flow<Result<Map<String, Any>>> = flow {
        try {
            val response = attendanceApi.deleteAttendance(attendanceId)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to delete attendance: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getBulkAttendanceReport(
        classId: String,
        startDate: String,
        endDate: String
    ): Flow<Result<List<MonthlyReport>>> = flow {
        try {
            val response = attendanceApi.getBulkAttendanceReport(classId, startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch bulk report: ${response.message()}")))
            }
        } catch (e: HttpException) {
            Log.e("AttendanceRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            emit(Result.failure(e))
        } catch (e: IOException) {
            Log.e("AttendanceRepository", "IO Error: ${e.message}")
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Unexpected Error: ${e.message}")
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
