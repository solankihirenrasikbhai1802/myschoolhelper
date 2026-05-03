const mongoose = require('mongoose');

const schoolSchema = new mongoose.Schema({
    name: { type: String, required: true },
    address: { type: String, required: true },
    code: { type: String, required: true, unique: true },
    payment_status: { type: String, enum: ['paid', 'pending'], default: 'pending' }
}, { timestamps: true });

module.exports = mongoose.model('School', schoolSchema);
