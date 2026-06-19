import { message } from 'antd';
import { apiUtils } from './api';

// Simple fix for common authentication issues
export const fixCommonApiIssues = () => {
    console.log('🔧 Checking for common API issues...');

    // Check if user is authenticated
    const isAuth = apiUtils.isAuthenticated();
    const user = apiUtils.getCurrentUser();
    const token = apiUtils.getToken();

    console.log('Auth Status:', { isAuth, hasUser: !!user, hasToken: !!token });

    // Issue 1: Token exists but user data is corrupted
    if (token && (!user || (!user.id && !user._id))) {
        console.log('🔧 Fix: Clearing corrupted user data');
        apiUtils.clearAuth();
        message.warning('Session expired. Please login again.');
        window.location.href = '/login';
        return false;
    }

    // Issue 2: User data exists but no token
    if (user && !token) {
        console.log('🔧 Fix: Clearing incomplete auth data');
        apiUtils.clearAuth();
        message.warning('Authentication incomplete. Please login again.');
        window.location.href = '/login';
        return false;
    }

    // Issue 3: Check token format
    if (token && token.split('.').length !== 3) {
        console.log('🔧 Fix: Invalid JWT token format');
        apiUtils.clearAuth();
        message.error('Invalid authentication token. Please login again.');
        window.location.href = '/login';
        return false;
    }

    return true; // All checks passed
};

// Quick token validation
export const validateToken = () => {
    const token = apiUtils.getToken();
    if (!token) return false;

    try {
        // Basic JWT structure check
        const parts = token.split('.');
        if (parts.length !== 3) return false;

        // Decode payload to check expiration
        const payload = JSON.parse(atob(parts[1]));
        const now = Date.now() / 1000;

        if (payload.exp && payload.exp < now) {
            console.log('🔧 Token expired');
            return false;
        }

        return true;
    } catch (error) {
        console.error('🔧 Token validation error:', error);
        return false;
    }
};

export default {
    fixCommonApiIssues,
    validateToken
}; 