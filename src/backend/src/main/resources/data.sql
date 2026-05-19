-- deletion is important for backend test stability, but breaks unit tests!
-- DELETE FROM user_interests;
-- DELETE FROM users;
-- DELETE FROM topics;

-- topics
CREATE TABLE IF NOT EXISTS topics (
  id UUID PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL
);

-- topic examples
INSERT INTO topics (id, name)
VALUES
  (gen_random_uuid(), 'Politik'),
  (gen_random_uuid(), 'Spazieren'),
  (gen_random_uuid(), 'Wandern'),
  (gen_random_uuid(), 'Kunst'),
  (gen_random_uuid(), 'Hunde'),
  (gen_random_uuid(), 'Katzen'),
  (gen_random_uuid(), 'Fahrrad fahren'),
  (gen_random_uuid(), 'Kochen'),
  (gen_random_uuid(), 'Kaffee trinken'),
  (gen_random_uuid(), 'Lesen'),
  (gen_random_uuid(), 'Programmieren'),
  (gen_random_uuid(), 'DIY handwerken'),
  (gen_random_uuid(), 'Gärtnern'),
  (gen_random_uuid(), 'Orchester spielen'),
  (gen_random_uuid(), 'Bandmusik spielen')
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

-- user_interests
CREATE TABLE IF NOT EXISTS user_interests (
  user_id UUID NOT NULL,
  topic_id UUID NOT NULL
);

-- user examples
INSERT INTO users (id, name, password_hash, email, social_battery, currency, experience_points, last_login)
VALUES
  (gen_random_uuid(), 'Alice Johnson', '$2a$10$hash1', 'alice@example.com', 80, 0, 0, NOW()),
  (gen_random_uuid(), 'Bob Smith', '$2a$10$hash2', 'bob@example.com', 60, 0, 0, NOW()),
  (gen_random_uuid(), 'Carol White', '$2a$10$hash3', 'carol@example.com', 90, 750, 2400, NOW()),
  (gen_random_uuid(), 'David Brown', '$2a$10$hash4', 'david@example.com', 40, 100, 400, NOW()),
  (gen_random_uuid(), 'Eva Martinez', '$2a$10$hash5', 'eva@example.com', 70, 600, 1800, NOW()),
  (gen_random_uuid(), 'Frank Lee', '$2a$10$hash6', 'frank@example.com', 50, 200, 600, NOW()),
  (gen_random_uuid(), 'Grace Kim', '$2a$10$hash7', 'grace@example.com', 85, 900, 3200, NOW()),
  (gen_random_uuid(), 'Henry Wilson', '$2a$10$hash8', 'henry@example.com', 30, 150, 300, NOW()),
  (gen_random_uuid(), 'Isla Davis', '$2a$10$hash9', 'isla@example.com', 75, 450, 1500, NOW()),
  (gen_random_uuid(), 'Jack Taylor', '$2a$10$hash10', 'jack@example.com', 65, 350, 1000, NOW())
ON CONFLICT DO NOTHING;

-- user_interests examples
INSERT INTO user_interests (user_id, topic_id)
SELECT u.id, t.id
FROM users u
CROSS JOIN topics t
WHERE 
  (u.email = 'alice@example.com' AND t.name IN ('Politik', 'Lesen', 'Kunst')) OR
  (u.email = 'bob@example.com' AND t.name IN ('Spazieren', 'Hunde', 'Kochen')) OR
  (u.email = 'carol@example.com' AND t.name IN ('Wandern', 'Fahrrad fahren', 'Programmieren')) OR
  (u.email = 'david@example.com' AND t.name IN ('Kaffee trinken', 'Kochen', 'Katzen')) OR
  (u.email = 'eva@example.com' AND t.name IN ('Kunst', 'DIY handwerken', 'Gärtnern')) OR
  (u.email = 'frank@example.com' AND t.name IN ('Bandmusik spielen', 'Orchester spielen')) OR
  (u.email = 'grace@example.com' AND t.name IN ('Kochen', 'Kunst', 'Programmieren')) OR
  (u.email = 'henry@example.com' AND t.name IN ('Hunde', 'Katzen', 'Spazieren'))
ON CONFLICT DO NOTHING;