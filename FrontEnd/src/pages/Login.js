import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Spin, message, Avatar } from 'antd';
import { Link, useNavigate } from 'react-router-dom';
import { UserOutlined, LockOutlined, HomeOutlined } from '@ant-design/icons';
import '../resources/authentication.css'
import { authAPI, apiUtils } from '../utils/api';

function Login() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const onfinish = async (values) => {
        setLoading(true);
        try {
            console.log('🔐 Attempting login with:', values);
            const response = await authAPI.login({
                username: values.email, // Backend expects 'username' field
                password: values.password
            });

            console.log('🔐 Login response:', response);
            setLoading(false);

            if (response.success) {
                message.success(response.message || "Login successful");
                // Small delay to ensure localStorage is updated
                setTimeout(() => {
                    navigate('/home');
                }, 100);
            } else {
                message.error(response.message || "Login failed");
            }
        } catch (error) {
            console.error('Login error:', error);
            setLoading(false);
            const errorMessage = error.response?.data?.message || error.message || "Login failed";
            message.error(errorMessage);
        }
    };

    // Redirect to home if already authenticated
    useEffect(() => {
        const isAuth = apiUtils.isAuthenticated();
        const user = apiUtils.getCurrentUser();
        console.log('🔐 Login page - checking auth:', { isAuth, user });
        
        if (isAuth && user && (user._id || user.id)) {
            console.log('🔐 User already authenticated, redirecting to home');
            navigate('/home', { replace: true });
        }
    }, [navigate]);

    const testimonials = [
        {
            quote: "Cloud Campus Nexus has transformed how students connect with opportunities on campus. The platform makes it incredibly easy to discover clubs, find jobs, and build professional networks.",
            name: "Sarah Johnson",
            role: "Computer Science Student",
            company: "SRM University",
            avatar: "SJ"
        },
        {
            quote: "As a career counselor, I've seen firsthand how this platform helps students showcase their skills and connect with relevant opportunities. It's a game-changer for campus engagement.",
            name: "Dr. Michael Chen",
            role: "Career Services Director",
            company: "University Career Center",
            avatar: "MC"
        },
        {
            quote: "The resume builder and job matching features are exceptional. I landed my dream internship through connections I made on this platform.",
            name: "Priya Sharma",
            role: "Marketing Student",
            company: "SRM University",
            avatar: "PS"
        }
    ];

    const [currentTestimonial, setCurrentTestimonial] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentTestimonial((prev) => (prev + 1) % testimonials.length);
        }, 5000);
        return () => clearInterval(interval);
    }, [testimonials.length]);

    return (
        <div className="auth-container">
            {/* Left Side - Testimonials */}
            <div className="auth-left">
                <div className="testimonial-section">
                    <div className="brand-header">
                        <div className="brand-icon">
                            <HomeOutlined />
                        </div>
                        <h1>Cloud Campus Nexus</h1>
                        <p>Your Gateway to Campus Opportunities</p>
                    </div>

                    <div className="testimonial-content">
                        <div className="quote-icon">"</div>
                        <p className="testimonial-text">
                            {testimonials[currentTestimonial].quote}
                        </p>

                        <div className="testimonial-author">
                            <Avatar size={48} style={{ backgroundColor: '#4F46E5', fontSize: '18px' }}>
                                {testimonials[currentTestimonial].avatar}
                            </Avatar>
                            <div className="author-info">
                                <div className="author-name">{testimonials[currentTestimonial].name}</div>
                                <div className="author-role">{testimonials[currentTestimonial].role}</div>
                                <div className="author-company">{testimonials[currentTestimonial].company}</div>
                            </div>
                        </div>

                        <div className="testimonial-dots">
                            {testimonials.map((_, index) => (
                                <span
                                    key={index}
                                    className={`dot ${index === currentTestimonial ? 'active' : ''}`}
                                    onClick={() => setCurrentTestimonial(index)}
                                />
                            ))}
                        </div>
                    </div>

                    <div className="trusted-by">
                        <p>Trusted by Students in Over 50+ Universities</p>
                        <div className="university-logos">
                            <span>SRM</span>
                            <span>VIT</span>
                            <span>BITS</span>
                            <span>IIT</span>
                            <span>NIT</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Right Side - Login Form */}
            <div className="auth-right">
                <div className="auth-form-container">
                    <div className="form-header">
                        <h2>Log In to your account</h2>
                        <p>To continue where you left off, please enter your details.</p>
                    </div>

                    <Form
                        layout='vertical'
                        onFinish={onfinish}
                        autoComplete="off"
                        className="auth-form"
                    >
                        <Form.Item
                            name='email'
                            label='Email'
                            rules={[
                                { required: true, message: 'Email is required' },
                                { type: 'email', message: 'Please enter a valid email' }
                            ]}
                        >
                            <Input
                                prefix={<UserOutlined />}
                                placeholder="Your Email"
                                size="large"
                            />
                        </Form.Item>

                        <Form.Item
                            name='password'
                            label={
                                <div className="password-label">
                                    <span>Password</span>
                                    <Link to="/forgot-password" className="forgot-link">
                                        Forgot Password?
                                    </Link>
                                </div>
                            }
                            rules={[{ required: true, message: 'Password is required' }]}
                        >
                            <Input.Password
                                prefix={<LockOutlined />}
                                placeholder="Your Password"
                                size="large"
                            />
                        </Form.Item>

                        <Form.Item>
                            <Button
                                type="primary"
                                htmlType='submit'
                                loading={loading}
                                block
                                size="large"
                                className="auth-button"
                            >
                                Login
                            </Button>
                        </Form.Item>

                        <div className="auth-divider">
                            <span>or</span>
                        </div>

                        <Button
                            block
                            size="large"
                            className="sso-button"
                            icon={<UserOutlined />}
                        >
                            Log In Using Single Sign On
                        </Button>

                        <div className="auth-footer">
                            <span>New to Cloud Campus Nexus? </span>
                            <Link to='/register' className="auth-link">
                                Sign Up
                            </Link>
                        </div>
                    </Form>

                    {loading && (
                        <div className="loading-overlay">
                            <Spin size='large' />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Login;