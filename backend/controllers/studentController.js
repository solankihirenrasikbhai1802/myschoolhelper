const Attendance = require('../models/Attendance');
const Result = require('../models/Result');
const Notification = require('../models/Notification');
const Homework = require('../models/Homework');
const Student = require('../models/Student');
const User = require('../models/User');

/**
 * Student Controller - Handles student related operations
 */

/**
 * Get students by class and section
 * GET /students?classId=xxx&sectionId=yyy
 */
exports.getStudents = async (req, res) => {
    try {
        const { classId, sectionId } = req.query;

        let query = { is_active: true };

        if (classId) query.class_id = classId;
        if (sectionId) query.section_id = sectionId;

        const students = await Student.find(query)
            .populate('user_id', 'name email')
            .lean();

        // Format response
        const formattedStudents = students.map(student => ({
            _id: student._id,
            name: student.name,
            email: student.email,
            rollNumber: student.roll_number,
            classId: student.class_id,
            sectionId: student.section_id
        }));

        res.json(formattedStudents);
    } catch (error) {
        console.error('Error fetching students:', error);
        res.status(500).json({ message: 'Error fetching students', error: error.message });
    }
};

/**
 * Get student details
 * GET /students/:studentId
 */
exports.getStudentDetails = async (req, res) => {
    try {
        const { studentId } = req.params;

        const student = await Student.findById(studentId)
            .populate('user_id', 'name email')
            .populate('class_id', 'name')
            .populate('section_id', 'name');

        if (!student) {
            return res.status(404).json({ message: 'Student not found' });
        }

        res.json(student);
    } catch (error) {
        console.error('Error fetching student:', error);
        res.status(500).json({ message: 'Error fetching student', error: error.message });
    }
};

/**
 * Create new student (admin only)
 * POST /students
 * Body: { name, email, classId, sectionId, rollNumber }
 */
exports.createStudent = async (req, res) => {
    try {
        const { name, email, classId, sectionId, rollNumber } = req.body;

        if (!name || !email || !classId || !sectionId) {
            return res.status(400).json({ message: 'Name, email, class, and section are required' });
        }

        // Create user first
        const user = await User.create({
            name,
            email,
            password: 'password123', // Default password
            role: 'student'
        });

        // Create student record
        const student = await Student.create({
            name,
            email,
            class_id: classId,
            section_id: sectionId,
            roll_number: rollNumber || null,
            user_id: user._id
        });

        res.status(201).json({
            message: 'Student created successfully',
            student
        });
    } catch (error) {
        console.error('Error creating student:', error);
        res.status(500).json({ message: 'Error creating student', error: error.message });
    }
};

/**
 * Update student
 * PUT /students/:studentId
 */
exports.updateStudent = async (req, res) => {
    try {
        const { studentId } = req.params;
        const { name, email, classId, sectionId, rollNumber } = req.body;

        const student = await Student.findByIdAndUpdate(
            studentId,
            {
                name,
                email,
                class_id: classId,
                section_id: sectionId,
                roll_number: rollNumber
            },
            { new: true }
        );

        if (!student) {
            return res.status(404).json({ message: 'Student not found' });
        }

        res.json({
            message: 'Student updated successfully',
            student
        });
    } catch (error) {
        console.error('Error updating student:', error);
        res.status(500).json({ message: 'Error updating student', error: error.message });
    }
};

exports.getDashboardStats = async (req, res) => {
    try {
        const student = await Student.findOne({ user_id: req.user._id });
        if (!student) return res.status(404).json({ message: 'Student profile not found' });

        const attendance = await Attendance.find({ student_id: student._id });
        const results = await Result.find({ student_id: student._id }).populate('exam_id', 'title date');
        const notifications = await Notification.find({ school_id: req.user.school_id }).sort({ createdAt: -1 }).limit(5);

        res.json({
            attendance,
            results,
            notifications
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

exports.getHomework = async (req, res) => {
    try {
        const student = await Student.findOne({ user_id: req.user._id });
        const homework = await Homework.find({
            school_id: req.user.school_id,
            class: student.class
        }).populate('teacher_id', 'name');
        res.json(homework);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};
