const Class = require('../models/Class');
const Section = require('../models/Section');

/**
 * Class Controller - Handles class and section management
 */

/**
 * Get all classes
 * GET /classes
 */
exports.getAllClasses = async (req, res) => {
    try {
        const classes = await Class.find()
            .lean();
        res.json(classes);
    } catch (error) {
        console.error('Error fetching classes:', error);
        res.status(500).json({ message: 'Error fetching classes', error: error.message });
    }
};

/**
 * Get sections for a class
 * GET /classes/:classId/sections
 */
exports.getSectionsByClass = async (req, res) => {
    try {
        const { classId } = req.params;

        const sections = await Section.find({ class_id: classId })
            .lean();

        res.json(sections);
    } catch (error) {
        console.error('Error fetching sections:', error);
        res.status(500).json({ message: 'Error fetching sections', error: error.message });
    }
};

/**
 * Create new class (admin only)
 * POST /classes
 * Body: { name, description }
 */
exports.createClass = async (req, res) => {
    try {
        const { name, description } = req.body;

        if (!name) {
            return res.status(400).json({ message: 'Class name is required' });
        }

        const newClass = await Class.create({
            name,
            description: description || ''
        });

        res.status(201).json(newClass);
    } catch (error) {
        console.error('Error creating class:', error);
        res.status(500).json({ message: 'Error creating class', error: error.message });
    }
};

/**
 * Create new section (admin only)
 * POST /sections
 * Body: { name, classId, description }
 */
exports.createSection = async (req, res) => {
    try {
        const { name, classId, description } = req.body;

        if (!name || !classId) {
            return res.status(400).json({ message: 'Section name and class ID are required' });
        }

        const newSection = await Section.create({
            name,
            class_id: classId,
            description: description || ''
        });

        res.status(201).json(newSection);
    } catch (error) {
        console.error('Error creating section:', error);
        res.status(500).json({ message: 'Error creating section', error: error.message });
    }
};
