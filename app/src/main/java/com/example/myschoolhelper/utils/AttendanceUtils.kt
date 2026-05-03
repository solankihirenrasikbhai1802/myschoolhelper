package com.example.myschoolhelper.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.myschoolhelper.data.Attendance
import com.example.myschoolhelper.data.MonthlyReport
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AttendanceUtils {

    /**
     * Calculate attendance percentage
     */
    fun calculateAttendancePercentage(
        presentDays: Int,
        totalDays: Int
    ): Double {
        return if (totalDays > 0) {
            (presentDays.toDouble() / totalDays) * 100
        } else {
            0.0
        }
    }

    /**
     * Get attendance status color
     */
    fun getStatusColor(status: String): String {
        return when (status) {
            "Present" -> "#4CAF50"
            "Absent" -> "#F44336"
            "Late" -> "#FF9800"
            "Leave" -> "#2196F3"
            else -> "#808080"
        }
    }

    /**
     * Format date to readable format
     */
    fun formatDate(date: String, fromFormat: String = "yyyy-MM-dd", toFormat: String = "MMM dd, yyyy"): String {
        return try {
            val sdf = SimpleDateFormat(fromFormat, Locale.getDefault())
            val parsedDate = sdf.parse(date) ?: return date
            val outputSdf = SimpleDateFormat(toFormat, Locale.getDefault())
            outputSdf.format(parsedDate)
        } catch (e: Exception) {
            date
        }
    }

    /**
     * Get current month in MM format
     */
    fun getCurrentMonth(): String {
        val cal = Calendar.getInstance()
        return String.format("%02d", cal.get(Calendar.MONTH) + 1)
    }

    /**
     * Get current year
     */
    fun getCurrentYear(): String {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR).toString()
    }

    /**
     * Get current date in yyyy-MM-dd format
     */
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Generate CSV report
     */
    fun generateCSVReport(
        fileName: String,
        attendanceList: List<Attendance>
    ): File {
        val csv = StringBuilder()
        csv.append("Student Name,Roll Number,Date,Status,Remarks\n")

        attendanceList.forEach { attendance ->
            csv.append("\"${attendance.studentName}\",")
            csv.append("\"${attendance.rollNumber}\",")
            csv.append("\"${attendance.date}\",")
            csv.append("\"${attendance.status}\",")
            csv.append("\"${attendance.remarks}\"\n")
        }

        val file = File.createTempFile(fileName, ".csv")
        file.writeText(csv.toString())
        return file
    }

    /**
     * Generate HTML report
     */
    fun generateHTMLReport(
        fileName: String,
        report: MonthlyReport,
        title: String = "Attendance Report"
    ): File {
        val html = StringBuilder()
        html.append("<!DOCTYPE html>\n")
        html.append("<html>\n")
        html.append("<head>\n")
        html.append("<meta charset=\"UTF-8\">\n")
        html.append("<title>$title</title>\n")
        html.append("<style>\n")
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n")
        html.append("h1 { color: #333; }\n")
        html.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }\n")
        html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n")
        html.append("th { background-color: #4CAF50; color: white; }\n")
        html.append(".present { background-color: #d4edda; }\n")
        html.append(".absent { background-color: #f8d7da; }\n")
        html.append(".late { background-color: #fff3cd; }\n")
        html.append(".leave { background-color: #d1ecf1; }\n")
        html.append("</style>\n")
        html.append("</head>\n")
        html.append("<body>\n")
        html.append("<h1>$title</h1>\n")
        html.append("<p><strong>Month:</strong> ${report.month}</p>\n")
        html.append("<p><strong>Attendance Rate:</strong> ${String.format("%.1f%%", report.attendancePercentage)}</p>\n")
        html.append("<table>\n")
        html.append("<tr>\n")
        html.append("<th>Metric</th>\n")
        html.append("<th>Count</th>\n")
        html.append("</tr>\n")
        html.append("<tr>\n")
        html.append("<td>Present Days</td>\n")
        html.append("<td>${report.presentDays}</td>\n")
        html.append("</tr>\n")
        html.append("<tr>\n")
        html.append("<td>Absent Days</td>\n")
        html.append("<td>${report.absentDays}</td>\n")
        html.append("</tr>\n")
        html.append("<tr>\n")
        html.append("<td>Late Days</td>\n")
        html.append("<td>${report.lateDays}</td>\n")
        html.append("</tr>\n")
        html.append("<tr>\n")
        html.append("<td>Leave Days</td>\n")
        html.append("<td>${report.leaveDays}</td>\n")
        html.append("</tr>\n")
        html.append("<tr>\n")
        html.append("<td><strong>Total Days</strong></td>\n")
        html.append("<td><strong>${report.totalDays}</strong></td>\n")
        html.append("</tr>\n")
        html.append("</table>\n")
        html.append("</body>\n")
        html.append("</html>\n")

        val file = File.createTempFile(fileName, ".html")
        file.writeText(html.toString())
        return file
    }

    /**
     * Share file via intent
     */
    fun shareFile(context: Context, file: File, mimeType: String = "text/plain") {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Attendance Report"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Parse attendance list by date
     */
    fun groupAttendanceByDate(attendanceList: List<Attendance>): Map<String, List<Attendance>> {
        return attendanceList.groupBy { it.date }
    }

    /**
     * Parse attendance list by status
     */
    fun groupAttendanceByStatus(attendanceList: List<Attendance>): Map<String, List<Attendance>> {
        return attendanceList.groupBy { it.status }
    }

    /**
     * Get attendance summary
     */
    fun getAttendanceSummary(attendanceList: List<Attendance>): Map<String, Int> {
        return mapOf(
            "Present" to attendanceList.count { it.status == "Present" },
            "Absent" to attendanceList.count { it.status == "Absent" },
            "Late" to attendanceList.count { it.status == "Late" },
            "Leave" to attendanceList.count { it.status == "Leave" }
        )
    }

    /**
     * Check if attendance meets minimum threshold
     */
    fun meetsMinimumThreshold(
        attendancePercentage: Double,
        minimumThreshold: Double = 75.0
    ): Boolean {
        return attendancePercentage >= minimumThreshold
    }

    /**
     * Get status by percentage
     */
    fun getStatusByPercentage(percentage: Double): String {
        return when {
            percentage >= 85 -> "Excellent"
            percentage >= 75 -> "Good"
            percentage >= 65 -> "Average"
            percentage >= 50 -> "Poor"
            else -> "Critical"
        }
    }

    /**
     * Validate date range
     */
    fun isValidDateRange(startDate: String, endDate: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)
            start != null && end != null && start.before(end)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get days between two dates
     */
    fun getDaysBetween(startDate: String, endDate: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = sdf.parse(startDate) ?: return 0
            val end = sdf.parse(endDate) ?: return 0
            (end.time - start.time) / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Check if date is in the past
     */
    fun isDateInPast(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val checkDate = sdf.parse(date) ?: return false
            val today = Calendar.getInstance().time
            checkDate.before(today)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get month name from month number
     */
    fun getMonthName(month: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        val sdf = SimpleDateFormat("MMMM", Locale.getDefault())
        return sdf.format(cal.time)
    }

    /**
     * Export attendance data to different formats
     */
    fun exportAttendanceData(
        context: Context,
        attendanceList: List<Attendance>,
        format: String = "csv"
    ): File {
        return when (format) {
            "csv" -> generateCSVReport("attendance_${System.currentTimeMillis()}", attendanceList)
            else -> generateCSVReport("attendance_${System.currentTimeMillis()}", attendanceList)
        }
    }
}
