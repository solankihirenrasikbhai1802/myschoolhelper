const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const {
    markAttendance,
    updateAttendance,
    checkExistingAttendance,
    getAttendanceRecords,
    getStudentAttendanceRecords,
    getStudentAttendanceStats,
    getAttendanceReport
} = require('../controllers/attendanceController');

/**
 * Attendance Routes - All routes for attendance management
 * Features:
 * - Teachers can mark and update attendance
 * - Students can view their attendance
 * - Admin can view and export reports
 */

// Teacher routes - Mark and view attendance
router.post('/mark', protect, authorize('teacher'), markAttendance);
router.put('/update', protect, authorize('teacher'), updateAttendance);
router.get('/check-existing', protect, authorize('teacher'), checkExistingAttendance);
router.get('/view', protect, authorize('teacher'), getAttendanceRecords);

// Student routes - View personal attendance
router.get('/records', protect, authorize('student'), getStudentAttendanceRecords);
router.get('/stats', protect, authorize('student'), getStudentAttendanceStats);

// Admin routes - View all attendance reports
router.get('/admin/reports', protect, authorize('admin'), getAttendanceReport);

module.exports = router;
router.get('/report/monthly', protect, getMonthlyReport);
router.get('/report/bulk', protect, getBulkAttendanceReport);

module.exports = router;