const mongoose = require('mongoose');

/**
 * Class Schema - Represents a class in the school (e.g., Class 1, Class 2, etc.)
 * Used for organizing students and marking attendance
 */
const classSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: true,
        trim: true
    },
    description: { 
        type: String,
        default: ''
    },
    school_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'School'
    }
}, { timestamps: true });

// Create index for faster searches
classSchema.index({ name: 1, school_id: 1 });

module.exports = mongoose.model('Class', classSchema);
