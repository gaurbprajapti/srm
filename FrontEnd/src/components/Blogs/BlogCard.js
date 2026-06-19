import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { blogAPI, apiUtils } from '../../utils/api';
import './BlogCard.css';

export const BlogCard = ({ blog, featured = false, onUpdate }) => {
    const [isLiked, setIsLiked] = useState(false);
    const [likesCount, setLikesCount] = useState(blog.likes?.length || 0);
    const [liking, setLiking] = useState(false);

    const isAuthenticated = apiUtils.isAuthenticated();
    const currentUser = apiUtils.getCurrentUser();

    // Check if current user has liked this blog
    React.useEffect(() => {
        if (currentUser && blog.likes) {
            setIsLiked(blog.likes.includes(currentUser.id));
        }
    }, [currentUser, blog.likes]);

    const handleLike = async (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (!isAuthenticated) {
            alert('Please login to like blogs');
            return;
        }

        if (liking) return;

        try {
            setLiking(true);
            const response = await blogAPI.likeBlog(blog._id);

            if (response.success) {
                setIsLiked(response.isLiked);
                setLikesCount(response.likesCount);

                if (onUpdate) {
                    onUpdate(blog._id, { isLiked: response.isLiked, likesCount: response.likesCount });
                }
            }
        } catch (error) {
            console.error('Error toggling like:', error);
            alert('Failed to update like status');
        } finally {
            setLiking(false);
        }
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    const truncateText = (text, maxLength) => {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    };

    const getCategoryColor = (category) => {
        const colors = {
            'Technology': '#667eea',
            'Career': '#f093fb',
            'Campus Life': '#4facfe',
            'Events': '#43e97b',
            'Academics': '#fa709a',
            'Sports': '#ff6b6b',
            'Culture': '#845ec2',
            'Others': '#6c757d'
        };
        return colors[category] || colors['Others'];
    };

    return (
        <div className={`blog-card ${featured ? 'featured' : ''}`}>
            {featured && <div className="featured-badge">Featured</div>}

            {blog.image && (
                <div className="blog-image">
                    <img src={blog.image} alt={blog.title} />
                    <div className="image-overlay">
                        <Link to={`/blog/${blog._id}`} className="read-more-overlay">
                            Read More
                        </Link>
                    </div>
                </div>
            )}

            <div className="blog-content">
                <div className="blog-header">
                    <div className="blog-category">
                        <span
                            className="category-tag"
                            style={{ backgroundColor: getCategoryColor(blog.category) }}
                        >
                            {blog.category}
                        </span>
                        {blog.status === 'draft' && (
                            <span className="status-badge draft">Draft</span>
                        )}
                    </div>

                    <div className="blog-meta-top">
                        <span className="blog-date">
                            <i className="fas fa-calendar"></i>
                            {formatDate(blog.createdAt)}
                        </span>
                    </div>
                </div>

                <Link to={`/blog/${blog._id}`} className="blog-title-link">
                    <h3 className="blog-title">
                        {truncateText(blog.title, featured ? 80 : 60)}
                    </h3>
                </Link>

                <p className="blog-summary">
                    {truncateText(blog.summary, featured ? 150 : 120)}
                </p>

                {blog.tags && blog.tags.length > 0 && (
                    <div className="blog-tags">
                        {blog.tags.slice(0, 3).map((tag, index) => (
                            <span key={index} className="blog-tag">
                                #{tag}
                            </span>
                        ))}
                        {blog.tags.length > 3 && (
                            <span className="blog-tag more-tags">
                                +{blog.tags.length - 3}
                            </span>
                        )}
                    </div>
                )}

                <div className="blog-footer">
                    <div className="blog-author">
                        <div className="author-avatar">
                            <i className="fas fa-user"></i>
                        </div>
                        <div className="author-info">
                            <span className="author-name">
                                {blog.author?.firstName && blog.author?.lastName
                                    ? `${blog.author.firstName} ${blog.author.lastName}`
                                    : blog.author?.username || 'Anonymous'}
                            </span>
                            <span className="author-role">Author</span>
                        </div>
                    </div>

                    <div className="blog-actions">
                        <div className="blog-stats">
                            <span className="stat-item">
                                <i className="fas fa-eye"></i>
                                {blog.views || 0}
                            </span>

                            <button
                                className={`like-btn ${isLiked ? 'liked' : ''}`}
                                onClick={handleLike}
                                disabled={liking}
                                title={isLiked ? 'Unlike' : 'Like'}
                            >
                                <i className={`${isLiked ? 'fas' : 'far'} fa-heart`}></i>
                                <span>{likesCount}</span>
                            </button>
                        </div>

                        <Link to={`/blog/${blog._id}`} className="read-more-btn">
                            Read More
                            <i className="fas fa-arrow-right"></i>
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};
