const path = require('path');
require('dotenv').config({ path: path.resolve(__dirname, '.env') });
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

// For development - use in-memory MongoDB if local MongoDB not available
let mongoMemoryServer;
if (process.env.NODE_ENV === 'development' && process.env.USE_MEMORY_DB === 'true') {
  try {
    const { MongoMemoryServer } = require('mongodb-memory-server');
    mongoMemoryServer = new MongoMemoryServer();
  } catch (error) {
    console.log('MongoDB Memory Server not available, using regular connection');
  }
}

const app = express();

// Task 5: Configure CORS properly for network access
app.use(cors({
  origin: '*', // Allow all origins for local network testing
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

app.use(express.json());

const PORT = process.env.PORT || 5000;
const MONGO_URI = process.env.MONGO_URI;

console.log('Starting MySchoolHelper backend...');
console.log(`Working directory: ${process.cwd()}`);
console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);
console.log(`Port: ${PORT}`);
console.log(`MongoDB URI provided: ${MONGO_URI ? 'yes' : 'no'}`);

if (!MONGO_URI) {
  console.error('FATAL ERROR: MONGO_URI is not defined. Set MONGO_URI in your environment or .env file.');
  process.exit(1);
}

mongoose.set('strictQuery', false);

mongoose.connection.on('connecting', () => console.log('Mongoose connecting...'));
mongoose.connection.on('connected', () => console.log('MongoDB Connected'));
mongoose.connection.on('error', (err) => console.error('Mongoose connection error:', err.message || err));
mongoose.connection.on('disconnected', () => console.warn('Mongoose disconnected'));

const connectDB = async (retries = 5) => {
  console.log('Attempting MongoDB connection...');

  let mongoUri = MONGO_URI;

  // Use in-memory MongoDB for development if available
  if (mongoMemoryServer) {
    console.log('Starting in-memory MongoDB server...');
    await mongoMemoryServer.start();
    mongoUri = mongoMemoryServer.getUri();
    console.log('Using in-memory MongoDB for development');
  }

  try {
    await mongoose.connect(mongoUri, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
      connectTimeoutMS: 10000,
      serverSelectionTimeoutMS: 10000,
    });

    console.log('MongoDB Connected');
  } catch (error) {
    console.error('MongoDB connection failed:', error.message || error);
    if (retries > 0) {
      console.log(`Retrying MongoDB connection in 5 seconds (${retries} retries remaining)...`);
      setTimeout(() => connectDB(retries - 1), 5000);
    } else {
      console.error('MongoDB connection could not be established. Continuing with degraded mode.');
    }
  }
};

// Task 2: Bind to 0.0.0.0 to make backend accessible on local network
const startServer = () => {
  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://0.0.0.0:${PORT}`);
    console.log(`Accessible on local network via your Laptop IP (e.g., http://10.132.133.184:${PORT})`);
  });
};

// Routes
console.log('Registering API routes...');
try {
  console.log('Loading authRoutes...');
  app.use('/api/auth', require('./routes/authRoutes'));
  console.log('Loaded authRoutes');
} catch (err) {
  console.error('Failed loading authRoutes:', err);
}

try {
  console.log('Loading classRoutes...');
  app.use('/api/classes', require('./routes/classRoutes'));
  console.log('Loaded classRoutes');
} catch (err) {
  console.error('Failed loading classRoutes:', err);
}

try {
  console.log('Loading studentRoutes...');
  app.use('/api/students', require('./routes/studentRoutes'));
  console.log('Loaded studentRoutes');
} catch (err) {
  console.error('Failed loading studentRoutes:', err);
}

try {
  console.log('Loading attendanceRoutes...');
  app.use('/api/attendance', require('./routes/attendanceRoutes'));
  app.use('/api/student/attendance', require('./routes/attendanceRoutes'));
  console.log('Loaded attendanceRoutes');
} catch (err) {
  console.error('Failed loading attendanceRoutes:', err);
}

try {
  console.log('Loading adminRoutes...');
  app.use('/api/admin', require('./routes/adminRoutes'));
  console.log('Loaded adminRoutes');
} catch (err) {
  console.error('Failed loading adminRoutes:', err);
}

console.log('API routes registered.');

// Health check route
app.get('/', (req, res) => {
  res.send('MySchoolHelper API is running...');
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({ message: 'Route not found' });
});

// Global Error Handler
app.use((err, req, res, next) => {
  const statusCode = res.statusCode === 200 ? 500 : res.statusCode;

  res.status(statusCode).json({
    message: err.message,
    stack: process.env.NODE_ENV === 'production' ? null : err.stack,
  });
});

process.on('uncaughtException', (err) => {
  console.error('Uncaught Exception:', err);
  process.exit(1);
});

process.on('unhandledRejection', (reason) => {
  console.error('Unhandled Promise Rejection:', reason);
  process.exit(1);
});

console.log('Starting Express listener...');
startServer();
connectDB();