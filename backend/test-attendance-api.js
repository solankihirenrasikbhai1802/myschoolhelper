/**
 * Attendance API Test Script
 * Tests all attendance endpoints without needing to run Android app
 */

const axios = require('axios');
const BASE_URL = 'http://10.215.152.184:5000';

let teacherToken = '';
let studentToken = '';
let testStudentId = '';
let testAttendanceId = '';

// Color output helpers
const log = {
    success: (msg) => console.log('\x1b[32m✓\x1b[0m', msg),
    error: (msg) => console.log('\x1b[31m✗\x1b[0m', msg),
    info: (msg) => console.log('\x1b[36mℹ\x1b[0m', msg),
    header: (msg) => console.log('\n\x1b[1m' + msg + '\x1b[0m')
};

async function testAttendanceEndpoints() {
    try {
        log.header('=== ATTENDANCE API TESTING ===\n');

        // Step 1: Login as Teacher
        log.info('Step 1: Login as Teacher');
        try {
            const teacherRes = await axios.post(`${BASE_URL}/api/auth/login`, {
                email: 'teacher@school.com',
                password: 'password123'
            });
            teacherToken = teacherRes.data.token;
            log.success('Teacher login successful');
            log.info(`Token: ${teacherToken.substring(0, 20)}...`);
        } catch (err) {
            log.error('Teacher login failed: ' + (err.response?.data?.message || err.message));
            log.info('Make sure teacher@school.com exists in database');
        }

        // Step 2: Login as Student
        log.info('\nStep 2: Login as Student');
        try {
            const studentRes = await axios.post(`${BASE_URL}/api/auth/login`, {
                email: 'student@school.com',
                password: 'password123'
            });
            studentToken = studentRes.data.token;
            log.success('Student login successful');
            log.info(`Token: ${studentToken.substring(0, 20)}...`);
        } catch (err) {
            log.error('Student login failed: ' + (err.response?.data?.message || err.message));
        }

        if (!teacherToken || !studentToken) {
            log.error('\nLogin failed. Please run seedAttendanceData.js first to create test users');
            return;
        }

        // Step 3: Test GET /api/attendance/students (Teacher only)
        log.header('Testing Teacher Endpoints\n');
        log.info('Test: GET /api/attendance/students');
        try {
            const studentsRes = await axios.get(`${BASE_URL}/api/attendance/students`, {
                headers: { 'Authorization': `Bearer ${teacherToken}` }
            });
            log.success('GET /api/attendance/students');
            log.info(`Retrieved ${studentsRes.data.length} students`);
            if (studentsRes.data.length > 0) {
                testStudentId = studentsRes.data[0]._id;
                log.info(`Using student ID for further tests: ${testStudentId}`);
            }
        } catch (err) {
            log.error('GET /api/attendance/students failed: ' + (err.response?.data?.message || err.message));
        }

        // Step 4: Test POST /api/attendance/mark (Teacher only)
        log.info('\nTest: POST /api/attendance/mark');
        if (testStudentId) {
            try {
                const today = new Date().toISOString().split('T')[0];
                const markRes = await axios.post(`${BASE_URL}/api/attendance/mark`, {
                    student_id: testStudentId,
                    date: today,
                    status: 'Present'
                }, {
                    headers: { 'Authorization': `Bearer ${teacherToken}` }
                });
                log.success('POST /api/attendance/mark');
                log.info('Attendance marked: ' + JSON.stringify(markRes.data));
            } catch (err) {
                log.error('POST /api/attendance/mark failed: ' + (err.response?.data?.message || err.message));
            }
        }

        // Step 5: Test GET /api/attendance/student/:id (Teacher only)
        log.info('\nTest: GET /api/attendance/student/:id');
        if (testStudentId) {
            try {
                const attendanceRes = await axios.get(`${BASE_URL}/api/attendance/student/${testStudentId}`, {
                    headers: { 'Authorization': `Bearer ${teacherToken}` }
                });
                log.success('GET /api/attendance/student/:id');
                log.info(`Retrieved ${attendanceRes.data.length} attendance records`);
            } catch (err) {
                log.error('GET /api/attendance/student/:id failed: ' + (err.response?.data?.message || err.message));
            }
        }

        // Step 6: Test GET /api/attendance/class/:id (Teacher only)
        log.info('\nTest: GET /api/attendance/class/:id');
        try {
            const today = new Date().toISOString().split('T')[0];
            const classRes = await axios.get(`${BASE_URL}/api/attendance/class/class1?date=${today}`, {
                headers: { 'Authorization': `Bearer ${teacherToken}` }
            });
            log.success('GET /api/attendance/class/:id');
            log.info(`Retrieved ${classRes.data.length} class attendance records`);
        } catch (err) {
            log.error('GET /api/attendance/class/:id failed: ' + (err.response?.data?.message || err.message));
        }

        // Step 7: Test Student endpoints
        log.header('Testing Student Endpoints\n');
        log.info('Test: GET /api/attendance/my (Student only)');
        try {
            const myAttendanceRes = await axios.get(`${BASE_URL}/api/attendance/my`, {
                headers: { 'Authorization': `Bearer ${studentToken}` }
            });
            log.success('GET /api/attendance/my');
            log.info(`Retrieved ${myAttendanceRes.data.length} records`);
        } catch (err) {
            log.error('GET /api/attendance/my failed: ' + (err.response?.data?.message || err.message));
        }

        // Step 8: Test General endpoints
        log.header('Testing General Endpoints\n');
        log.info('Test: GET /api/attendance/stats');
        try {
            const statsRes = await axios.get(`${BASE_URL}/api/attendance/stats`, {
                headers: { 'Authorization': `Bearer ${teacherToken}` }
            });
            log.success('GET /api/attendance/stats');
            log.info(`Stats: ${JSON.stringify(statsRes.data)}`);
        } catch (err) {
            log.error('GET /api/attendance/stats failed: ' + (err.response?.data?.message || err.message));
        }

        log.header('=== TEST COMPLETE ===\n');

    } catch (err) {
        log.error('Unexpected error: ' + err.message);
    }
}

// Run tests
testAttendanceEndpoints();
