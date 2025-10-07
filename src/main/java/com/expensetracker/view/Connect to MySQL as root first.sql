-- Connect to MySQL as root first
mysql -u root -p

-- Create the database and grant permissions
CREATE DATABASE expense_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON expense_tracker.* TO 'asus'@'localhost';
FLUSH PRIVILEGES;

-- Use the database and create tables from schema.sql
USE expense_tracker;
-- Then paste the contents of database/schema.sql here