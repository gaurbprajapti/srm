import React, { useState, useEffect } from 'react';
import { apiUtils } from '../utils/api';

const ApiTest = () => {
    const [healthStatus, setHealthStatus] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [registrationTest, setRegistrationTest] = useState(null);

    // Test health endpoint
    const testHealthEndpoint = async () => {
        try {
            setLoading(true);
            const response = await fetch('http://localhost:5050/api/health');
            const data = await response.json();
            setHealthStatus(data);
            setError(null);
        } catch (err) {
            setError(`Health check failed: ${err.message}`);
            setHealthStatus(null);
        } finally {
            setLoading(false);
        }
    };

    // Test API documentation endpoint
    const testApiDocEndpoint = async () => {
        try {
            const response = await fetch('http://localhost:5050/api/');
            const data = await response.json();
            console.log('API Documentation:', data);
            return data;
        } catch (err) {
            console.error('API Doc test failed:', err);
            return null;
        }
    };

    // Test registration endpoint
    const testRegistrationEndpoint = async () => {
        try {
            const testUser = {
                username: `testuser_${Date.now()}`,
                email: `test_${Date.now()}@example.com`,
                password: 'password123',
                firstName: 'Test',
                lastName: 'User'
            };

            const response = await fetch('http://localhost:5050/api/user/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(testUser)
            });

            const data = await response.json();
            setRegistrationTest(data);
            return data;
        } catch (err) {
            setRegistrationTest({ error: err.message });
            return null;
        }
    };

    // Test using the API utility
    const testWithApiUtils = async () => {
        try {
            console.log('Testing with API Utils...');
            const healthData = await apiUtils.get('/health');
            console.log('API Utils Health:', healthData);
        } catch (err) {
            console.error('API Utils test failed:', err);
        }
    };

    useEffect(() => {
        testHealthEndpoint();
        testApiDocEndpoint();
        testWithApiUtils();
    }, []);

    return (
        <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
            <h2>🔧 Backend Connectivity Test</h2>

            <div style={{ marginBottom: '20px', padding: '15px', border: '1px solid #ddd', borderRadius: '8px' }}>
                <h3>📡 Health Check</h3>
                {loading ? (
                    <p>Testing connection...</p>
                ) : error ? (
                    <div style={{ color: 'red' }}>
                        <strong>❌ Connection Failed:</strong> {error}
                        <p>Make sure your Spring Boot backend is running on http://localhost:5050</p>
                    </div>
                ) : healthStatus ? (
                    <div style={{ color: 'green' }}>
                        <strong>✅ Backend Connected Successfully!</strong>
                        <pre style={{ background: '#f5f5f5', padding: '10px', marginTop: '10px' }}>
                            {JSON.stringify(healthStatus, null, 2)}
                        </pre>
                    </div>
                ) : null}

                <button
                    onClick={testHealthEndpoint}
                    style={{ marginTop: '10px', padding: '8px 16px', cursor: 'pointer' }}
                >
                    🔄 Test Again
                </button>
            </div>

            <div style={{ marginBottom: '20px', padding: '15px', border: '1px solid #ddd', borderRadius: '8px' }}>
                <h3>🧪 Test Registration Endpoint</h3>
                <button
                    onClick={testRegistrationEndpoint}
                    style={{ padding: '8px 16px', cursor: 'pointer', marginBottom: '10px' }}
                >
                    Test User Registration
                </button>

                {registrationTest && (
                    <div>
                        <strong>Registration Test Result:</strong>
                        <pre style={{ background: '#f5f5f5', padding: '10px', marginTop: '10px', fontSize: '12px' }}>
                            {JSON.stringify(registrationTest, null, 2)}
                        </pre>
                    </div>
                )}
            </div>

            <div style={{ padding: '15px', border: '1px solid #ddd', borderRadius: '8px' }}>
                <h3>ℹ️ Backend Information</h3>
                <ul>
                    <li><strong>Backend URL:</strong> http://localhost:5050</li>
                    <li><strong>Frontend URL:</strong> {window.location.origin}</li>
                    <li><strong>API Base URL:</strong> {process.env.REACT_APP_API_URL || 'http://localhost:5050'}</li>
                    <li><strong>CORS Enabled:</strong> ✅ Yes</li>
                </ul>

                <h4>Available Endpoints:</h4>
                <ul>
                    <li>GET /api/health - Health check</li>
                    <li>GET /api/ - API documentation</li>
                    <li>POST /api/user/register - User registration</li>
                    <li>POST /api/user/login - User login</li>
                    <li>GET /api/user/profile - User profile (requires auth)</li>
                </ul>
            </div>
        </div>
    );
};

export default ApiTest; 