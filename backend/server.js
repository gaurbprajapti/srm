import express from 'express';
import cors from 'cors';
import path from 'path';
import { config } from 'dotenv';
import { connectDB } from './data/dbConnect.js';
import { errorHandler, notFound } from './middleware/error.js';

// Import routes
import userRoute from './routes/userRoute.js';
import clubRoute from './routes/ClubRoute.js';
import jobRoute from './routes/jobRoute.js';
import blogRoute from './routes/blogRoute.js';

// Load environment variables
config({
    path: "./data/config.env",
});

const app = express();
const port = process.env.PORT || 5050;

// Connect to database
connectDB();

// Trust proxy for rate limiting (if behind a proxy like nginx)
app.set('trust proxy', 1);

// Middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// CORS configuration
app.use(cors({
    origin: function (origin, callback) {
        // Allow requests with no origin (like mobile apps or curl requests)
        if (!origin) return callback(null, true);

        const allowedOrigins = [
            process.env.FRONTEND_URL || "http://localhost:3000",
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            // Production Netlify domain
            "https://campusnexus.netlify.app"
        ];

        if (allowedOrigins.includes(origin)) {
            callback(null, true);
        } else {
            callback(new Error('Not allowed by CORS'));
        }
    },
    credentials: true,
    methods: ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"],
    allowedHeaders: ["Content-Type", "Authorization", "x-csrf-token"],
    exposedHeaders: ["Content-Range", "X-Content-Range"]
}));

// Security headers
app.use((req, res, next) => {
    res.setHeader('X-Content-Type-Options', 'nosniff');
    res.setHeader('X-Frame-Options', 'DENY');
    res.setHeader('X-XSS-Protection', '1; mode=block');
    next();
});

// API Routes
app.use('/api/user', userRoute);
app.use('/api/clubs', clubRoute);
app.use('/api/jobs', jobRoute);
app.use('/api', blogRoute);

// Health check endpoint
app.get('/api/health', (req, res) => {
    res.json({
        success: true,
        message: 'Server is running!',
        timestamp: new Date().toISOString(),
        version: '2.0.0',
        environment: process.env.NODE_ENV || 'development'
    });
});

// API documentation route
app.get('/api', (req, res) => {
    res.json({
        success: true,
        message: 'SRM API v2.0 - JWT Authentication System',
        documentation: {
            auth: '/api/user - User authentication endpoints',
            clubs: '/api/clubs - Club management endpoints',
            jobs: '/api/jobs - Job management endpoints',
            blogs: '/api/blogs - Blog management endpoints',
            health: '/api/health - Health check endpoint'
        },
        authRequired: 'Include Authorization: Bearer <token> header for protected routes'
    });
});

// Production static files serving
if (process.env.NODE_ENV === 'production') {
    app.use(express.static(path.join(process.cwd(), 'client/build')));

    app.get('*', (req, res) => {
        res.sendFile(path.join(process.cwd(), 'client/build/index.html'));
    });
}

// Error handling middleware (must be last)
app.use(notFound);
app.use(errorHandler);

// Graceful shutdown
process.on('SIGTERM', () => {
    console.log('👋 SIGTERM received, shutting down gracefully');
    process.exit(0);
});

process.on('SIGINT', () => {
    console.log('👋 SIGINT received, shutting down gracefully');
    process.exit(0);
});

// Start server
app.listen(port, () => {
    console.log('🚀 ================================');
    console.log(`🚀 Server is running on port ${port}`);
    console.log(`📊 Environment: ${process.env.NODE_ENV || 'development'}`);
    console.log(`🌐 CORS enabled for: ${process.env.FRONTEND_URL || 'http://localhost:3000'}`);
    console.log(`🔐 JWT Authentication: Enabled`);
    console.log(`📚 API Documentation: http://localhost:${port}/api`);
    console.log(`�� Health Check: http://localhost:${port}/api/health`);
    console.log('🚀 ================================');
});
