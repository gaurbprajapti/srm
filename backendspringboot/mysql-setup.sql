-- MySQL Database Setup for Campus Nexus Spring Boot Application
-- Run this script in your MySQL server to set up the database

-- Create database
CREATE DATABASE IF NOT EXISTS campus_nexus 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE campus_nexus;

-- Create a specific user for the application (optional, you can use root)
-- CREATE USER IF NOT EXISTS 'campus_user'@'localhost' IDENTIFIED BY '123456';
-- GRANT ALL PRIVILEGES ON campus_nexus.* TO 'campus_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Show current database
SELECT DATABASE() AS current_database;

-- Show tables (will be empty initially, Spring Boot will create them)
SHOW TABLES;

-- Optional: Set timezone (Spring Boot handles this automatically)
-- SET time_zone = '+00:00';

-- Check MySQL version
SELECT VERSION() AS mysql_version;

-- Verify connection
SELECT 'MySQL Database Setup Complete!' AS status; 