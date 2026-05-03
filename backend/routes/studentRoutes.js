const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const {
    getStudents,
    getStudentDetails,
    createStudent,
    updateStudent,
    getDashboardStats,
    getHomework
} = require('../controllers/studentController');

/**
 * Student Routes - Manage and view students
 */

// Admin only - Create and manage students
router.get('/', protect, authorize('teacher', 'admin'), getStudents);
router.post('/', protect, authorize('admin'), createStudent);
router.get('/:studentId', protect, getStudentDetails);
router.put('/:studentId', protect, authorize('admin'), updateStudent);

// Student dashboard
router.get('/dashboard', protect, authorize('student'), getDashboardStats);
router.get('/homework', protect, authorize('student'), getHomework);

module.exports = router;
