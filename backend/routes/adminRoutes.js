const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const {
    getDashboardStats,
    createSchool,
    getAllSchools,
    updateSchool,
    deleteSchool,
    getAllStudents,
    getAllUsers,
    createUser,
    updateUser,
    deleteUser
} = require('../controllers/adminController');

/**
 * Admin Routes - Administrative operations
 * All routes require admin role
 */

// Apply protection and authorization to all admin routes
router.use(protect);
router.use(authorize('admin'));

// Dashboard
router.get('/dashboard', getDashboardStats);

// School management
router.route('/schools')
    .post(createSchool)
    .get(getAllSchools);

// User management
router.get('/users', getAllUsers);
router.post('/users', createUser);
router.put('/users/:userId', updateUser);
router.delete('/users/:userId', deleteUser);

router.route('/schools/:id')
    .put(updateSchool)
    .delete(deleteSchool);

router.get('/students', getAllStudents);

module.exports = router;
