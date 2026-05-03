const User = require('../models/User');
const School = require('../models/School');
const Student = require('../models/Student');
const Teacher = require('../models/Teacher');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');

/**
 * Generate JWT token
 * @param {string} id - User ID
 * @returns {string} JWT token
 */
const generateToken = (id) => {
    return jwt.sign({ id }, process.env.JWT_SECRET || 'your-secret-key', {
        expiresIn: '7d',
    });
};

/**
 * Register new user (admin only)
 * POST /auth/register
 * Body: { name, email, password, role, school_code }
 */
exports.register = async (req, res) => {
    const { name, email, password, role, school_code } = req.body;
    try {
        // Check if user already exists
        const userExists = await User.findOne({ email });
        if (userExists) {
            return res.status(400).json({ message: 'User already exists' });
        }

        // Validate input
        if (!name || !email || !password || !role) {
            return res.status(400).json({ message: 'All fields are required' });
        }

        // Get school ID
        let school_id = null;
        if (role !== 'admin') {
            if (!school_code) {
                return res.status(400).json({ message: 'School code is required for non-admin users' });
            }
            const trimmedCode = school_code.trim().toUpperCase();
            console.log(`Looking up school with code: ${trimmedCode}`);
            
            const school = await School.findOne({ code: trimmedCode });
            if (!school) {
                console.error(`School not found with code: ${trimmedCode}`);
                const allSchools = await School.find().select('code name');
                console.error(`Available schools: ${JSON.stringify(allSchools)}`);
                return res.status(400).json({ message: 'Invalid school code', availableSchools: allSchools });
            }
            school_id = school._id;
        }

        // Create user
        const user = await User.create({
            name,
            email,
            password,
            role,
            school_id
        });

        // Create role-specific record
        if (role === 'teacher') {
            await Teacher.create({
                name,
                email,
                user_id: user._id
            });
        } else if (role === 'student') {
            // Student record will be created separately
        }

        res.status(201).json({
            message: 'User registered successfully',
            user: {
                _id: user._id,
                name: user.name,
                email: user.email,
                role: user.role
            },
            token: generateToken(user._id),
        });
    } catch (error) {
        console.error('Register error:', error.message);
        res.status(500).json({ message: error.message });
    }
};

/**
 * Login user with email and password
 * POST /auth/login
 * Body: { email, password }
 */
exports.login = async (req, res) => {
    const { email, password } = req.body;
    try {
        // Validate input
        if (!email || !password) {
            return res.status(400).json({ message: 'Email and password are required' });
        }

        // Find user
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(401).json({ message: 'Invalid email or password' });
        }

        // Compare passwords
        const isPasswordValid = await bcrypt.compare(password, user.password);
        if (!isPasswordValid) {
            return res.status(401).json({ message: 'Invalid email or password' });
        }

        // Get role-specific data
        let roleData = {};
        if (user.role === 'teacher') {
            const teacher = await Teacher.findOne({ user_id: user._id });
            roleData = { teacherId: teacher?._id };
        } else if (user.role === 'student') {
            const student = await Student.findOne({ user_id: user._id });
            roleData = { studentId: student?._id };
        }

        res.json({
            user: {
                _id: user._id,
                name: user.name,
                email: user.email,
                role: user.role.toLowerCase(), // Ensure lowercase for consistency
                ...roleData
            },
            token: generateToken(user._id),
        });
    } catch (error) {
        console.error('Login error:', error.message);
        res.status(500).json({ message: error.message });
    }
};

/**
 * Verify JWT token
 * GET /auth/verify
 */
exports.verifyToken = async (req, res) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];

        if (!token) {
            return res.status(401).json({ message: 'No token provided', valid: false });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET || 'your-secret-key');
        const user = await User.findById(decoded.id).select('-password');

        if (!user) {
            return res.status(401).json({ message: 'User not found', valid: false });
        }

        res.json({ user, valid: true });
    } catch (error) {
        res.status(401).json({ message: 'Invalid token', valid: false });
    }
};
