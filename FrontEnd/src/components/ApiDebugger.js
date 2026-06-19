import React, { useState, useEffect } from 'react';
import { Button, Card, Alert, Spin } from 'antd';
import { clubAPI, jobAPI, apiUtils } from '../utils/api';

const ApiDebugger = () => {
    const [debugInfo, setDebugInfo] = useState({});
    const [testing, setTesting] = useState(false);

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = () => {
        const token = apiUtils.getToken();
        const user = apiUtils.getCurrentUser();
        const isAuth = apiUtils.isAuthenticated();

        setDebugInfo({
            isAuthenticated: isAuth,
            hasToken: !!token,
            tokenLength: token ? token.length : 0,
            user: user,
            userId: user?.id || user?._id,
            timestamp: new Date().toISOString()
        });
    };

    const testClubsAPI = async () => {
        setTesting(true);
        try {
            console.log('🧪 Testing Clubs API...');
            const response = await clubAPI.getClubs({ page: 1, limit: 10 });
            console.log('🧪 Clubs API Response:', response);

            setDebugInfo(prev => ({
                ...prev,
                clubsTest: {
                    success: true,
                    response: response,
                    timestamp: new Date().toISOString()
                }
            }));
        } catch (error) {
            console.error('🧪 Clubs API Error:', error);
            setDebugInfo(prev => ({
                ...prev,
                clubsTest: {
                    success: false,
                    error: error.message,
                    status: error.response?.status,
                    data: error.response?.data,
                    timestamp: new Date().toISOString()
                }
            }));
        }
        setTesting(false);
    };

    const testJobsAPI = async () => {
        setTesting(true);
        try {
            console.log('🧪 Testing Jobs API...');
            const response = await jobAPI.getJobs({ page: 1, limit: 10 });
            console.log('🧪 Jobs API Response:', response);

            setDebugInfo(prev => ({
                ...prev,
                jobsTest: {
                    success: true,
                    response: response,
                    timestamp: new Date().toISOString()
                }
            }));
        } catch (error) {
            console.error('🧪 Jobs API Error:', error);
            setDebugInfo(prev => ({
                ...prev,
                jobsTest: {
                    success: false,
                    error: error.message,
                    status: error.response?.status,
                    data: error.response?.data,
                    timestamp: new Date().toISOString()
                }
            }));
        }
        setTesting(false);
    };

    const testDirectFetch = async () => {
        setTesting(true);
        try {
            const token = apiUtils.getToken();
            console.log('🧪 Testing direct fetch with token:', token?.substring(0, 20) + '...');

            const response = await fetch('http://localhost:5050/api/clubs', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            const data = await response.json();

            setDebugInfo(prev => ({
                ...prev,
                directTest: {
                    success: response.ok,
                    status: response.status,
                    response: data,
                    timestamp: new Date().toISOString()
                }
            }));
        } catch (error) {
            console.error('🧪 Direct fetch error:', error);
            setDebugInfo(prev => ({
                ...prev,
                directTest: {
                    success: false,
                    error: error.message,
                    timestamp: new Date().toISOString()
                }
            }));
        }
        setTesting(false);
    };

    return (
        <div style={{ padding: '20px', maxWidth: '1000px', margin: '0 auto' }}>
            <h2>🔍 API Debugger</h2>

            {/* Authentication Status */}
            <Card title="🔐 Authentication Status" style={{ marginBottom: '20px' }}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                    <div><strong>Authenticated:</strong> {debugInfo.isAuthenticated ? '✅ Yes' : '❌ No'}</div>
                    <div><strong>Has Token:</strong> {debugInfo.hasToken ? '✅ Yes' : '❌ No'}</div>
                    <div><strong>Token Length:</strong> {debugInfo.tokenLength} chars</div>
                    <div><strong>User ID:</strong> {debugInfo.userId || 'None'}</div>
                </div>

                {debugInfo.user && (
                    <div style={{ marginTop: '10px' }}>
                        <strong>User Info:</strong>
                        <pre style={{ background: '#f5f5f5', padding: '10px', fontSize: '12px' }}>
                            {JSON.stringify(debugInfo.user, null, 2)}
                        </pre>
                    </div>
                )}

                <Button onClick={checkAuthStatus} style={{ marginTop: '10px' }}>
                    🔄 Refresh Auth Status
                </Button>
            </Card>

            {/* API Tests */}
            <Card title="🧪 API Tests" style={{ marginBottom: '20px' }}>
                <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
                    <Button
                        onClick={testClubsAPI}
                        loading={testing}
                        type="primary"
                    >
                        Test Clubs API
                    </Button>
                    <Button
                        onClick={testJobsAPI}
                        loading={testing}
                        type="primary"
                    >
                        Test Jobs API
                    </Button>
                    <Button
                        onClick={testDirectFetch}
                        loading={testing}
                    >
                        Test Direct Fetch
                    </Button>
                </div>

                {/* Clubs Test Results */}
                {debugInfo.clubsTest && (
                    <Alert
                        message={`Clubs API Test - ${debugInfo.clubsTest.success ? 'SUCCESS' : 'FAILED'}`}
                        description={
                            <pre style={{ fontSize: '12px', maxHeight: '200px', overflow: 'auto' }}>
                                {JSON.stringify(debugInfo.clubsTest, null, 2)}
                            </pre>
                        }
                        type={debugInfo.clubsTest.success ? 'success' : 'error'}
                        style={{ marginBottom: '10px' }}
                    />
                )}

                {/* Jobs Test Results */}
                {debugInfo.jobsTest && (
                    <Alert
                        message={`Jobs API Test - ${debugInfo.jobsTest.success ? 'SUCCESS' : 'FAILED'}`}
                        description={
                            <pre style={{ fontSize: '12px', maxHeight: '200px', overflow: 'auto' }}>
                                {JSON.stringify(debugInfo.jobsTest, null, 2)}
                            </pre>
                        }
                        type={debugInfo.jobsTest.success ? 'success' : 'error'}
                        style={{ marginBottom: '10px' }}
                    />
                )}

                {/* Direct Test Results */}
                {debugInfo.directTest && (
                    <Alert
                        message={`Direct Fetch Test - ${debugInfo.directTest.success ? 'SUCCESS' : 'FAILED'}`}
                        description={
                            <pre style={{ fontSize: '12px', maxHeight: '200px', overflow: 'auto' }}>
                                {JSON.stringify(debugInfo.directTest, null, 2)}
                            </pre>
                        }
                        type={debugInfo.directTest.success ? 'success' : 'error'}
                        style={{ marginBottom: '10px' }}
                    />
                )}
            </Card>

            {/* Recommendations */}
            <Card title="💡 Recommendations">
                <ul>
                    <li>If authentication shows ❌, please login again</li>
                    <li>If token is missing, check localStorage in browser dev tools</li>
                    <li>If API tests fail with 401/403, token might be expired</li>
                    <li>If direct fetch works but API utils fail, check axios interceptors</li>
                    <li>Check browser console for detailed error messages</li>
                </ul>
            </Card>
        </div>
    );
};

export default ApiDebugger; 