const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema({
    name: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    // Role must be lowercase for consistency with auth middleware
    role: { type: String, enum: ['admin', 'principal', 'teacher', 'student'], required: true },
    school_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'School'
    }
}, { timestamps: true });

userSchema.pre('save', function(next) {
    if (!this.isModified('password')) return next();
    bcrypt.hash(this.password, 12, (err, hash) => {
        if (err) return next(err);
        this.password = hash;
        next();
    });
});

module.exports = mongoose.model('User', userSchema);
