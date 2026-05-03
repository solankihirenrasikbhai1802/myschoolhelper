const Attendance = require('../models/Attendance');
const Student = require('../models/Student');
const User = require('../models/User');

/**
 * Attendance Controller - Handles all attendance related operations
 * Includes marking, viewing, updating, and reporting attendance
 */

/**
 * Mark attendance for multiple students
 * POST /attendance/mark
 * Body: { records: [{student_id, date, status, classId, sectionId}], classId, sectionId, date }
 */
exports.markAttendance = async (req, res) => {
    try {
        const { records, classId, sectionId, date } = req.body;
        const teacherId = req.user._id;

        // Validate input
        if (!records || !Array.isArray(records) || records.length === 0) {
            return res.status(400).json({ message: 'Records array is required' });
        }

        if (!classId || !sectionId || !date) {
            return res.status(400).json({ message: 'Class ID, Section ID, and Date are required' });
        }

        // Create attendance records with teacher info
        const attendanceRecords = records.map(record => ({
            ...record,
            marked_by: teacherId,
            class_id: classId,
            section_id: sectionId,
            date: new Date(date)
        }));

        // Insert records (this may fail if duplicates exist due to unique index)
        const savedRecords = await Attendance.insertMany(attendanceRecords, { ordered: false }).catch(error => {
            // If some records failed due to duplicates, continue with the rest
            if (error.code === 11000) {
                // Duplicate key error - try updating existing records
                return null;
            }
            throw error;
        });

        if (!savedRecords) {
            // Try updating existing records
            for (const record of attendanceRecords) {
                await Attendance.findOneAndUpdate(
                    {
                        student_id: record.student_id,
                        date: new Date(date)
                    },
                    {
                        status: record.status,
                        remarks: record.remarks || '',
                        marked_by: teacherId
                    },
                    { upsert: true }
                );
            }
        }

        res.status(201).json({
            message: 'Attendance marked successfully',
            count: records.length
        });
    } catch (error) {
        console.error('Error marking attendance:', error);
        res.status(500).json({ message: 'Error marking attendance', error: error.message });
    }
};

/**
 * Update existing attendance
 * PUT /attendance/update
 */
exports.updateAttendance = async (req, res) => {
    try {
        const { records, date, classId, sectionId } = req.body;
        const teacherId = req.user._id;

        if (!records || !Array.isArray(records) || records.length === 0) {
            return res.status(400).json({ message: 'Records array is required' });
        }

        // Update each attendance record
        const updatePromises = records.map(record =>
            Attendance.findOneAndUpdate(
                {
                    student_id: record.student_id,
                    date: new Date(date),
                    class_id: classId,
                    section_id: sectionId
                },
                {
                    status: record.status,
                    remarks: record.remarks || '',
                    marked_by: teacherId
                },
                { new: true }
            )
        );

        await Promise.all(updatePromises);

        res.json({
            message: 'Attendance updated successfully',
            count: records.length
        });
    } catch (error) {
        console.error('Error updating attendance:', error);
        res.status(500).json({ message: 'Error updating attendance', error: error.message });
    }
};

/**
 * Check if attendance already exists for a date
 * GET /attendance/check-existing
 * Query: { classId, sectionId, date }
 */
exports.checkExistingAttendance = async (req, res) => {
    try {
        const { classId, sectionId, date } = req.query;

        if (!classId || !sectionId || !date) {
            return res.status(400).json({ message: 'Class ID, Section ID, and Date are required' });
        }

        const existingRecords = await Attendance.find({
            class_id: classId,
            section_id: sectionId,
            date: new Date(date)
        }).populate('student_id');

        res.json({
            exists: existingRecords.length > 0,
            attendance: existingRecords
        });
    } catch (error) {
        console.error('Error checking attendance:', error);
        res.status(500).json({ message: 'Error checking attendance', error: error.message });
    }
};

/**
 * Get attendance records for teacher (view attendance)
 * GET /attendance/view
 * Query: { classId, sectionId, fromDate, toDate }
 */
exports.getAttendanceRecords = async (req, res) => {
    try {
        const { classId, sectionId, fromDate, toDate } = req.query;

        if (!classId || !sectionId) {
            return res.status(400).json({ message: 'Class ID and Section ID are required' });
        }

        let query = {
            class_id: classId,
            section_id: sectionId
        };

        // Add date range if provided
        if (fromDate || toDate) {
            query.date = {};
            if (fromDate) query.date.$gte = new Date(fromDate);
            if (toDate) {
                const endDate = new Date(toDate);
                endDate.setHours(23, 59, 59, 999);
                query.date.$lte = endDate;
            }
        }

        const records = await Attendance.find(query)
            .populate('student_id', 'name email')
            .sort({ date: -1 })
            .lean();

        res.json(records);
    } catch (error) {
        console.error('Error fetching attendance records:', error);
        res.status(500).json({ message: 'Error fetching attendance records', error: error.message });
    }
};

/**
 * Get student's attendance records
 * GET /student/attendance/records
 * Query: { month } - Format: YYYY-MM
 */
exports.getStudentAttendanceRecords = async (req, res) => {
    try {
        const studentId = req.user._id;
        const { month } = req.query;

        // Find student record
        const student = await Student.findOne({ user_id: studentId });
        if (!student) {
            return res.status(404).json({ message: 'Student not found' });
        }

        let query = { student_id: student._id };

        // Filter by month if provided
        if (month) {
            const [year, monthNum] = month.split('-');
            const startDate = new Date(`${year}-${monthNum}-01`);
            const endDate = new Date(startDate);
            endDate.setMonth(endDate.getMonth() + 1);

            query.date = {
                $gte: startDate,
                $lt: endDate
            };
        }

        const records = await Attendance.find(query)
            .sort({ date: 1 })
            .lean();

        // Calculate statistics
        const stats = {
            total: records.length,
            present: records.filter(r => r.status === 'Present').length,
            absent: records.filter(r => r.status === 'Absent').length,
            late: records.filter(r => r.status === 'Late').length
        };

        res.json({ records, stats });
    } catch (error) {
        console.error('Error fetching student attendance:', error);
        res.status(500).json({ message: 'Error fetching attendance records', error: error.message });
    }
};

/**
 * Get student's attendance statistics
 * GET /student/attendance/stats
 */
exports.getStudentAttendanceStats = async (req, res) => {
    try {
        const studentId = req.user._id;

        // Find student record
        const student = await Student.findOne({ user_id: studentId });
        if (!student) {
            return res.status(404).json({ message: 'Student not found' });
        }

        // Get all attendance records for this student
        const records = await Attendance.find({ student_id: student._id })
            .lean();

        // Calculate statistics
        const stats = {
            totalDays: records.length,
            presentDays: records.filter(r => r.status === 'Present').length,
            absentDays: records.filter(r => r.status === 'Absent').length,
            lateDays: records.filter(r => r.status === 'Late').length,
            percentage: records.length > 0 
                ? Math.round((records.filter(r => r.status === 'Present').length / records.length) * 100)
                : 0
        };

        res.json(stats);
    } catch (error) {
        console.error('Error fetching student stats:', error);
        res.status(500).json({ message: 'Error fetching statistics', error: error.message });
    }
};

/**
 * Export attendance report (for admin)
 * GET /admin/attendance/reports
 * Query: { search, classId, fromDate, toDate }
 */
exports.getAttendanceReport = async (req, res) => {
    try {
        const { search, classId, fromDate, toDate } = req.query;

        let query = {};

        // Add class filter
        if (classId) {
            query.class_id = classId;
        }

        // Add date range
        if (fromDate || toDate) {
            query.date = {};
            if (fromDate) query.date.$gte = new Date(fromDate);
            if (toDate) {
                const endDate = new Date(toDate);
                endDate.setHours(23, 59, 59, 999);
                query.date.$lte = endDate;
            }
        }

        let records = await Attendance.find(query)
            .populate('student_id', 'name email')
            .populate('marked_by', 'name email')
            .sort({ date: -1 })
            .lean();

        // Apply search filter on results
        if (search) {
            records = records.filter(record =>
                record.student_id?.name?.toLowerCase().includes(search.toLowerCase()) ||
                record.student_id?.email?.toLowerCase().includes(search.toLowerCase())
            );
        }

        res.json(records);
    } catch (error) {
        console.error('Error fetching report:', error);
        res.status(500).json({ message: 'Error fetching report', error: error.message });
    }
};

// @desc    Get students for attendance marking (Teacher only)
// @route   GET /api/attendance/students
// @access  Private/Teacher
const getStudentsForAttendance = async (req, res) => {
    try {
        const teacher = req.user;
        if (teacher.role !== 'Teacher') {
            return res.status(403).json({ message: 'Only teachers can access this' });
        }

        // Get all students in the teacher's school
        const students = await Student.find({ school_id: teacher.school_id })
            .populate('user_id', 'name email')
            .select('class section');

        // Format for frontend
        const studentList = students.map(student => ({
            _id: student._id,
            name: student.user_id.name,
            email: student.user_id.email,
            class: student.class,
            section: student.section,
            user_id: student.user_id._id
        }));

        res.json(studentList);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Mark attendance for students
// @route   POST /api/attendance/mark
// @access  Private/Teacher
const markAttendance = async (req, res) => {
    try {
        const { student_id, date, status } = req.body;
        const teacher = req.user;

        if (teacher.role !== 'Teacher') {
            return res.status(403).json({ message: 'Only teachers can mark attendance' });
        }

        // Verify student belongs to teacher's school
        const student = await Student.findOne({ _id: student_id, school_id: teacher.school_id });
        if (!student) {
            return res.status(404).json({ message: 'Student not found in your school' });
        }

        // Check if attendance already marked for this date
        const existingAttendance = await Attendance.findOne({
            student_id,
            date: new Date(date)
        });

        if (existingAttendance) {
            // Update existing
            existingAttendance.status = status;
            existingAttendance.marked_by = teacher._id;
            await existingAttendance.save();
            res.json({ message: 'Attendance updated successfully' });
        } else {
            // Create new
            const attendance = await Attendance.create({
                student_id,
                date: new Date(date),
                status,
                marked_by: teacher._id
            });
            res.status(201).json({ message: 'Attendance marked successfully' });
        }
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Get student's own attendance records
// @route   GET /api/attendance/my
// @access  Private/Student
const getMyAttendance = async (req, res) => {
    try {
        const user = req.user;
        if (user.role !== 'Student') {
            return res.status(403).json({ message: 'Only students can access their own attendance' });
        }

        // Find student record
        const student = await Student.findOne({ user_id: user._id });
        if (!student) {
            return res.status(404).json({ message: 'Student record not found' });
        }

        const attendance = await Attendance.find({ student_id: student._id })
            .sort({ date: -1 })
            .populate('marked_by', 'name');

        // Format for frontend
        const records = attendance.map(record => ({
            date: record.date.toISOString().split('T')[0],
            status: record.status,
            marked_by: record.marked_by.name
        }));

        res.json(records);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Get specific student's attendance
// @route   GET /api/attendance/student/:id
// @access  Private/Teacher
const getStudentAttendance = async (req, res) => {
    try {
        const { id } = req.params;
        const { startDate, endDate } = req.query;

        const query = { student_id: id };

        if (startDate || endDate) {
            query.date = {};
            if (startDate) query.date.$gte = new Date(startDate);
            if (endDate) query.date.$lte = new Date(endDate);
        }

        const attendance = await Attendance.find(query)
            .sort({ date: -1 })
            .populate('marked_by', 'name');

        const records = attendance.map(record => ({
            id: record._id,
            date: record.date.toISOString().split('T')[0],
            studentId: record.student_id,
            status: record.status,
            marked_by: record.marked_by?.name,
            remarks: record.remarks || ''
        }));

        res.json(records);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Get attendance for a specific class on a date
// @route   GET /api/attendance/class/:id
// @access  Private/Teacher
const getClassAttendance = async (req, res) => {
    try {
        const { id } = req.params;
        const { date } = req.query;

        if (!date) {
            return res.status(400).json({ message: 'Date parameter is required' });
        }

        const startOfDay = new Date(date);
        startOfDay.setHours(0, 0, 0, 0);
        const endOfDay = new Date(date);
        endOfDay.setHours(23, 59, 59, 999);

        const attendance = await Attendance.find({
            date: { $gte: startOfDay, $lte: endOfDay }
        })
            .populate('student_id', 'class section')
            .populate('marked_by', 'name');

        const records = attendance.map(record => ({
            id: record._id,
            date: record.date.toISOString().split('T')[0],
            studentId: record.student_id._id,
            classId: record.student_id.class,
            status: record.status,
            remarks: record.remarks || ''
        }));

        res.json(records);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Update attendance record
// @route   PUT /api/attendance/:id
// @access  Private/Teacher
const updateAttendance = async (req, res) => {
    try {
        const { id } = req.params;
        const { status, remarks } = req.body;

        const attendance = await Attendance.findByIdAndUpdate(
            id,
            { status, remarks, marked_by: req.user._id },
            { new: true }
        ).populate('marked_by', 'name');

        if (!attendance) {
            return res.status(404).json({ message: 'Attendance record not found' });
        }

        const record = {
            id: attendance._id,
            date: attendance.date.toISOString().split('T')[0],
            status: attendance.status,
            remarks: attendance.remarks,
            marked_by: attendance.marked_by?.name
        };

        res.json(record);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Delete attendance record
// @route   DELETE /api/attendance/:id
// @access  Private/Teacher
const deleteAttendance = async (req, res) => {
    try {
        const { id } = req.params;

        const attendance = await Attendance.findByIdAndDelete(id);

        if (!attendance) {
            return res.status(404).json({ message: 'Attendance record not found' });
        }

        res.json({ message: 'Attendance record deleted successfully' });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Get attendance statistics
// @route   GET /api/attendance/stats
// @access  Private
const getAttendanceStats = async (req, res) => {
    try {
        const { studentId, classId, month } = req.query;
        const query = {};

        if (studentId) query.student_id = studentId;
        if (month) {
            const [year, monthNum] = month.split('-');
            const startDate = new Date(year, parseInt(monthNum) - 1, 1);
            const endDate = new Date(year, parseInt(monthNum), 0);
            query.date = { $gte: startDate, $lte: endDate };
        }

        const total = await Attendance.countDocuments(query);
        const present = await Attendance.countDocuments({ ...query, status: 'Present' });
        const absent = await Attendance.countDocuments({ ...query, status: 'Absent' });
        const late = await Attendance.countDocuments({ ...query, status: 'Late' });
        const leave = await Attendance.countDocuments({ ...query, status: 'Leave' });

        const percentage = total > 0 ? Math.round((present / total) * 100) : 0;

        res.json({
            total,
            present,
            absent,
            late,
            leave,
            percentage
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Get monthly attendance report
// @route   GET /api/attendance/report/monthly
// @access  Private
const getMonthlyReport = async (req, res) => {
    try {
        const { studentId, classId, month } = req.query;

        if (!month) {
            return res.status(400).json({ message: 'Month parameter is required' });
        }

        const [year, monthNum] = month.split('-');
        const startDate = new Date(year, parseInt(monthNum) - 1, 1);
        const endDate = new Date(year, parseInt(monthNum), 0);

        const query = {
            date: { $gte: startDate, $lte: endDate }
        };

        if (studentId) query.student_id = studentId;

        const attendance = await Attendance.find(query)
            .populate('student_id', 'class section')
            .sort({ date: -1 });

        const grouped = {};
        attendance.forEach(record => {
            const dateKey = record.date.toISOString().split('T')[0];
            if (!grouped[dateKey]) {
                grouped[dateKey] = { date: dateKey, present: 0, absent: 0, late: 0, leave: 0 };
            }
            grouped[dateKey][record.status.toLowerCase()]++;
        });

        res.json({
            month,
            report: Object.values(grouped),
            summary: {
                total: attendance.length,
                present: attendance.filter(a => a.status === 'Present').length,
                absent: attendance.filter(a => a.status === 'Absent').length,
                late: attendance.filter(a => a.status === 'Late').length,
                leave: attendance.filter(a => a.status === 'Leave').length
            }
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// @desc    Get bulk attendance report for date range
// @route   GET /api/attendance/report/bulk
// @access  Private
const getBulkAttendanceReport = async (req, res) => {
    try {
        const { classId, startDate, endDate } = req.query;

        if (!startDate || !endDate) {
            return res.status(400).json({ message: 'startDate and endDate parameters are required' });
        }

        const query = {
            date: { $gte: new Date(startDate), $lte: new Date(endDate) }
        };

        const attendance = await Attendance.find(query)
            .populate('student_id', 'class section')
            .sort({ date: -1 });

        const reports = [];
        attendance.forEach(record => {
            const dateKey = record.date.toISOString().split('T')[0];
            let report = reports.find(r => r.month === dateKey);
            if (!report) {
                report = {
                    month: dateKey,
                    report: [],
                    summary: { total: 0, present: 0, absent: 0, late: 0, leave: 0 }
                };
                reports.push(report);
            }
            report.summary.total++;
            report.summary[record.status.toLowerCase()]++;
        });

        res.json(reports);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

module.exports = {
    getStudentsForAttendance,
    markAttendance,
    getMyAttendance,
    getStudentAttendance,
    getClassAttendance,
    updateAttendance,
    deleteAttendance,
    getAttendanceStats,
    getMonthlyReport,
    getBulkAttendanceReport
};