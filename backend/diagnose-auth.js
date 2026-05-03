/**
 * DIAGNOSTIC SCRIPT - Check and fix login issues
 * Run: node diagnose-auth.js
 */

const path = require('path');
require('dotenv').config({ path: path.resolve(__dirname, '.env') });
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const User = require('./models/User');
const Teacher = require('./models/Teacher');
const Student = require('./models/Student');

async function diagnoseAuthIssues() {
    try {
        console.log('\n📋 AUTHENTICATION DIAGNOSTIC REPORT\n');
        console.log('=' .repeat(50));

        // Connect to MongoDB
        await mongoose.connect(process.env.MONGO_URI, {
            useNewUrlParser: true,
            useUnifiedTopology: true,
        });
        console.log('✅ MongoDB Connected');

        // Check existing users
        console.log('\n📊 EXISTING USERS:');
        console.log('-'.repeat(50));
        
        const users = await User.find().select('email role password');
        
        if (users.length === 0) {
            console.log('❌ No users found! Need to create demo users.');
            console.log('\nCreating demo users...\n');
            await createDemoUsers();
        } else {
            console.log(`Found ${users.length} users:\n`);
            
            for (const user of users) {
                console.log(`📧 Email: ${user.email}`);
                console.log(`   Role: "${user.role}" (${typeof user.role})`);
                console.log(`   Password Hash: ${user.password.substring(0, 20)}...`);
                
                // Check for role issues
                if (!['admin', 'teacher', 'student', 'principal'].includes(user.role?.toLowerCase())) {
                    console.log(`   ⚠️  ISSUE: Role "${user.role}" is not valid!`);
                } else {
                    console.log(`   ✅ Role is valid`);
                }
                console.log('');
            }
        }

        // Check for Teacher/Student records
        console.log('📚 ROLE-SPECIFIC RECORDS:');
        console.log('-'.repeat(50));
        
        const teachers = await Teacher.find();
        const students = await Student.find();
        
        console.log(`✅ Teachers: ${teachers.length}`);
        console.log(`✅ Students: ${students.length}\n`);

        // Test login with known credentials
        console.log('🧪 TEST LOGIN:');
        console.log('-'.repeat(50));
        console.log('Trying to login as teacher@school.com / password123...\n');
        
        const testUser = await User.findOne({ email: 'teacher@school.com' });
        
        if (testUser) {
            console.log('User found!');
            console.log(`Role in DB: "${testUser.role}"`);
            
            const isPasswordValid = await bcrypt.compare('password123', testUser.password);
            console.log(`Password valid: ${isPasswordValid ? '✅ YES' : '❌ NO'}`);
            
            if (isPasswordValid) {
                console.log('\n✅ LOGIN SHOULD WORK!');
            }
        } else {
            console.log('❌ Teacher account not found');
        }

        console.log('\n' + '=' .repeat(50));
        console.log('\n✨ DIAGNOSTIC COMPLETE\n');

        await mongoose.connection.close();

    } catch (error) {
        console.error('\n❌ ERROR:', error.message);
        process.exit(1);
    }
}

async function createDemoUsers() {
    try {
        // Create demo users with correct lowercase roles
        const demoUsers = [
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

        for (const userData of demoUsers) {
            // Check if user already exists
            const existingUser = await User.findOne({ email: userData.email });
            if (existingUser) {
                console.log(`⏭️  User ${userData.email} already exists, skipping...`);
                continue;
            }

            // Create user
            const user = await User.create(userData);
            console.log(`✅ Created: ${user.email} (Role: ${user.role})`);

            // Create teacher record if role is teacher
            if (user.role === 'teacher') {
                await Teacher.create({
                    name: user.name,
                    email: user.email,
                    user_id: user._id
                });
                console.log(`   ✅ Created teacher record`);
            }

            // Create student record if role is student
            if (user.role === 'student') {
                await Student.create({
                    name: user.name,
                    email: user.email,
                    user_id: user._id
                });
                console.log(`   ✅ Created student record`);
            }
        }

        console.log('\n✨ Demo users created successfully!\n');

    } catch (error) {
        console.error('Error creating demo users:', error.message);
    }
}

// Run diagnostic
diagnoseAuthIssues();
