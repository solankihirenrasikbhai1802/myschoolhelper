const mongoose = require('mongoose');

/**
 * Teacher Schema - Represents a teacher in the school
 * Links teacher to the classes they teach
 */
const teacherSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: true,
        trim: true
    },
    email: { 
        type: String, 
        required: true,
        unique: true,
        lowercase: true
    },
    phone: {
        type: String,
        default: ''
    },
    subject: {
        type: String,
        default: ''
    },
    qualification: {
        type: String,
        default: ''
    },
    user_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    assigned_classes: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Class'
    }],
    is_active: {
        type: Boolean,
        default: true
    }
}, { timestamps: true });

// Create indexes
teacherSchema.index({ email: 1 });
teacherSchema.index({ user_id: 1 });

module.exports = mongoose.model('Teacher', teacherSchema);
