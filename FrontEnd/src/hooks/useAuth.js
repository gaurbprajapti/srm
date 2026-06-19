import { useState, useEffect } from 'react';
import { apiUtils } from '../utils/api';

export const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = () => {
        try {
            const token = apiUtils.getToken();
            const userData = apiUtils.getCurrentUser();

            console.log('🔐 Auth check:', {
                hasToken: !!token,
                hasUser: !!userData,
                userId: userData?.id || userData?._id
            });

            const isAuth = !!token && !!userData && (userData.id || userData._id);

            setIsAuthenticated(isAuth);
            setUser(userData);
            setLoading(false);
        } catch (error) {
            console.error('Auth check error:', error);
            setIsAuthenticated(false);
            setUser(null);
            setLoading(false);
        }
    };

    const login = (token, userData) => {
        apiUtils.setAuth(token, userData);
        setIsAuthenticated(true);
        setUser(userData);
    };

    const logout = () => {
        apiUtils.clearAuth();
        setIsAuthenticated(false);
        setUser(null);
    };

    return {
        isAuthenticated,
        user,
        loading,
        login,
        logout,
        checkAuthStatus
    };
};

export default useAuth; 