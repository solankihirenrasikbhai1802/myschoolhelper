const mongoose = require('mongoose');
require('dotenv').config();

const School = require('./models/School');

const setupLocalDB = async () => {
    try {
        await mongoose.connect(process.env.MONGO_URI, {
            useNewUrlParser: true,
            useUnifiedTopology: true,
        });
        console.log('Connected to local MongoDB');

        // Check if school exists
        const existingSchool = await School.findOne({ code: 'SCH001' });
        if (existingSchool) {
            console.log('School SCH001 already exists:', existingSchool);
        } else {
            const school = await School.create({
                name: 'Sample School',
                address: '123 Main St',
                code: 'SCH001',
                payment_status: 'paid'
            });
            console.log('Created school:', school);
        }

        // List all schools
        const allSchools = await School.find();
        console.log('All schools:', allSchools);

        process.exit(0);
    } catch (error) {
        console.error('Error:', error.message);
        process.exit(1);
    }
};

setupLocalDB();
