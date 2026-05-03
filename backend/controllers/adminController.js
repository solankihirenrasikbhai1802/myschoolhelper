const School = require('../models/School');
const Student = require('../models/Student');
const User = require('../models/User');
const Exam = require('../models/Exam');
const Attendance = require('../models/Attendance');
const Teacher = require('../models/Teacher');

/**
 * Admin Controller - Handles admin operations
 * User management, system administration
 */

/**
 * Get all users (admin only)
 * GET /admin/users
 */
exports.getAllUsers = async (req, res) => {
    try {
        const users = await User.find()
            .select('-password')
            .lean();

        res.json(users);
    } catch (error) {
        console.error('Error fetching users:', error);
        res.status(500).json({ message: 'Error fetching users', error: error.message });
    }
};

/**
 * Create new user (admin only)
 * POST /admin/users
 * Body: { name, email, password, role }
 */
exports.createUser = async (req, res) => {
    try {
        const { name, email, password, role } = req.body;

        // Validate input
        if (!name || !email || !password || !role) {
            return res.status(400).json({ message: 'All fields are required' });
        }

        // Check if user exists
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ message: 'User with this email already exists' });
        }

        // Create user
        const user = await User.create({
            name,
            email,
            password,
            role
        });

        // Create role-specific records
        if (role === 'teacher') {
            await Teacher.create({
                name,
                email,
                user_id: user._id
            });
        }

        res.status(201).json({
            message: 'User created successfully',
            user: {
                _id: user._id,
                name: user.name,
                email: user.email,
                role: user.role
            }
        });
    } catch (error) {
        console.error('Error creating user:', error);
        res.status(500).json({ message: 'Error creating user', error: error.message });
    }
};

/**
 * Update user (admin only)
 * PUT /admin/users/:userId
 */
exports.updateUser = async (req, res) => {
    try {
        const { userId } = req.params;
        const { name, email, password, role } = req.body;

        const user = await User.findByIdAndUpdate(
            userId,
            {
                name,
                email,
                ...(password && { password }),
                role
            },
            { new: true }
        ).select('-password');

        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        res.json({
            message: 'User updated successfully',
            user
        });
    } catch (error) {
        console.error('Error updating user:', error);
        res.status(500).json({ message: 'Error updating user', error: error.message });
    }
};

/**
 * Delete user (admin only)
 * DELETE /admin/users/:userId
 */
exports.deleteUser = async (req, res) => {
    try {
        const { userId } = req.params;

        const user = await User.findByIdAndDelete(userId);

        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        // Also delete role-specific records
        if (user.role === 'teacher') {
            await Teacher.deleteOne({ user_id: userId });
        } else if (user.role === 'student') {
            await Student.deleteOne({ user_id: userId });
        }

        res.json({ message: 'User deleted successfully' });
    } catch (error) {
        console.error('Error deleting user:', error);
        res.status(500).json({ message: 'Error deleting user', error: error.message });
    }
};

exports.getDashboardStats = async (req, res) => {
    try {
        const totalSchools = await School.countDocuments();
        const paidSchools = await School.countDocuments({ payment_status: 'paid' });
        const pendingSchools = await School.countDocuments({ payment_status: 'pending' });
        const totalStudents = await Student.countDocuments();
        const paidStudents = await Student.countDocuments({ fees_paid: true });

        res.json({
            totalSchools,
            paidSchools,
            pendingSchools,
            totalStudents,
            paidStudents
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

exports.createSchool = async (req, res) => {
    try {
        const school = await School.create(req.body);
        res.status(201).json(school);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.getAllSchools = async (req, res) => {
    try {
        const schools = await School.find();
        res.json(schools);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

exports.updateSchool = async (req, res) => {
    try {
        const school = await School.findByIdAndUpdate(req.params.id, req.body, { new: true });
        res.json(school);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.deleteSchool = async (req, res) => {
    try {
        await School.findByIdAndDelete(req.params.id);
        res.json({ message: 'School deleted successfully' });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.getAllStudents = async (req, res) => {
    try {
        const students = await Student.find().populate('user_id', 'name email');
        res.json(students);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};
