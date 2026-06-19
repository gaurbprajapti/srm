import mongoose from "mongoose";
const { Schema, model } = mongoose;

const PostSchema = new Schema(
    {
        title: {
            type: String,
            required: [true, "Title is required"],
            trim: true,
            maxlength: [200, "Title cannot exceed 200 characters"]
        },
        summary: {
            type: String,
            required: [true, "Summary is required"],
            trim: true,
            maxlength: [500, "Summary cannot exceed 500 characters"]
        },
        content: {
            type: String,
            required: [true, "Content is required"]
        },
        author: {
            type: Schema.Types.ObjectId,
            ref: "users",
            required: [true, "Author is required"]
        },
        category: {
            type: String,
            enum: ['Technology', 'Career', 'Campus Life', 'Events', 'Academics', 'Sports', 'Culture', 'Others'],
            default: 'Others'
        },
        tags: [{
            type: String,
            trim: true
        }],
        image: {
            type: String, // URL or path to the image
            default: null
        },
        likes: [{
            type: Schema.Types.ObjectId,
            ref: "users"
        }],
        views: {
            type: Number,
            default: 0
        },
        status: {
            type: String,
            enum: ['draft', 'published', 'archived'],
            default: 'draft'
        },
        featured: {
            type: Boolean,
            default: false
        }
    },
    {
        timestamps: true,
    }
);

// Index for better query performance
PostSchema.index({ title: 'text', content: 'text', summary: 'text' });
PostSchema.index({ category: 1 });
PostSchema.index({ author: 1 });
PostSchema.index({ createdAt: -1 });
PostSchema.index({ status: 1 });

export default model("Blog", PostSchema);