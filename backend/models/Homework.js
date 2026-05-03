const mongoose = require('mongoose');

const homeworkSchema = new mongoose.Schema({
    teacher_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    school_id: { type: mongoose.Schema.Types.ObjectId, ref: 'School', required: true },
    class: { type: String, required: true },
    description: { type: String, required: true },
    due_date: { type: Date, required: true }
}, { timestamps: true });

module.exports = mongoose.model('Homework', homeworkSchema);
