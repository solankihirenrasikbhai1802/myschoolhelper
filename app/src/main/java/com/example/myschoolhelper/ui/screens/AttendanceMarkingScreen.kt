package com.example.myschoolhelper.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myschoolhelper.data.Attendance
import com.example.myschoolhelper.viewmodel.AttendanceViewModel
import com.example.myschoolhelper.viewmodel.AttendanceUiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceMarkingScreen(
    viewModel: AttendanceViewModel = hiltViewModel(),
    classId: String,
    onBackClick: () -> Unit
) {
    val attendanceState by viewModel.attendanceListState.collectAsState()
    val markingState by viewModel.markAttendanceState.collectAsState()
    val markingInProgress by viewModel.markingInProgress.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var selectedAttendanceList by remember { mutableStateOf<List<Attendance>>(emptyList()) }
    var attendanceMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showBulkActions by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedDate) {
        viewModel.getClassAttendance(classId, selectedDate)
    }

    LaunchedEffect(attendanceState) {
        if (attendanceState is AttendanceUiState.Success<*>) {
            @Suppress("UNCHECKED_CAST")
            selectedAttendanceList = (attendanceState as AttendanceUiState.Success<List<Attendance>>).data
            attendanceMap = selectedAttendanceList.associate { it.id to it.status }
        }
    }

    val filteredAttendance = if (filterStatus != null) {
        selectedAttendanceList.filter { it.status == filterStatus }
    } else {
        selectedAttendanceList
    }

    val presentCount = selectedAttendanceList.count { it.status == "Present" }
    val absentCount = selectedAttendanceList.count { it.status == "Absent" }
    val lateCount = selectedAttendanceList.count { it.status == "Late" }
    val leaveCount = selectedAttendanceList.count { it.status == "Leave" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mark Attendance", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showBulkActions = !showBulkActions }) {
                        Icon(Icons.Default.MoreVert, "More options")
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
            // Date Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { showDatePicker = !showDatePicker },
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
                            "Date",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            selectedDate,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Icon(
                        Icons.Default.DateRange,
                        "Select Date",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Statistics Row
            AnimatedVisibility(
                visible = selectedAttendanceList.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                StatisticsRow(
                    present = presentCount,
                    absent = absentCount,
                    late = lateCount,
                    leave = leaveCount,
                    total = selectedAttendanceList.size
                )
            }

            // Filter Chips
            FilterChipsRow(
                currentFilter = filterStatus,
                onFilterSelected = { filterStatus = it },
                present = presentCount,
                absent = absentCount,
                late = lateCount,
                leave = leaveCount
            )

            // Attendance List
            Box(modifier = Modifier.fillMaxSize()) {
                when (attendanceState) {
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
                                (attendanceState as AttendanceUiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                    is AttendanceUiState.Success<*> -> {
                        if (filteredAttendance.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.EventNote,
                                    "No records",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Text("No attendance records")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    filteredAttendance,
                                    key = { it.id }
                                ) { attendance ->
                                    AttendanceItemCard(
                                        attendance = attendance,
                                        isMarking = attendance.id in markingInProgress,
                                        onStatusChanged = { newStatus ->
                                            scope.launch {
                                                viewModel.updateAttendance(
                                                    attendance.id,
                                                    newStatus
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    // Bulk Actions Menu
    if (showBulkActions) {
        BulkActionsDialog(
            onMarkAllPresent = {
                scope.launch {
                    attendanceMap = selectedAttendanceList.associate { it.id to "Present" }
                }
                showBulkActions = false
            },
            onMarkAllAbsent = {
                scope.launch {
                    attendanceMap = selectedAttendanceList.associate { it.id to "Absent" }
                }
                showBulkActions = false
            },
            onSubmit = {
                scope.launch {
                    val attendanceList = attendanceMap.map { (studentId, status) ->
                        mapOf(
                            "studentId" to studentId,
                            "status" to status
                        )
                    }
                    viewModel.markAttendance(classId, selectedDate, attendanceList)
                }
                showBulkActions = false
            },
            onDismiss = { showBulkActions = false }
        )
    }

    // Success Snackbar
    LaunchedEffect(markingState) {
        if (markingState is AttendanceUiState.Success<*>) {
            // Trigger refresh
            viewModel.getClassAttendance(classId, selectedDate)
        }
    }
}

@Composable
private fun StatisticsRow(
    present: Int,
    absent: Int,
    late: Int,
    leave: Int,
    total: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Summary",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticChip("Present", present, Color(0xFF4CAF50), total)
            StatisticChip("Absent", absent, Color(0xFFF44336), total)
            StatisticChip("Late", late, Color(0xFFFF9800), total)
            StatisticChip("Leave", leave, Color(0xFF2196F3), total)
        }
    }
}

@Composable
private fun StatisticChip(
    label: String,
    count: Int,
    color: Color,
    total: Int
) {
    val percentage = if (total > 0) (count * 100) / total else 0
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            Text(
                "$count ($percentage%)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipsRow(
    currentFilter: String?,
    onFilterSelected: (String?) -> Unit,
    present: Int,
    absent: Int,
    late: Int,
    leave: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("All") }
        )
        FilterChip(
            selected = currentFilter == "Present",
            onClick = { onFilterSelected("Present") },
            label = { Text("Present ($present)") }
        )
        FilterChip(
            selected = currentFilter == "Absent",
            onClick = { onFilterSelected("Absent") },
            label = { Text("Absent ($absent)") }
        )
        FilterChip(
            selected = currentFilter == "Late",
            onClick = { onFilterSelected("Late") },
            label = { Text("Late ($late)") }
        )
        FilterChip(
            selected = currentFilter == "Leave",
            onClick = { onFilterSelected("Leave") },
            label = { Text("Leave ($leave)") }
        )
    }
}

@Composable
private fun AttendanceItemCard(
    attendance: Attendance,
    isMarking: Boolean,
    onStatusChanged: (String) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = getStatusBackgroundColor(attendance.status)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        attendance.studentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Roll No: ${attendance.rollNumber}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    Button(
                        onClick = { showStatusMenu = !showStatusMenu },
                        modifier = Modifier.height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getStatusColor(attendance.status)
                        ),
                        enabled = !isMarking
                    ) {
                        if (isMarking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(attendance.status)
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("Present", "Absent", "Late", "Leave").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    onStatusChanged(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (attendance.remarks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Remarks: ${attendance.remarks}",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BulkActionsDialog(
    onMarkAllPresent: () -> Unit,
    onMarkAllAbsent: () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bulk Actions") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkAllPresent,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Mark All Present")
                }
                Button(
                    onClick = onMarkAllAbsent,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Mark All Absent")
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getStatusColor(status: String): Color {
    return when (status) {
        "Present" -> Color(0xFF4CAF50)
        "Absent" -> Color(0xFFF44336)
        "Late" -> Color(0xFFFF9800)
        "Leave" -> Color(0xFF2196F3)
        else -> Color.Gray
    }
}

@Composable
private fun getStatusBackgroundColor(status: String): Color {
    return when (status) {
        "Present" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
        "Absent" -> Color(0xFFF44336).copy(alpha = 0.1f)
        "Late" -> Color(0xFFFF9800).copy(alpha = 0.1f)
        "Leave" -> Color(0xFF2196F3).copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }
}

private fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}
