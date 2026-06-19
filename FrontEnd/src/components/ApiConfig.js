import React from 'react';

const ApiConfig = () => {
    const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:5050';
    const isProduction = process.env.NODE_ENV === 'production';
    const currentUrl = window.location.origin;

    return (
        <div style={{
            position: 'fixed',
            bottom: '10px',
            right: '10px',
            background: '#f8f9fa',
            border: '1px solid #ddd',
            borderRadius: '8px',
            padding: '10px',
            fontSize: '12px',
            maxWidth: '300px',
            zIndex: 1000,
            boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
        }}>
            <strong>🔧 API Configuration</strong>
            <div style={{ marginTop: '5px' }}>
                <div><strong>Frontend:</strong> {currentUrl}</div>
                <div><strong>Backend:</strong> {apiUrl}</div>
                <div><strong>Environment:</strong> {isProduction ? 'Production' : 'Development'}</div>
                <div><strong>CORS:</strong> ✅ Enabled</div>
            </div>
            <div style={{ marginTop: '8px', fontSize: '11px', color: '#666' }}>
                <div>Visit <a href="/api-test" style={{ color: '#007bff' }}>/api-test</a> to test connectivity</div>
                <div>Visit <a href="/debug" style={{ color: '#ff6b35' }}>/debug</a> for API debugging</div>
            </div>
        </div>
    );
};

export default ApiConfig; 