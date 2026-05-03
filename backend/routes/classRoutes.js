const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const {
    getAllClasses,
    getSectionsByClass,
    createClass,
    createSection
} = require('../controllers/classController');

/**
 * Class & Section Routes - Manage classes and sections
 */

// Public routes (all authenticated users)
router.get('/', protect, getAllClasses);
router.get('/:classId/sections', protect, getSectionsByClass);

// Admin only routes
router.post('/', protect, authorize('admin'), createClass);
router.post('/sections', protect, authorize('admin'), createSection);

module.exports = router;
