const mongoose = require('mongoose');

/**
 * Student Schema - Represents a student in the school
 * Links student to their class and section for attendance tracking
 */
const studentSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: true,
        trim: true
    },
    email: { 
        type: String, 
        required: true,
        lowercase: true
    },
    roll_number: {
        type: Number,
        required: false
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
    user_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    school_id: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'School'
    },
    guardian_name: { 
        type: String,
        default: ''
    },
    guardian_phone: { 
        type: String,
        default: ''
    },
    date_of_birth: {
        type: Date
    },
    fees_paid: { 
        type: Boolean, 
        default: false 
    },
    is_active: {
        type: Boolean,
        default: true
    }
}, { timestamps: true });

// Create indexes for faster searches
studentSchema.index({ class_id: 1, section_id: 1 });
studentSchema.index({ email: 1 });
studentSchema.index({ user_id: 1 });

module.exports = mongoose.model('Student', studentSchema);
