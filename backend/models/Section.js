const mongoose = require('mongoose');

/**
 * Section Schema - Represents a section within a class (e.g., A, B, C)
 * Sections help divide students in a class
 */
const sectionSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: true,
        trim: true
    },
    class_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Class',
        required: true
    },
    description: { 
        type: String,
        default: ''
    }
}, { timestamps: true });

// Create index for faster searches
sectionSchema.index({ class_id: 1, name: 1 });

module.exports = mongoose.model('Section', sectionSchema);
