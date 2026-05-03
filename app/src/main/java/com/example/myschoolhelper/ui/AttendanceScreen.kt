package com.example.myschoolhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myschoolhelper.data.local.SessionManager
import com.example.myschoolhelper.data.model.AttendanceRecord
import com.example.myschoolhelper.data.model.StudentForAttendance
import com.example.myschoolhelper.viewmodel.AttendanceUiState
import com.example.myschoolhelper.viewmodel.AttendanceViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBack: () -> Unit,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userRole = sessionManager.getUserRole().lowercase()  // Convert to lowercase

    val attendanceListState by viewModel.attendanceListState.collectAsState()

    LaunchedEffect(userRole) {
        when {
            userRole == "teacher" -> {
                // For teacher, we'll show teacher attendance marking (already implemented)
                viewModel.getClassAttendance("DEFAULT_CLASS", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
            }
            userRole == "student" -> {
                // For student, fetch their own attendance using the token
                viewModel.getMyAttendance()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userRole == "teacher") "Mark Attendance" else "My Attendance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = attendanceListState) {
                is AttendanceUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AttendanceUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is AttendanceUiState.Success<*> -> {
                    val data = state.data
                    if (userRole == "student") {
                        val records = when (data) {
                            is List<*> -> data.filterIsInstance<AttendanceRecord>()
                            else -> emptyList()
                        }
                        StudentAttendanceView(attendanceRecords = records)
                    }
                }
                else -> {
                    // Idle state
                    Text(
                        "No attendance data",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherAttendanceView(
    students: List<StudentForAttendance>,
    selectedDate: String,
    onDateChange: (String) -> Unit,
    onMarkAttendance: (String, String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = onDateChange,
            label = { Text("Select Date") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (students.isEmpty()) {
            Box(modifier = Modifier.fillWeight(1f), contentAlignment = Alignment.Center) {
                Text("No students found for this class")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(students) { student ->
                    StudentAttendanceCard(
                        student = student,
                        onMarkAttendance = onMarkAttendance
                    )
                }
            }
        }
    }
}

private fun Modifier.fillWeight(f: Float): Modifier = this.fillMaxHeight().fillMaxWidth()

@Composable
fun StudentAttendanceCard(
    student: StudentForAttendance,
    onMarkAttendance: (String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = student.name, fontWeight = FontWeight.Bold)
            Text(text = "${student.class_} - ${student.section}", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatusButton(
                    label = "Present",
                    icon = Icons.Default.CheckCircle,
                    color = Color.Green,
                    isSelected = selectedStatus == "present",
                    onClick = {
                        selectedStatus = "present"
                        onMarkAttendance(student._id, "present")
                    }
                )
                AttendanceStatusButton(
                    label = "Absent",
                    icon = Icons.Default.Cancel,
                    color = Color.Red,
                    isSelected = selectedStatus == "absent",
                    onClick = {
                        selectedStatus = "absent"
                        onMarkAttendance(student._id, "absent")
                    }
                )
            }
        }
    }
}

@Composable
fun AttendanceStatusButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
            contentColor = color
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) color else Color.Gray)
        )
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun StudentAttendanceView(attendanceRecords: List<AttendanceRecord>) {
    if (attendanceRecords.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "No Records",
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No attendance records yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your attendance records will appear here",
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Total Records: ${attendanceRecords.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(attendanceRecords) { record ->
                AttendanceRecordItem(record)
            }
        }
    }
}

@Composable
fun AttendanceRecordItem(record: AttendanceRecord) {
    val (icon, color, statusText) = when (record.status.lowercase()) {
        "present" -> Triple(Icons.Default.CheckCircle, Color(0xFF4CAF50), "Present")
        "absent" -> Triple(Icons.Default.Cancel, Color(0xFFF44336), "Absent")
        "late" -> Triple(Icons.Default.Schedule, Color(0xFFFF9800), "Late")
        else -> Triple(Icons.Default.CheckCircle, Color.Gray, record.status.replaceFirstChar { it.uppercase() })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(color.copy(alpha = 0.2f))
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.date,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Marked by: ${record.marked_by}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Status badge
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(50.dp),
                color = color.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = statusText,
                            fontSize = 9.sp,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
