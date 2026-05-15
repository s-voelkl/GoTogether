-- topics
CREATE TABLE IF NOT EXISTS topics (
  id UUID PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL
);

-- topic examples
INSERT INTO topics (id, name)
SELECT gen_random_uuid(), name
FROM unnest(ARRAY[
  'Politik', 'Spazieren', 'Wandern', 'Kunst', 'Hunde', 'Katzen',
  'Fahrrad fahren', 'Kochen', 'Kaffee trinken', 'Lesen', 'Programmieren',
  'DIY handwerken', 'Gärtnern', 'Orchester spielen', 'Bandmusik spielen'
]) AS t(name)
ON CONFLICT DO NOTHING;

-- users
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  social_battery INT NOT NULL,
  currency INT NOT NULL,
  experience_points INT NOT NULL,
  last_login TIMESTAMP
);

-- user examples
INSERT INTO users (id, name, password_hash, email, social_battery, currency, experience_points, last_login)
VALUES
  (gen_random_uuid(), 'Alice Johnson', '$2a$10$hash1', 'alice@example.com', 80, 0, 0, NOW()),
  (gen_random_uuid(), 'Bob Smith', '$2a$10$hash2', 'bob@example.com', 60, 0, 0, NOW() - INTERVAL '1 day'),
  (gen_random_uuid(), 'Carol White', '$2a$10$hash3', 'carol@example.com', 90, 750, 2400, NOW() - INTERVAL '2 days'),
  (gen_random_uuid(), 'David Brown', '$2a$10$hash4', 'david@example.com', 40, 100, 400, NOW() - INTERVAL '3 days'),
  (gen_random_uuid(), 'Eva Martinez', '$2a$10$hash5', 'eva@example.com', 70, 600, 1800, NOW() - INTERVAL '1 week'),
  (gen_random_uuid(), 'Frank Lee', '$2a$10$hash6', 'frank@example.com', 50, 200, 600, NOW() - INTERVAL '6 weeks'),
  (gen_random_uuid(), 'Grace Kim', '$2a$10$hash7', 'grace@example.com', 85, 900, 3200, NOW() - INTERVAL '1 month'),
  (gen_random_uuid(), 'Henry Wilson', '$2a$10$hash8', 'henry@example.com', 30, 150, 300, NOW() - INTERVAL '1 year'),
  (gen_random_uuid(), 'Isla Davis', '$2a$10$hash9', 'isla@example.com', 75, 450, 1500, NOW() - INTERVAL '2 years'),
  (gen_random_uuid(), 'Jack Taylor', '$2a$10$hash10', 'jack@example.com', 65, 350, 1000, NOW())
ON CONFLICT DO NOTHING;
