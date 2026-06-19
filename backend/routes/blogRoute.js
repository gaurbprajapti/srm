import express from "express";
import {
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
} from "../controllers/blog.js";

import { isAuthenticatedUser, authorizeRoles } from "../middleware/auth.js";

const router = express.Router();

// Public routes
router.route("/blogs").get(getAllBlogs);
router.route("/blogs/featured").get(getFeaturedBlogs);
router.route("/blogs/category/:category").get(getBlogsByCategory);
router.route("/blogs/author/:authorId").get(getBlogsByAuthor);
router.route("/blogs/stats").get(getBlogStats);
router.route("/blog/:id").get(getBlogDetails);

// Protected routes - User
router.route("/blogs/my").get(isAuthenticatedUser, getMyBlogs);
router.route("/blog/new").post(isAuthenticatedUser, createBlog);
router.route("/blog/:id")
    .put(isAuthenticatedUser, updateBlog)
    .delete(isAuthenticatedUser, deleteBlog);
router.route("/blog/:id/like").post(isAuthenticatedUser, toggleLikeBlog);

// Admin routes
router.route("/admin/blogs").get(isAuthenticatedUser, authorizeRoles("admin"), getAllBlogsAdmin);

export default router; 