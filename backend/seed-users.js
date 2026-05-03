/**
 * SEED USERS SCRIPT
 * Run with: npm run seed
 * 
 * Creates demo users with correct role format (lowercase)
 */

const path = require('path');
require('dotenv').config({ path: path.resolve(__dirname, '.env') });
const mongoose = require('mongoose');

const User = require('./models/User');
const Teacher = require('./models/Teacher');
const Student = require('./models/Student');

const DEMO_USERS = [
    {
        name: 'Teacher User',
        email: 'teacher@school.com',
        password: 'password123',
        role: 'teacher'
    },
    {
        name: 'Student User',
        email: 'student@school.com',
        password: 'password123',
        role: 'student'
    },
    {
        name: 'Admin User',
        email: 'admin@school.com',
        password: 'password123',
        role: 'admin'
    }
];

async function seedUsers() {
    try {
        console.log('\n🌱 SEEDING DEMO USERS');
        console.log('='.repeat(60));
        
        // Connect to MongoDB
        console.log('\n📡 Connecting to MongoDB...');
        await mongoose.connect(process.env.MONGO_URI, {
            useNewUrlParser: true,
            useUnifiedTopology: true,
            serverSelectionTimeoutMS: 10000,
        });
        console.log('✅ Connected to MongoDB');

        // Clear existing users (optional - uncomment if you want fresh start)
        // const deleted = await User.deleteMany({});
        // console.log(`\n🗑️  Deleted ${deleted.deletedCount} existing users`);

        console.log('\n👥 Creating demo users...\n');

        for (const userData of DEMO_USERS) {
            // Check if user already exists
            const existingUser = await User.findOne({ email: userData.email });
            
            if (existingUser) {
                console.log(`⏭️  User "${userData.email}" already exists`);
                console.log(`   Role: ${existingUser.role}`);
                console.log(`   Status: Skipping...\n`);
                continue;
            }

            try {
                // Create user
                const user = await User.create(userData);
                console.log(`✅ Created user: ${user.email}`);
                console.log(`   Name: ${user.name}`);
                console.log(`   Role: ${user.role}`);
                console.log(`   Password: ${userData.password} (hashed in DB)`);

                // Create role-specific records
                if (user.role === 'teacher') {
                    await Teacher.create({
                        name: user.name,
                        email: user.email,
                        user_id: user._id
                    });
                    console.log(`   ✅ Created teacher record`);
                } 
                else if (user.role === 'student') {
                    await Student.create({
                        name: user.name,
                        email: user.email,
                        user_id: user._id
                    });
                    console.log(`   ✅ Created student record`);
                }
                
                console.log('');

            } catch (error) {
                console.log(`❌ Error creating user: ${error.message}\n`);
            }
        }

        // Show summary
        console.log('='.repeat(60));
        console.log('\n📊 SUMMARY');
        console.log('-'.repeat(60));
        
        const totalUsers = await User.countDocuments();
        const teachers = await Teacher.countDocuments();
        const students = await Student.countDocuments();
        
        console.log(`Total Users: ${totalUsers}`);
        console.log(`Teachers: ${teachers}`);
        console.log(`Students: ${students}`);

        console.log('\n✨ SEEDING COMPLETE!\n');
        console.log('🔐 LOGIN CREDENTIALS:');
        console.log('-'.repeat(60));
        
        DEMO_USERS.forEach(user => {
            console.log(`\n📧 ${user.email}`);
            console.log(`   Password: ${user.password}`);
            console.log(`   Role: ${user.role}`);
        });

        console.log('\n' + '='.repeat(60) + '\n');

        await mongoose.connection.close();

    } catch (error) {
        console.error('\n❌ SEEDING FAILED:', error.message);
        
        if (error.message.includes('MONGO_URI')) {
            console.error('\n⚠️  MongoDB URI not configured');
            console.error('Please add MONGO_URI to backend/.env file');
        } else if (error.message.includes('connection')) {
            console.error('\n⚠️  Cannot connect to MongoDB');
            console.error('Make sure MongoDB is running');
        }
        
        process.exit(1);
    }
}

// Run seeding
seedUsers();
