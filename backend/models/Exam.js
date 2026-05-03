const mongoose = require('mongoose');

const examSchema = new mongoose.Schema({
    school_id: { type: mongoose.Schema.Types.ObjectId, ref: 'School', required: true },
    title: { type: String, required: true },
    date: { type: Date, required: true }
}, { timestamps: true });

module.exports = mongoose.model('Exam', examSchema);
