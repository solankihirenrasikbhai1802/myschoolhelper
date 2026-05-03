// File: backend/seedAttendanceData.js
// Run with: node seedAttendanceData.js

require('dotenv').config();
const mongoose = require('mongoose');
const Attendance = require('./models/Attendance');
const Student = require('./models/Student');
const User = require('./models/User');

const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017/myschoolhelper';

async function seedData() {
    try {
        // Connect to MongoDB
        await mongoose.connect(mongoUri);
        console.log('Connected to MongoDB');

        // Clear existing data
        await Attendance.deleteMany({});
        console.log('Cleared existing attendance records');

        // Sample data for testing
        const classId = new mongoose.Types.ObjectId();
        const teacherId = new mongoose.Types.ObjectId();

        // Create sample students
        const students = [];
        for (let i = 1; i <= 30; i++) {
            students.push({
                _id: new mongoose.Types.ObjectId(),
                name: `Student ${i}`,
                rollNumber: i.toString().padStart(3, '0'),
                email: `student${i}@school.com`,
                phone: `98765432${String(i).padStart(2, '0')}`,
                classId: classId,
                parentEmail: `parent${i}@email.com`
            });
        }

        console.log(`Created ${students.length} sample students`);

        // Create attendance records for the past 30 days
        const attendanceRecords = [];
        const statuses = ['Present', 'Absent', 'Late', 'Leave'];
        const today = new Date();

        for (let day = 0; day < 30; day++) {
            const date = new Date(today);
            date.setDate(date.getDate() - day);

            // Skip weekends
            if (date.getDay() === 0 || date.getDay() === 6) continue;

            // Create attendance for each student
            students.forEach((student, index) => {
                // Randomly assign status (weighted towards Present)
                const rand = Math.random();
                let status;
                if (rand < 0.75) status = 'Present';
                else if (rand < 0.85) status = 'Absent';
                else if (rand < 0.92) status = 'Late';
                else status = 'Leave';

                attendanceRecords.push({
                    date: date,
                    studentId: student._id,
                    studentName: student.name,
                    rollNumber: student.rollNumber,
                    classId: classId,
                    status: status,
                    remarks: status === 'Absent' ? 'Not present' : 
                             status === 'Late' ? 'Late arrival' :
                             status === 'Leave' ? 'Leave request approved' : '',
                    markedBy: teacherId,
                    createdAt: new Date(),
                    updatedAt: new Date()
                });
            });
        }

        // Insert attendance records
        await Attendance.insertMany(attendanceRecords);
        console.log(`Created ${attendanceRecords.length} attendance records`);

        // Calculate statistics
        const totalRecords = attendanceRecords.length;
        const presentCount = attendanceRecords.filter(r => r.status === 'Present').length;
        const absentCount = attendanceRecords.filter(r => r.status === 'Absent').length;
        const lateCount = attendanceRecords.filter(r => r.status === 'Late').length;
        const leaveCount = attendanceRecords.filter(r => r.status === 'Leave').length;

        console.log('\n=== Attendance Statistics ===');
        console.log(`Total Records: ${totalRecords}`);
        console.log(`Present: ${presentCount} (${((presentCount/totalRecords)*100).toFixed(2)}%)`);
        console.log(`Absent: ${absentCount} (${((absentCount/totalRecords)*100).toFixed(2)}%)`);
        console.log(`Late: ${lateCount} (${((lateCount/totalRecords)*100).toFixed(2)}%)`);
        console.log(`Leave: ${leaveCount} (${((leaveCount/totalRecords)*100).toFixed(2)}%)`);

        // Create sample monthly reports for each student
        const monthlyReports = [];
        students.forEach((student) => {
            const studentAttendance = attendanceRecords.filter(r => r.studentId.toString() === student._id.toString());
            const present = studentAttendance.filter(r => r.status === 'Present').length;
            const absent = studentAttendance.filter(r => r.status === 'Absent').length;
            const late = studentAttendance.filter(r => r.status === 'Late').length;
            const leave = studentAttendance.filter(r => r.status === 'Leave').length;
            const total = studentAttendance.length;

            monthlyReports.push({
                studentId: student._id,
                studentName: student.name,
                month: '01-2024',
                presentDays: present,
                absentDays: absent,
                lateDays: late,
                leaveDays: leave,
                totalDays: total,
                attendancePercentage: total > 0 ? (present / total) * 100 : 0
            });
        });

        console.log(`\nGenerated ${monthlyReports.length} monthly reports`);

        console.log('\n✅ Data seeding completed successfully!');
        console.log('\nYou can now run the attendance system with test data.');

        process.exit(0);
    } catch (error) {
        console.error('Error seeding data:', error);
        process.exit(1);
    } finally {
        await mongoose.disconnect();
    }
}

// Run the seed function
seedData();
