const mongoose = require('mongoose');

/**
 * Attendance Schema - Tracks student attendance records
 * Includes unique index to prevent duplicate attendance for same date
 * Fields for class, section, and teacher tracking
 */
const attendanceSchema = new mongoose.Schema({
    student_id: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Student', 
        required: true 
    },
    class_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Class',
        required: true
    },
    section_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Section',
        required: true
    },
    date: { 
        type: Date, 
        required: true,
        set: (val) => {
            const date = new Date(val);
            date.setHours(0, 0, 0, 0);
            return date;
        }
    },
    status: { 
        type: String, 
        enum: ['Present', 'Absent', 'Late', 'Leave'], 
        default: 'Present', 
        required: true 
    },
    marked_by: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'User', 
        required: true 
    },
    remarks: { 
        type: String, 
        default: '' 
    }
}, { timestamps: true });

// Create index to prevent duplicate attendance for same student on same date
attendanceSchema.index({ student_id: 1, date: 1 }, { unique: true });

// Create indexes for faster searches
attendanceSchema.index({ class_id: 1, section_id: 1, date: 1 });
attendanceSchema.index({ date: 1 });
attendanceSchema.index({ marked_by: 1 });

module.exports = mongoose.model('Attendance', attendanceSchema);
