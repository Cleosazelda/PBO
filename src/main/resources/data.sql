-- File: src/main/resources/data.sql
-- Script ini akan dijalankan otomatis saat aplikasi start

-- Insert default agents (password sudah di-encode dengan BCrypt)
-- Password: admin123 -> $2a$10$...
-- Password: agent123 -> $2a$10$...

INSERT INTO agent (nama, email, telepon, alamat, password, tanggal_bergabung) 
VALUES 
('Admin Agent', 'admin@rental.com', '081234567890', 'Jl. Admin No. 1', '$2a$10$N.zmdr9k7uOCQQydw/pLJOUK3w9.bGHB8GG9SgTf/TgX.VjU0a.kC', CURRENT_DATE),
('Super Agent', 'agent@rental.com', '081234567891', 'Jl. Agent No. 2', '$2a$10$8Y5JH7.dCQjQ8O9XSjVWJeK5s9Z8r7W6dQjN9FjL3mP4nR7tG2cQ.', CURRENT_DATE)
ON CONFLICT (email) DO NOTHING;

-- Credentials:
-- admin@rental.com / admin123
-- agent@rental.com / agent123