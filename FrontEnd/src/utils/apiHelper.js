import { apiUtils, authAPI } from './api';

// Helper function to handle token refresh
export const handleTokenRefresh = async () => {
    try {
        console.log('🔄 Attempting token refresh...');
        const response = await authAPI.refreshToken();

        if (response.success && response.token) {
            console.log('✅ Token refreshed successfully');
            return response.token;
        } else {
            console.log('❌ Token refresh failed');
            return null;
        }
    } catch (error) {
        console.error('❌ Token refresh error:', error);
        return null;
    }
};

// Helper function to handle authentication errors
export const handleAuthError = (error) => {
    const status = error.response?.status;

    if (status === 401) {
        console.log('🔒 Authentication failed - clearing auth data');
        apiUtils.clearAuth();

        // Redirect to login if not already there
        if (window.location.pathname !== '/login') {
            window.location.href = '/login';
        }
        return true; // Handled
    }

    if (status === 403) {
        console.log('🚫 Access forbidden - user may not have required permissions');
        return true; // Handled
    }

    return false; // Not handled
};

// Enhanced API call wrapper with automatic retry
export const enhancedApiCall = async (apiFunction, ...args) => {
    try {
        console.log('📡 Making API call...');
        return await apiFunction(...args);
    } catch (error) {
        console.error('📡 API call failed:', error);

        // If it's an auth error, try to handle it
        if (handleAuthError(error)) {
            throw error; // Re-throw after handling
        }

        // If it's a token expiration, try to refresh
        if (error.response?.status === 401) {
            const newToken = await handleTokenRefresh();

            if (newToken) {
                console.log('🔄 Retrying API call with new token...');
                try {
                    return await apiFunction(...args);
                } catch (retryError) {
                    console.error('🔄 Retry failed:', retryError);
                    throw retryError;
                }
            }
        }

        throw error;
    }
};

// Debug helper to check API health
export const checkApiHealth = async () => {
    try {
        const response = await fetch('http://localhost:5050/api/health');
        const data = await response.json();

        return {
            healthy: response.ok,
            status: response.status,
            data: data
        };
    } catch (error) {
        return {
            healthy: false,
            error: error.message
        };
    }
};

export default {
    handleTokenRefresh,
    handleAuthError,
    enhancedApiCall,
    checkApiHealth
}; 