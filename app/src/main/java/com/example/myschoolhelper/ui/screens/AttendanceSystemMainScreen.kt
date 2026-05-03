package com.example.myschoolhelper.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class AttendanceNavigationEvent {
    object MarkAttendance : AttendanceNavigationEvent()
    object ViewStatistics : AttendanceNavigationEvent()
    object ViewReports : AttendanceNavigationEvent()
    object Back : AttendanceNavigationEvent()
    data class DetailScreen(val screen: String, val data: Map<String, Any> = emptyMap()) : AttendanceNavigationEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceSystemMainScreen(
    classId: String? = null,
    studentId: String? = null,
    userRole: String = "teacher", // teacher, student, admin
    onNavigationEvent: (AttendanceNavigationEvent) -> Unit
) {
    var currentScreen by remember { mutableStateOf("home") }
    var navigationEvent by remember { mutableStateOf<AttendanceNavigationEvent?>(null) }

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { onNavigationEvent(it) }
    }

    when (currentScreen) {
        "home" -> AttendanceHomeScreen(
            userRole = userRole,
            classId = classId,
            studentId = studentId,
            onNavigationEvent = { event ->
                navigationEvent = event
                when (event) {
                    AttendanceNavigationEvent.MarkAttendance -> currentScreen = "marking"
                    AttendanceNavigationEvent.ViewStatistics -> currentScreen = "statistics"
                    AttendanceNavigationEvent.ViewReports -> currentScreen = "reports"
                    AttendanceNavigationEvent.Back -> navigationEvent = AttendanceNavigationEvent.Back
                    else -> {}
                }
            }
        )
        "marking" -> AttendanceMarkingScreen(
            classId = classId ?: "",
            onBackClick = { currentScreen = "home" }
        )
        "statistics" -> AttendanceStatsDashboard(
            studentId = studentId,
            classId = classId,
            onBackClick = { currentScreen = "home" }
        )
        "reports" -> AttendanceReportsScreen(
            classId = classId,
            studentId = studentId,
            onBackClick = { currentScreen = "home" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceHomeScreen(
    userRole: String,
    classId: String?,
    studentId: String?,
    onNavigationEvent: (AttendanceNavigationEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance System", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WelcomeCard(userRole)
                }

                item {
                    QuickStatsCard()
                }

                item {
                    Text(
                        "Features",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    if (userRole == "teacher" || userRole == "admin") {
                        FeatureCard(
                            icon = Icons.Default.CheckCircle,
                            title = "Mark Attendance",
                            description = "Mark attendance for your class",
                            onClick = { onNavigationEvent(AttendanceNavigationEvent.MarkAttendance) }
                        )
                    }
                }

                item {
                    FeatureCard(
                        icon = Icons.Default.BarChart,
                        title = "View Statistics",
                        description = "Check attendance statistics and trends",
                        onClick = { onNavigationEvent(AttendanceNavigationEvent.ViewStatistics) }
                    )
                }

                item {
                    FeatureCard(
                        icon = Icons.Default.Receipt,
                        title = "Attendance Reports",
                        description = "Generate and view detailed reports",
                        onClick = { onNavigationEvent(AttendanceNavigationEvent.ViewReports) }
                    )
                }

                item {
                    if (userRole == "admin") {
                        FeatureCard(
                            icon = Icons.Default.Settings,
                            title = "System Settings",
                            description = "Configure attendance settings",
                            onClick = { /* Navigate to settings */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard(userRole: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    "Welcome!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    when (userRole) {
                        "teacher" -> "Manage your class attendance"
                        "student" -> "View your attendance record"
                        "admin" -> "Manage system attendance"
                        else -> "Attendance Management"
                    },
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun QuickStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "This Month",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStatItem("Present", "18", Color(0xFF4CAF50))
                QuickStatItem("Absent", "2", Color(0xFFF44336))
                QuickStatItem("Late", "1", Color(0xFFFF9800))
                QuickStatItem("Leave", "0", Color(0xFF2196F3))
            }
        }
    }
}

@Composable
private fun QuickStatItem(
    label: String,
    count: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            count,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onClick,
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(icon, null)
            }
        }
    }
}
