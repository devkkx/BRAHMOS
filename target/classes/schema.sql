-- BrahMos Bhawan Management System Database Schema (MySQL 8.0)

CREATE DATABASE IF NOT EXISTS brahmos_bhawan;
USE brahmos_bhawan;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_STUDENT',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Food Preferences Table
CREATE TABLE IF NOT EXISTS food_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    day VARCHAR(20) NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    food_preference VARCHAR(20) NOT NULL,
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    locked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_date_meal (user_id, date, meal_type)
);

-- 3. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
