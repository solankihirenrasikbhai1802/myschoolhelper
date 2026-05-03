/**
 * SIMPLE LOGIN TEST SCRIPT
 * Run: node test-login.js
 * 
 * Ye directly MongoDB se connect karke user check karta hai
 * aur password verify karta hai
 */

const path = require('path');
require('dotenv').config({ path: path.resolve(__dirname, '.env') });
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const User = require('./models/User');

async function testLogin() {
    try {
        console.log('\n🔐 LOGIN TEST SCRIPT');
        console.log('='.repeat(60));
        
        // Connect
        console.log('\n📡 Connecting to MongoDB...');
        await mongoose.connect(process.env.MONGO_URI, {
            useNewUrlParser: true,
            useUnifiedTopology: true,
            serverSelectionTimeoutMS: 5000,
            socketTimeoutMS: 5000,
        });
        console.log('✅ Connected to MongoDB');

        // Test credentials
        const testCases = [
            { email: 'teacher@school.com', password: 'password123' },
            { email: 'student@school.com', password: 'password123' },
            { email: 'admin@school.com', password: 'password123' }
        ];

        console.log('\n' + '='.repeat(60));
        console.log('🧪 TESTING LOGIN CREDENTIALS');
        console.log('='.repeat(60));

        for (const testCase of testCases) {
            console.log(`\n📧 Testing: ${testCase.email}`);
            console.log('-'.repeat(60));
            
            const user = await User.findOne({ email: testCase.email });
            
            if (!user) {
                console.log('❌ USER NOT FOUND IN DATABASE');
                console.log('   Status: Need to create this user');
            } else {
                console.log(`✅ User found in DB`);
                console.log(`   Name: ${user.name}`);
                console.log(`   Email: ${user.email}`);
                console.log(`   Role: ${user.role}`);
                console.log(`   Role Type: ${typeof user.role}`);
                
                // Test password
                const isValid = await bcrypt.compare(testCase.password, user.password);
                
                if (isValid) {
                    console.log(`✅ PASSWORD CORRECT - Login should work!`);
                } else {
                    console.log(`❌ PASSWORD INCORRECT`);
                    console.log(`   Try resetting with: npm run seed`);
                }
            }
        }

        console.log('\n' + '='.repeat(60));
        console.log('\n✨ TEST COMPLETE\n');

        // If no users exist, offer to create them
        const userCount = await User.countDocuments();
        if (userCount === 0) {
            console.log('⚠️  NO USERS FOUND');
            console.log('\nTo create demo users, run:');
            console.log('   npm run seed\n');
        }

        await mongoose.connection.close();

    } catch (error) {
        console.error('\n❌ ERROR:', error.message);
        
        if (error.message.includes('connection')) {
            console.error('\nConnection Error - Check:');
            console.error('1. Is MongoDB running?');
            console.error('2. Is MONGO_URI correct in .env?');
            console.error('3. Can you reach the MongoDB server?');
        }
        
        process.exit(1);
    }
}

// Run test
testLogin();
