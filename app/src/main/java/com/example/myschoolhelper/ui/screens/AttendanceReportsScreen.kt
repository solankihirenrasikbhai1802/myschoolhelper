package com.example.myschoolhelper.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myschoolhelper.data.MonthlyReport
import com.example.myschoolhelper.viewmodel.AttendanceViewModel
import com.example.myschoolhelper.viewmodel.AttendanceUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportsScreen(
    viewModel: AttendanceViewModel = hiltViewModel(),
    classId: String? = null,
    studentId: String? = null,
    onBackClick: () -> Unit
) {
    var selectedMonth by remember { mutableStateOf(getCurrentMonth()) }
    var selectedYear by remember { mutableStateOf(getCurrentYear()) }
    var reportType by remember { mutableStateOf("monthly") } // monthly, bulk
    var expandedFilters by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val monthlyReportState by viewModel.monthlyReportState.collectAsState()
    val bulkReportState by viewModel.bulkReportState.collectAsState()

    LaunchedEffect(selectedMonth, selectedYear) {
        if (reportType == "monthly") {
            viewModel.getMonthlyReport(studentId, classId, selectedMonth, selectedYear)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        expandedFilters = !expandedFilters
                    }) {
                        Icon(Icons.Default.FilterList, "Filters")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Report Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("monthly", "bulk").forEach { type ->
                    Button(
                        onClick = { reportType = type },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (reportType == type)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Transparent
                        )
                    ) {
                        Text(type.replaceFirstChar { it.uppercase() }, 
                             color = if (reportType == type) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Filters
            AnimatedVisibility(
                visible = expandedFilters,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                FilterPanel(
                    reportType = reportType,
                    selectedMonth = selectedMonth,
                    onMonthChange = { selectedMonth = it },
                    selectedYear = selectedYear,
                    onYearChange = { selectedYear = it },
                    startDate = startDate,
                    onStartDateChange = { startDate = it },
                    endDate = endDate,
                    onEndDateChange = { endDate = it },
                    onApplyFilters = {
                        if (reportType == "bulk" && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                            classId?.let { cId ->
                                viewModel.getBulkAttendanceReport(cId, startDate, endDate)
                            }
                        }
                    }
                )
            }

            // Reports Content
            Box(modifier = Modifier.fillMaxSize()) {
                val currentState = if (reportType == "monthly") monthlyReportState else bulkReportState

                when (currentState) {
                    is AttendanceUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is AttendanceUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                "Error",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                (currentState as AttendanceUiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    is AttendanceUiState.Success<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        if (reportType == "monthly") {
                            val report = (currentState as AttendanceUiState.Success<MonthlyReport>).data
                            MonthlyReportContent(report)
                        } else {
                            val reports = (currentState as? AttendanceUiState.Success<List<MonthlyReport>>)?.data ?: emptyList()
                            BulkReportContent(reports)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun FilterPanel(
    reportType: String,
    selectedMonth: String,
    onMonthChange: (String) -> Unit,
    selectedYear: String,
    onYearChange: (String) -> Unit,
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    onApplyFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (reportType == "monthly") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = onMonthChange,
                        label = { Text("Month") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = selectedYear,
                        onValueChange = onYearChange,
                        label = { Text("Year") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = onStartDateChange,
                        label = { Text("Start Date (yyyy-MM-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = onEndDateChange,
                        label = { Text("End Date (yyyy-MM-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = onApplyFilters,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Report")
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyReportContent(report: MonthlyReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReportHeaderCard(
                period = report.month,
                totalDays = report.totalDays,
                presentDays = report.presentDays,
                absentDays = report.absentDays
            )
        }

        item {
            ReportStatsGrid(
                present = report.presentDays,
                absent = report.absentDays,
                late = report.lateDays,
                leave = report.leaveDays,
                total = report.totalDays
            )
        }

        item {
            AttendancePercentageCard(
                percentage = report.attendancePercentage,
                trend = report.trend
            )
        }

        if (report.remarks.isNotEmpty()) {
            item {
                RemarksCard(remarks = report.remarks)
            }
        }

        item {
            ExportCard()
        }
    }
}

@Composable
private fun BulkReportContent(reports: List<MonthlyReport>) {
    if (reports.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.EventNote,
                "No records",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text("No reports available for the selected period")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reports) { report ->
                BulkReportItemCard(report)
            }
        }
    }
}

@Composable
private fun ReportHeaderCard(
    period: String,
    totalDays: Int,
    presentDays: Int,
    absentDays: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                period,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeaderStat("Total Days", totalDays.toString())
                HeaderStat("Present", presentDays.toString())
                HeaderStat("Absent", absentDays.toString())
            }
        }
    }
}

@Composable
private fun HeaderStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ReportStatsGrid(
    present: Int,
    absent: Int,
    late: Int,
    leave: Int,
    total: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Attendance Breakdown",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        ReportStatItem("Present", present, total, Color(0xFF4CAF50))
        ReportStatItem("Absent", absent, total, Color(0xFFF44336))
        ReportStatItem("Late", late, total, Color(0xFFFF9800))
        ReportStatItem("Leave", leave, total, Color(0xFF2196F3))
    }
}

@Composable
private fun ReportStatItem(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text(
                "$count / $total (${if (total > 0) (count * 100) / total else 0}%)",
                fontWeight = FontWeight.SemiBold
            )
        }
        LinearProgressIndicator(
            progress = if (total > 0) count.toFloat() / total else 0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color
        )
    }
}

@Composable
private fun AttendancePercentageCard(
    percentage: Double,
    trend: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Attendance Rate", fontSize = 12.sp)
                    Text(
                        String.format("%.1f%%", percentage),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = getPercentageColor(percentage)
                    )
                }
                Icon(
                    when {
                        percentage >= 85 -> Icons.Default.TrendingUp
                        percentage >= 70 -> Icons.Default.TrendingFlat
                        else -> Icons.Default.TrendingDown
                    },
                    "Trend",
                    modifier = Modifier.size(32.dp),
                    tint = getPercentageColor(percentage)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((percentage / 100).toFloat().coerceIn(0f, 1f))
                        .background(
                            getPercentageColor(percentage),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun RemarksCard(remarks: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Remarks", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(remarks, fontSize = 12.sp)
        }
    }
}

@Composable
private fun BulkReportItemCard(report: MonthlyReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        report.month,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "${report.presentDays}/${report.totalDays} Present",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        String.format("%.1f%%", report.attendancePercentage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = getPercentageColor(report.attendancePercentage)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = (report.attendancePercentage / 100).toFloat().coerceIn(0f, 1f),
                        modifier = Modifier
                            .width(60.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = getPercentageColor(report.attendancePercentage)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* Export to PDF */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PDF")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /* Export to CSV */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CSV")
            }
        }
    }
}

private fun getPercentageColor(percentage: Double): Color {
    return when {
        percentage >= 85 -> Color(0xFF4CAF50)
        percentage >= 70 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}

private fun getCurrentMonth(): String {
    val cal = Calendar.getInstance()
    return String.format("%02d", cal.get(Calendar.MONTH) + 1)
}

private fun getCurrentYear(): String {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.YEAR).toString()
}
