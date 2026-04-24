-- Fresh CivicTrack Database Schema with Geo Location Support
-- Drop existing database if it exists and create new one
DROP DATABASE IF EXISTS civictrack;
CREATE DATABASE civictrack;
USE civictrack;

-- Users table (base for Citizens, Officials, Admins)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CITIZEN', 'OFFICIAL', 'ADMIN') NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Departments table
CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL,
    description TEXT,
    head_official_id INT,
    FOREIGN KEY (head_official_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Issue categories
CREATE TABLE issue_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL,
    description TEXT,
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
);

-- Issues table with geo location support
CREATE TABLE issues (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(500),
    latitude DECIMAL(10, 8) NULL,
    longitude DECIMAL(11, 8) NULL,
    formatted_address TEXT NULL,
    status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'PENDING',
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    date_reported TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reported_by INT NOT NULL,
    assigned_official INT NULL,
    category_id INT NULL,
    FOREIGN KEY (reported_by) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_official) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES issue_categories(category_id) ON DELETE SET NULL
);

-- Comments table
CREATE TABLE comments (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    issue_id INT NOT NULL,
    user_id INT NOT NULL,
    comment_text TEXT NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Attachments table
CREATE TABLE attachments (
    attachment_id INT PRIMARY KEY AUTO_INCREMENT,
    issue_id INT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255),
    file_size BIGINT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    notification_type ENUM('NEW_ISSUE', 'STATUS_UPDATE', 'ASSIGNMENT', 'COMMENT') NOT NULL,
    related_issue_id INT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (related_issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_issues_status ON issues(status);
CREATE INDEX idx_issues_priority ON issues(priority);
CREATE INDEX idx_issues_reported_by ON issues(reported_by);
CREATE INDEX idx_issues_assigned_official ON issues(assigned_official);
CREATE INDEX idx_issues_date_reported ON issues(date_reported);
CREATE INDEX idx_issues_location ON issues(latitude, longitude);
CREATE INDEX idx_comments_issue_id ON comments(issue_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);

-- Insert sample data
-- Default admin user (password: admin123)
INSERT INTO users (name, email, password_hash, role) VALUES
('System Admin', 'admin@civictrack.com', 'admin123', 'ADMIN'),
('John Citizen', 'john@example.com', 'password123', 'CITIZEN'),
('Jane Official', 'jane@cityoffice.gov', 'official123', 'OFFICIAL');

-- Sample departments
INSERT INTO departments (department_name, description, head_official_id) VALUES
('Public Works', 'Infrastructure maintenance and repair', 3),
('Environmental Services', 'Waste management and environmental issues', NULL),
('Transportation', 'Roads, traffic, and public transportation', NULL);

-- Sample categories
INSERT INTO issue_categories (category_name, description, department_id) VALUES
('Road Maintenance', 'Potholes, road repairs, street lighting', 1),
('Waste Management', 'Garbage collection, illegal dumping', 2),
('Traffic Issues', 'Traffic lights, parking, road signs', 3),
('Utilities', 'Water, electricity, gas issues', 1),
('Parks & Recreation', 'Park maintenance, recreational facilities', 2);

-- Sample issues with geo location
INSERT INTO issues (title, description, location, latitude, longitude, formatted_address, status, priority, reported_by, category_id) VALUES
('Pothole on Main Street', 'Large pothole causing vehicle damage', 'Main Street, Downtown', 40.7128, -74.0060, 'Main Street, New York, NY, USA', 'PENDING', 'HIGH', 2, 1),
('Broken streetlight', 'Streetlight not working for 3 days', 'Oak Avenue near Park', 40.7589, -73.9851, 'Oak Avenue, New York, NY, USA', 'PENDING', 'MEDIUM', 2, 1);

COMMIT;

-- Display success message
SELECT 'Fresh CivicTrack database created successfully with geo location support!' as Status;