package com.example.myschoolhelper.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.myschoolhelper.data.AttendanceStatistics
import com.example.myschoolhelper.viewmodel.AttendanceViewModel
import com.example.myschoolhelper.viewmodel.AttendanceUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceStatsDashboard(
    viewModel: AttendanceViewModel = hiltViewModel(),
    studentId: String? = null,
    classId: String? = null,
    onBackClick: () -> Unit
) {
    val statsState by viewModel.attendanceStatsState.collectAsState()
    var selectedMonth by remember { mutableStateOf(getCurrentMonth()) }

    LaunchedEffect(selectedMonth) {
        viewModel.getAttendanceStats(studentId, classId, selectedMonth)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (statsState) {
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
                            (statsState as AttendanceUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is AttendanceUiState.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val stats = (statsState as AttendanceUiState.Success<AttendanceStatistics>).data

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            MonthSelectorCard(
                                selectedMonth = selectedMonth,
                                onMonthChange = { selectedMonth = it }
                            )
                        }

                        item {
                            OverallStatsCard(
                                presentPercentage = stats.presentPercentage,
                                absentPercentage = stats.absentPercentage,
                                latePercentage = stats.latePercentage,
                                leavePercentage = stats.leavePercentage
                            )
                        }

                        item {
                            DetailedStatsGrid(
                                present = stats.presentDays,
                                absent = stats.absentDays,
                                late = stats.lateDays,
                                leave = stats.leaveDays,
                                total = stats.totalDays
                            )
                        }

                        item {
                            AttendanceProgressCard(
                                percentage = stats.presentPercentage,
                                label = "Current Month Attendance"
                            )
                        }

                        item {
                            if (stats.trend.isNotEmpty()) {
                                TrendAnalysisCard(trend = stats.trend)
                            }
                        }

                        item {
                            if (stats.warnings.isNotEmpty()) {
                                WarningsCard(warnings = stats.warnings)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun MonthSelectorCard(
    selectedMonth: String,
    onMonthChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Period",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    selectedMonth,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                Icon(
                    Icons.Default.DateRange,
                    "Change Date",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun OverallStatsCard(
    presentPercentage: Double,
    absentPercentage: Double,
    latePercentage: Double,
    leavePercentage: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Overall Statistics",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "Present",
                    value = String.format("%.1f%%", presentPercentage),
                    color = Color(0xFF4CAF50)
                )
                StatisticItem(
                    label = "Absent",
                    value = String.format("%.1f%%", absentPercentage),
                    color = Color(0xFFF44336)
                )
                StatisticItem(
                    label = "Late",
                    value = String.format("%.1f%%", latePercentage),
                    color = Color(0xFFFF9800)
                )
                StatisticItem(
                    label = "Leave",
                    value = String.format("%.1f%%", leavePercentage),
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailedStatsGrid(
    present: Int,
    absent: Int,
    late: Int,
    leave: Int,
    total: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Detailed Breakdown",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailedStatRow("Present", present, total, Color(0xFF4CAF50))
                DetailedStatRow("Absent", absent, total, Color(0xFFF44336))
                DetailedStatRow("Late", late, total, Color(0xFFFF9800))
                DetailedStatRow("Leave", leave, total, Color(0xFF2196F3))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Days",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        total.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailedStatRow(
    label: String,
    value: Int,
    total: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Text(label)
        }

        Text(
            "$value/$total",
            fontWeight = FontWeight.SemiBold
        )
    }

    LinearProgressIndicator(
        progress = { if (total > 0) value.toFloat() / total else 0f },
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
        color = color
    )
}

@Composable
private fun AttendanceProgressCard(
    percentage: Double,
    label: String
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
                Text(
                    label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    String.format("%.1f%%", percentage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = getPercentageColor(percentage)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = (percentage / 100).toFloat().coerceIn(0f, 1f))
                        .background(
                            getPercentageColor(percentage),
                            RoundedCornerShape(15.dp)
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        String.format("%.1f%%", percentage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (percentage < 50) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendAnalysisCard(trend: List<Map<String, Any>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Attendance Trend",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                trend.take(5).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item["date"]?.toString() ?: "N/A")
                        Text(
                            item["status"]?.toString() ?: "N/A",
                            fontWeight = FontWeight.SemiBold,
                            color = when (item["status"]) {
                                "Present" -> Color(0xFF4CAF50)
                                "Absent" -> Color(0xFFF44336)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WarningsCard(warnings: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFF44336).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    Icons.Default.WarningAmber,
                    "Warning",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Alerts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFF44336)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                warnings.forEach { warning ->
                    Text(
                        "• $warning",
                        fontSize = 12.sp,
                        color = Color(0xFFF44336)
                    )
                }
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
    val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    return sdf.format(Date())
}
