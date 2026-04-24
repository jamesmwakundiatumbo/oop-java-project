-- Migration to add geo location fields to issues table
ALTER TABLE issues
ADD COLUMN latitude DECIMAL(10, 8) NULL,
ADD COLUMN longitude DECIMAL(11, 8) NULL,
ADD COLUMN formatted_address TEXT NULL;

-- Add index for geo location queries
CREATE INDEX idx_issues_location ON issues(latitude, longitude);