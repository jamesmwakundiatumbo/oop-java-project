-- Demo users: Citizen, Official, Admin (load after schema.sql)
USE civictrack;

INSERT INTO users (name, email, password, phone, role)
VALUES
('Demo Citizen', 'citizen@civictrack.local', 'pass123', '000-000-0001', 'CITIZEN'),
('Demo Official', 'official@civictrack.local', 'pass123', '000-000-0002', 'OFFICIAL'),
('Demo Admin', 'admin@civictrack.local', 'pass123', '000-000-0003', 'ADMIN')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO issues (title, description, location, status, priority, reported_by, assigned_official, category_id)
SELECT
    'Broken street light near Main St',
    'Street light has been off for 2 weeks.',
    'Main Street Block A',
    'PENDING',
    'MEDIUM',
    c.user_id,
    o.user_id,
    2
FROM users c
JOIN users o ON o.email = 'official@civictrack.local'
WHERE c.email = 'citizen@civictrack.local'
  AND NOT EXISTS (
      SELECT 1 FROM issues WHERE title = 'Broken street light near Main St'
  );
