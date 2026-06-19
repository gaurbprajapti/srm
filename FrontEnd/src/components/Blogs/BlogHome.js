import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { BlogCard } from './BlogCard';
import { blogAPI } from '../../utils/api';
import { apiUtils } from '../../utils/api';
import './BlogHome.css';

export const BlogHome = () => {
    const [blogs, setBlogs] = useState([]);
    const [featuredBlogs, setFeaturedBlogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [stats, setStats] = useState(null);

    const categories = [
        'Technology', 'Career', 'Campus Life', 'Events',
        'Academics', 'Sports', 'Culture', 'Others'
    ];

    const isAuthenticated = apiUtils.isAuthenticated();

    useEffect(() => {
        fetchBlogs();
        fetchFeaturedBlogs();
        fetchStats();
    }, [selectedCategory, searchTerm, currentPage]);

    const fetchBlogs = async () => {
        try {
            setLoading(true);
            const params = {
                page: currentPage,
                ...(selectedCategory && { category: selectedCategory }),
                ...(searchTerm && { keyword: searchTerm })
            };

            const response = await blogAPI.getBlogs(params);
            if (response.success) {
                setBlogs(response.blogs);
                setTotalPages(response.totalPages);
            }
        } catch (error) {
            console.error('Error fetching blogs:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchFeaturedBlogs = async () => {
        try {
            const response = await blogAPI.getFeaturedBlogs();
            if (response.success) {
                setFeaturedBlogs(response.blogs);
            }
        } catch (error) {
            console.error('Error fetching featured blogs:', error);
        }
    };

    const fetchStats = async () => {
        try {
            const response = await blogAPI.getBlogStats();
            if (response.success) {
                setStats(response.stats);
            }
        } catch (error) {
            console.error('Error fetching stats:', error);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        setCurrentPage(1);
        fetchBlogs();
    };

    const handleCategoryChange = (category) => {
        setSelectedCategory(category === selectedCategory ? '' : category);
        setCurrentPage(1);
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        <div className="blog-home">
            {/* Hero Section */}
            <div className="blog-hero">
                <div className="hero-content">
                    <h1>Campus Nexus Blog</h1>
                    <p>Discover stories, insights, and knowledge from our campus community</p>
                    {isAuthenticated && (
                        <Link to="/blog/new" className="btn-primary">
                            <i className="fas fa-plus"></i> Write a Blog
                        </Link>
                    )}
                </div>
            </div>

            {/* Stats Section */}
            {stats && (
                <div className="blog-stats">
                    <div className="stats-container">
                        <div className="stat-item">
                            <h3>{stats.totalBlogs}</h3>
                            <p>Published Blogs</p>
                        </div>
                        <div className="stat-item">
                            <h3>{stats.totalViews}</h3>
                            <p>Total Views</p>
                        </div>
                        <div className="stat-item">
                            <h3>{stats.categoryStats?.length || 0}</h3>
                            <p>Categories</p>
                        </div>
                    </div>
                </div>
            )}

            {/* Featured Blogs */}
            {featuredBlogs.length > 0 && (
                <section className="featured-section">
                    <h2>Featured Blogs</h2>
                    <div className="featured-blogs">
                        {featuredBlogs.map(blog => (
                            <div key={blog._id} className="featured-blog-card">
                                <BlogCard blog={blog} featured={true} />
                            </div>
                        ))}
                    </div>
                </section>
            )}

            {/* Search and Filters */}
            <div className="blog-controls">
                <div className="search-container">
                    <form onSubmit={handleSearch} className="search-form">
                        <input
                            type="text"
                            placeholder="Search blogs..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="search-input"
                        />
                        <button type="submit" className="search-btn">
                            <i className="fas fa-search"></i>
                        </button>
                    </form>
                </div>

                <div className="category-filters">
                    <button
                        className={`category-btn ${!selectedCategory ? 'active' : ''}`}
                        onClick={() => handleCategoryChange('')}
                    >
                        All
                    </button>
                    {categories.map(category => (
                        <button
                            key={category}
                            className={`category-btn ${selectedCategory === category ? 'active' : ''}`}
                            onClick={() => handleCategoryChange(category)}
                        >
                            {category}
                        </button>
                    ))}
                </div>
            </div>

            {/* Blog Grid */}
            <main className="blog-content">
                {loading ? (
                    <div className="loading-spinner">
                        <i className="fas fa-spinner fa-spin"></i>
                        <p>Loading blogs...</p>
                    </div>
                ) : blogs.length > 0 ? (
                    <>
                        <div className="blogs-grid">
                            {blogs.map(blog => (
                                <BlogCard key={blog._id} blog={blog} />
                            ))}
                        </div>

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div className="pagination">
                                <button
                                    onClick={() => handlePageChange(currentPage - 1)}
                                    disabled={currentPage === 1}
                                    className="pagination-btn"
                                >
                                    <i className="fas fa-chevron-left"></i> Previous
                                </button>

                                <div className="pagination-numbers">
                                    {[...Array(totalPages)].map((_, index) => (
                                        <button
                                            key={index + 1}
                                            onClick={() => handlePageChange(index + 1)}
                                            className={`pagination-number ${currentPage === index + 1 ? 'active' : ''}`}
                                        >
                                            {index + 1}
                                        </button>
                                    ))}
                                </div>

                                <button
                                    onClick={() => handlePageChange(currentPage + 1)}
                                    disabled={currentPage === totalPages}
                                    className="pagination-btn"
                                >
                                    Next <i className="fas fa-chevron-right"></i>
                                </button>
                            </div>
                        )}
                    </>
                ) : (
                    <div className="no-blogs">
                        <i className="fas fa-blog"></i>
                        <h3>No blogs found</h3>
                        <p>
                            {selectedCategory || searchTerm
                                ? 'Try adjusting your filters or search terms.'
                                : 'Be the first to write a blog!'}
                        </p>
                        {isAuthenticated && (
                            <Link to="/blog/new" className="btn-primary">
                                Write First Blog
                            </Link>
                        )}
                    </div>
                )}
            </main>

            {/* Popular Blogs Sidebar */}
            {stats?.popularBlogs && stats.popularBlogs.length > 0 && (
                <aside className="sidebar">
                    <div className="popular-blogs">
                        <h3>Popular Blogs</h3>
                        {stats.popularBlogs.map(blog => (
                            <Link key={blog._id} to={`/blog/${blog._id}`} className="popular-blog-item">
                                <h4>{blog.title}</h4>
                                <div className="blog-meta">
                                    <span><i className="fas fa-eye"></i> {blog.views}</span>
                                    <span><i className="fas fa-heart"></i> {blog.likes?.length || 0}</span>
                                </div>
                            </Link>
                        ))}
                    </div>
                </aside>
            )}
        </div>
    );
};
