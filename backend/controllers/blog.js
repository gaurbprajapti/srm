import Blog from '../models/BlogModel.js';
import User from '../models/userModel.js';
import Features from '../utils/features.js';

// Get all blogs with filtering, searching, and pagination
const getAllBlogs = async (req, res, next) => {
    try {
        const resultPerPage = 6;
        const blogsCount = await Blog.countDocuments({ status: 'published' });

        // Initialize features for filtering, searching, and pagination
        const apiFeature = new Features(
            Blog.find({ status: 'published' }).populate('author', 'username firstName lastName'),
            req.query
        )
            .search()
            .filter()
            .pagination(resultPerPage);

        let blogs = await apiFeature.query;
        let filteredBlogsCount = blogs.length;

        res.status(200).json({
            success: true,
            blogs,
            blogsCount,
            resultPerPage,
            filteredBlogsCount,
            totalPages: Math.ceil(blogsCount / resultPerPage)
        });
    } catch (error) {
        next(error);
    }
};

// Get all blogs (including drafts) for admin/author
const getAllBlogsAdmin = async (req, res, next) => {
    try {
        const resultPerPage = 10;
        const blogsCount = await Blog.countDocuments();

        const apiFeature = new Features(
            Blog.find().populate('author', 'username firstName lastName'),
            req.query
        )
            .search()
            .filter()
            .pagination(resultPerPage);

        let blogs = await apiFeature.query;
        let filteredBlogsCount = blogs.length;

        res.status(200).json({
            success: true,
            blogs,
            blogsCount,
            resultPerPage,
            filteredBlogsCount,
            totalPages: Math.ceil(blogsCount / resultPerPage)
        });
    } catch (error) {
        next(error);
    }
};

// Get featured blogs
const getFeaturedBlogs = async (req, res, next) => {
    try {
        const blogs = await Blog.find({
            status: 'published',
            featured: true
        })
            .populate('author', 'username firstName lastName')
            .sort({ createdAt: -1 })
            .limit(5);

        res.status(200).json({
            success: true,
            blogs
        });
    } catch (error) {
        next(error);
    }
};

// Get blogs by category
const getBlogsByCategory = async (req, res, next) => {
    try {
        const { category } = req.params;
        const resultPerPage = 6;

        const query = {
            status: 'published',
            category: category
        };

        const blogsCount = await Blog.countDocuments(query);

        const apiFeature = new Features(
            Blog.find(query).populate('author', 'username firstName lastName'),
            req.query
        )
            .search()
            .pagination(resultPerPage);

        const blogs = await apiFeature.query;

        res.status(200).json({
            success: true,
            blogs,
            blogsCount,
            resultPerPage,
            totalPages: Math.ceil(blogsCount / resultPerPage),
            category
        });
    } catch (error) {
        next(error);
    }
};

// Get blogs by author
const getBlogsByAuthor = async (req, res, next) => {
    try {
        const { authorId } = req.params;
        const resultPerPage = 6;

        const query = {
            status: 'published',
            author: authorId
        };

        const blogsCount = await Blog.countDocuments(query);

        const apiFeature = new Features(
            Blog.find(query).populate('author', 'username firstName lastName'),
            req.query
        )
            .pagination(resultPerPage);

        const blogs = await apiFeature.query;

        res.status(200).json({
            success: true,
            blogs,
            blogsCount,
            resultPerPage,
            totalPages: Math.ceil(blogsCount / resultPerPage)
        });
    } catch (error) {
        next(error);
    }
};

// Get user's own blogs (including drafts)
const getMyBlogs = async (req, res, next) => {
    try {
        const userId = req.user.id;
        const resultPerPage = 10;

        const blogsCount = await Blog.countDocuments({ author: userId });

        const apiFeature = new Features(
            Blog.find({ author: userId }).populate('author', 'username firstName lastName'),
            req.query
        )
            .search()
            .filter()
            .pagination(resultPerPage);

        const blogs = await apiFeature.query;

        res.status(200).json({
            success: true,
            blogs,
            blogsCount,
            resultPerPage,
            totalPages: Math.ceil(blogsCount / resultPerPage)
        });
    } catch (error) {
        next(error);
    }
};

// Get single blog details
const getBlogDetails = async (req, res, next) => {
    try {
        const blog = await Blog.findById(req.params.id)
            .populate('author', 'username firstName lastName email')
            .populate('likes', 'username firstName lastName');

        if (!blog) {
            return res.status(404).json({
                success: false,
                message: "Blog not found"
            });
        }

        // Check if blog is published or user is author/admin
        if (blog.status !== 'published' &&
            (!req.user || (req.user.id !== blog.author._id.toString() && !req.user.isAdmin))) {
            return res.status(403).json({
                success: false,
                message: "Access denied"
            });
        }

        // Increment views only if not the author viewing their own blog
        if (!req.user || req.user.id !== blog.author._id.toString()) {
            blog.views += 1;
            await blog.save();
        }

        res.status(200).json({
            success: true,
            blog
        });
    } catch (error) {
        next(error);
    }
};

// Create new blog
const createBlog = async (req, res, next) => {
    try {
        const { title, summary, content, category, tags, image, status, featured } = req.body;

        // Only admin can set featured
        const blogData = {
            title,
            summary,
            content,
            category,
            tags: tags || [],
            image,
            status: status || 'draft',
            author: req.user.id
        };

        if (req.user.isAdmin && featured !== undefined) {
            blogData.featured = featured;
        }

        const blog = await Blog.create(blogData);
        const populatedBlog = await Blog.findById(blog._id)
            .populate('author', 'username firstName lastName');

        res.status(201).json({
            success: true,
            message: "Blog created successfully",
            blog: populatedBlog
        });
    } catch (error) {
        next(error);
    }
};

// Update blog
const updateBlog = async (req, res, next) => {
    try {
        let blog = await Blog.findById(req.params.id);

        if (!blog) {
            return res.status(404).json({
                success: false,
                message: "Blog not found"
            });
        }

        // Check if user is author or admin
        if (blog.author.toString() !== req.user.id && !req.user.isAdmin) {
            return res.status(403).json({
                success: false,
                message: "Access denied. You can only edit your own blogs"
            });
        }

        const { title, summary, content, category, tags, image, status, featured } = req.body;

        const updateData = {};
        if (title !== undefined) updateData.title = title;
        if (summary !== undefined) updateData.summary = summary;
        if (content !== undefined) updateData.content = content;
        if (category !== undefined) updateData.category = category;
        if (tags !== undefined) updateData.tags = tags;
        if (image !== undefined) updateData.image = image;
        if (status !== undefined) updateData.status = status;

        // Only admin can update featured status
        if (req.user.isAdmin && featured !== undefined) {
            updateData.featured = featured;
        }

        blog = await Blog.findByIdAndUpdate(
            req.params.id,
            updateData,
            { new: true, runValidators: true }
        ).populate('author', 'username firstName lastName');

        res.status(200).json({
            success: true,
            message: "Blog updated successfully",
            blog
        });
    } catch (error) {
        next(error);
    }
};

// Delete blog
const deleteBlog = async (req, res, next) => {
    try {
        const blog = await Blog.findById(req.params.id);

        if (!blog) {
            return res.status(404).json({
                success: false,
                message: "Blog not found"
            });
        }

        // Check if user is author or admin
        if (blog.author.toString() !== req.user.id && !req.user.isAdmin) {
            return res.status(403).json({
                success: false,
                message: "Access denied. You can only delete your own blogs"
            });
        }

        await Blog.findByIdAndDelete(req.params.id);

        res.status(200).json({
            success: true,
            message: "Blog deleted successfully"
        });
    } catch (error) {
        next(error);
    }
};

// Like/Unlike blog
const toggleLikeBlog = async (req, res, next) => {
    try {
        const blog = await Blog.findById(req.params.id);

        if (!blog) {
            return res.status(404).json({
                success: false,
                message: "Blog not found"
            });
        }

        const userId = req.user.id;
        const isLiked = blog.likes.includes(userId);

        if (isLiked) {
            // Unlike
            blog.likes = blog.likes.filter(id => id.toString() !== userId);
        } else {
            // Like
            blog.likes.push(userId);
        }

        await blog.save();

        res.status(200).json({
            success: true,
            message: isLiked ? "Blog unliked successfully" : "Blog liked successfully",
            isLiked: !isLiked,
            likesCount: blog.likes.length
        });
    } catch (error) {
        next(error);
    }
};

// Get blog statistics
const getBlogStats = async (req, res, next) => {
    try {
        const totalBlogs = await Blog.countDocuments({ status: 'published' });
        const totalDrafts = await Blog.countDocuments({ status: 'draft' });
        const totalViews = await Blog.aggregate([
            { $match: { status: 'published' } },
            { $group: { _id: null, totalViews: { $sum: '$views' } } }
        ]);

        const categoryStats = await Blog.aggregate([
            { $match: { status: 'published' } },
            { $group: { _id: '$category', count: { $sum: 1 } } },
            { $sort: { count: -1 } }
        ]);

        const popularBlogs = await Blog.find({ status: 'published' })
            .populate('author', 'username firstName lastName')
            .sort({ views: -1, likes: -1 })
            .limit(5)
            .select('title views likes author createdAt');

        res.status(200).json({
            success: true,
            stats: {
                totalBlogs,
                totalDrafts,
                totalViews: totalViews[0]?.totalViews || 0,
                categoryStats,
                popularBlogs
            }
        });
    } catch (error) {
        next(error);
    }
};

export {
    getAllBlogs,
    getAllBlogsAdmin,
    getFeaturedBlogs,
    getBlogsByCategory,
    getBlogsByAuthor,
    getMyBlogs,
    getBlogDetails,
    createBlog,
    updateBlog,
    deleteBlog,
    toggleLikeBlog,
    getBlogStats
}; 