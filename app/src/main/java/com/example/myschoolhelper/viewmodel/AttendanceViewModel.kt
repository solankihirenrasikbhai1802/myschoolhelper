package com.example.myschoolhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myschoolhelper.data.Attendance
import com.example.myschoolhelper.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Success<T>(val data: T) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
    object Idle : AttendanceUiState()
}

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: AttendanceRepository) : ViewModel() {

    private val _attendanceListState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val attendanceListState: StateFlow<AttendanceUiState> = _attendanceListState.asStateFlow()

    private val _markAttendanceState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val markAttendanceState: StateFlow<AttendanceUiState> = _markAttendanceState.asStateFlow()

    private val _updateAttendanceState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val updateAttendanceState: StateFlow<AttendanceUiState> = _updateAttendanceState.asStateFlow()

    private val _attendanceStatsState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val attendanceStatsState: StateFlow<AttendanceUiState> = _attendanceStatsState.asStateFlow()

    private val _monthlyReportState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val monthlyReportState: StateFlow<AttendanceUiState> = _monthlyReportState.asStateFlow()

    private val _bulkReportState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val bulkReportState: StateFlow<AttendanceUiState> = _bulkReportState.asStateFlow()

    // Local state for current selection
    private val _selectedAttendance = MutableStateFlow<Attendance?>(null)
    val selectedAttendance: StateFlow<Attendance?> = _selectedAttendance.asStateFlow()

    private val _markingInProgress = MutableStateFlow<Set<String>>(emptySet())
    val markingInProgress: StateFlow<Set<String>> = _markingInProgress.asStateFlow()

    // For student viewing their own attendance
    fun getMyAttendance() {
        viewModelScope.launch {
            _attendanceListState.value = AttendanceUiState.Loading
            repository.getMyAttendance().collect { result ->
                result.onSuccess { data ->
                    _attendanceListState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _attendanceListState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to fetch your attendance"
                    )
                }
            }
        }
    }

    fun getStudentAttendance(
        studentId: String,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _attendanceListState.value = AttendanceUiState.Loading
            repository.getStudentAttendance(studentId, startDate, endDate).collect { result ->
                result.onSuccess { data ->
                    _attendanceListState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _attendanceListState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to fetch attendance"
                    )
                }
            }
        }
    }

    fun markAttendance(
        classId: String,
        date: String,
        attendance: List<Map<String, Any>>
    ) {
        viewModelScope.launch {
            _markAttendanceState.value = AttendanceUiState.Loading
            // Fixed type mismatch by removing classId to match repository signature
            repository.markAttendance(date, attendance).collect { result ->
                result.onSuccess { data ->
                    _markAttendanceState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _markAttendanceState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to mark attendance"
                    )
                }
            }
        }
    }

    fun updateAttendance(
        attendanceId: String,
        status: String,
        remarks: String? = null
    ) {
        viewModelScope.launch {
            _markingInProgress.value = _markingInProgress.value + attendanceId
            _updateAttendanceState.value = AttendanceUiState.Loading

            repository.updateAttendance(attendanceId, status, remarks).collect { result ->
                _markingInProgress.value = _markingInProgress.value - attendanceId

                result.onSuccess { data ->
                    _updateAttendanceState.value = AttendanceUiState.Success(data)
                    _selectedAttendance.value = data
                }.onFailure { error ->
                    _updateAttendanceState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to update attendance"
                    )
                }
            }
        }
    }

    fun getClassAttendance(classId: String, date: String) {
        viewModelScope.launch {
            _attendanceListState.value = AttendanceUiState.Loading
            repository.getClassAttendance(classId, date).collect { result ->
                result.onSuccess { data ->
                    _attendanceListState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _attendanceListState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to fetch class attendance"
                    )
                }
            }
        }
    }

    fun getAttendanceStats(
        studentId: String? = null,
        classId: String? = null,
        month: String? = null
    ) {
        viewModelScope.launch {
            _attendanceStatsState.value = AttendanceUiState.Loading
            repository.getAttendanceStats(studentId, classId, month).collect { result ->
                result.onSuccess { data ->
                    _attendanceStatsState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _attendanceStatsState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to fetch statistics"
                    )
                }
            }
        }
    }

    fun getMonthlyReport(
        studentId: String? = null,
        classId: String? = null,
        month: String,
        year: String
    ) {
        viewModelScope.launch {
            _monthlyReportState.value = AttendanceUiState.Loading
            repository.getMonthlyReport(studentId, classId, month, year).collect { result ->
                result.onSuccess { data ->
                    _monthlyReportState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _monthlyReportState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to fetch monthly report"
                    )
                }
            }
        }
    }

    fun getBulkAttendanceReport(
        classId: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            _bulkReportState.value = AttendanceUiState.Loading
            repository.getBulkAttendanceReport(classId, startDate, endDate).collect { result ->
                result.onSuccess { data ->
                    _bulkReportState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _bulkReportState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to fetch bulk report"
                    )
                }
            }
        }
    }

    fun selectAttendance(attendance: Attendance) {
        _selectedAttendance.value = attendance
    }

    fun clearSelection() {
        _selectedAttendance.value = null
    }

    fun deleteAttendance(attendanceId: String) {
        viewModelScope.launch {
            _markAttendanceState.value = AttendanceUiState.Loading
            repository.deleteAttendance(attendanceId).collect { result ->
                result.onSuccess { data ->
                    _markAttendanceState.value = AttendanceUiState.Success(data)
                }.onFailure { error ->
                    _markAttendanceState.value = AttendanceUiState.Error(
                        error.message ?: "Failed to delete attendance"
                    )
                }
            }
        }
    }

    fun resetMarkAttendanceState() {
        _markAttendanceState.value = AttendanceUiState.Idle
    }

    fun resetUpdateAttendanceState() {
        _updateAttendanceState.value = AttendanceUiState.Idle
    }
}
