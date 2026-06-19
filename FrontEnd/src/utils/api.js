import axios from 'axios';

// Base configuration
const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:5050';

// Create axios instance with base configuration
const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Token management
const TOKEN_KEY = 'srm-token';
const USER_KEY = 'srm-user';

// Request interceptor to add auth token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem(TOKEN_KEY);
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => {
        return response.data; // Return just the data part
    },
    (error) => {
        // Handle token expiration
        if (error.response?.status === 401) {
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
            // Optionally redirect to login
            if (window.location.pathname !== '/login') {
                window.location.href = '/login';
            }
        }

        // Return the error with consistent structure
        return Promise.reject(error);
    }
);

// Auth API functions
export const authAPI = {
    register: async (userData) => {
        const response = await api.post('/api/user/register', userData);
        if (response.success && response.token) {
            localStorage.setItem(TOKEN_KEY, response.token);
            localStorage.setItem(USER_KEY, JSON.stringify(response.user)); // Fixed: use response.user
        }
        return response;
    },

    login: async (credentials) => {
        const response = await api.post('/api/user/login', credentials);
        if (response.success && response.token) {
            localStorage.setItem(TOKEN_KEY, response.token);
            localStorage.setItem(USER_KEY, JSON.stringify(response.user)); // Fixed: use response.user
        }
        return response;
    },

    logout: async () => {
        try {
            await api.post('/api/user/logout');
        } finally {
            // Always clear local storage, even if API call fails
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
        }
    },

    getProfile: async () => {
        return await api.get('/api/user/profile');
    },

    updateProfile: async (profileData) => {
        const response = await api.put('/api/user/profile', profileData);
        if (response.success && response.data) {
            localStorage.setItem(USER_KEY, JSON.stringify(response.data));
        }
        return response;
    },

    changePassword: async (passwordData) => {
        return await api.put('/api/user/change-password', passwordData);
    },

    refreshToken: async () => {
        const response = await api.post('/api/user/refresh-token');
        if (response.success && response.token) {
            localStorage.setItem(TOKEN_KEY, response.token);
        }
        return response;
    }
};

// Club API functions
export const clubAPI = {
    getClubs: async (params = {}) => {
        // Filter out undefined values to prevent ?category=undefined
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        const queryString = new URLSearchParams(cleanParams).toString();
        return await api.get(`/api/clubs${queryString ? '?' + queryString : ''}`);
    },

    getClub: async (id) => {
        return await api.get(`/api/clubs/${id}`);
    },

    createClub: async (clubData) => {
        return await api.post('/api/clubs', clubData);
    },

    updateClub: async (id, clubData) => {
        return await api.put(`/api/clubs/${id}`, clubData);
    },

    deleteClub: async (id) => {
        return await api.delete(`/api/clubs/${id}`);
    }
};

// Job API functions
export const jobAPI = {
    getJobs: async (params = {}) => {
        // Filter out undefined values to prevent ?type=undefined
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        const queryString = new URLSearchParams(cleanParams).toString();
        return await api.get(`/api/jobs${queryString ? '?' + queryString : ''}`);
    },

    getJob: async (id) => {
        return await api.get(`/api/jobs/${id}`);
    },

    createJob: async (jobData) => {
        return await api.post('/api/jobs', jobData);
    },

    updateJob: async (id, jobData) => {
        return await api.put(`/api/jobs/${id}`, jobData);
    },

    deleteJob: async (id) => {
        return await api.delete(`/api/jobs/${id}`);
    }
};

// Blog API functions
export const blogAPI = {
    getBlogs: async (params = {}) => {
        // Filter out undefined values to prevent ?category=undefined
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        const queryString = new URLSearchParams(cleanParams).toString();
        return await api.get(`/api/blogs${queryString ? '?' + queryString : ''}`);
    },

    getFeaturedBlogs: async () => {
        return await api.get('/api/blogs/featured');
    },

    getBlogsByCategory: async (category, params = {}) => {
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        const queryString = new URLSearchParams(cleanParams).toString();
        return await api.get(`/api/blogs/category/${category}${queryString ? '?' + queryString : ''}`);
    },

    getBlogsByAuthor: async (authorId, params = {}) => {
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        const queryString = new URLSearchParams(cleanParams).toString();
        return await api.get(`/api/blogs/author/${authorId}${queryString ? '?' + queryString : ''}`);
    },

    getMyBlogs: async (params = {}) => {
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        const queryString = new URLSearchParams(cleanParams).toString();
        return await api.get(`/api/blogs/my${queryString ? '?' + queryString : ''}`);
    },

    getBlog: async (id) => {
        return await api.get(`/api/blog/${id}`);
    },

    createBlog: async (blogData) => {
        return await api.post('/api/blog/new', blogData);
    },

    updateBlog: async (id, blogData) => {
        return await api.put(`/api/blog/${id}`, blogData);
    },

    deleteBlog: async (id) => {
        return await api.delete(`/api/blog/${id}`);
    },

    likeBlog: async (id) => {
        return await api.post(`/api/blog/${id}/like`);
    },

    getBlogStats: async () => {
        return await api.get('/api/blogs/stats');
    },

    getAllBlogsAdmin: async (params = {}) => {
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        const queryString = new URLSearchParams(cleanParams).toString();
        return await api.get(`/api/admin/blogs${queryString ? '?' + queryString : ''}`);
    }
};

// Utility functions
export const apiUtils = {
    isAuthenticated: () => {
        return !!localStorage.getItem(TOKEN_KEY);
    },

    getCurrentUser: () => {
        const userData = localStorage.getItem(USER_KEY);
        return userData ? JSON.parse(userData) : null;
    },

    getToken: () => {
        return localStorage.getItem(TOKEN_KEY);
    },

    clearAuth: () => {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    },

    setAuth: (token, user) => {
        localStorage.setItem(TOKEN_KEY, token);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    }
};

export default api; 