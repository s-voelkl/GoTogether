-- deletion is important for backend test stability, but breaks unit tests!
-- DELETE FROM challenge_users;
-- DELETE FROM challenge_topics;
-- DELETE FROM challenges;
-- DELETE FROM user_interests;
-- DELETE FROM users;
-- DELETE FROM companies;
-- DELETE FROM topics;

-- =====================================================================
-- Topics
-- =====================================================================
INSERT INTO topics (id, name)
VALUES
  (gen_random_uuid(), 'Politik'),
  (gen_random_uuid(), 'Spazieren'),
  (gen_random_uuid(), 'Wandern'),
  (gen_random_uuid(), 'Natur'),
  (gen_random_uuid(), 'Stadt & Geschichte'),
  (gen_random_uuid(), 'Kunst & Kreatives'),
  (gen_random_uuid(), 'Musik'),
  (gen_random_uuid(), 'Lesen'),
  (gen_random_uuid(), 'Programmieren'),
  (gen_random_uuid(), 'Handwerk & Garten'),
  (gen_random_uuid(), 'Essen & Kochen'),
  (gen_random_uuid(), 'Café'),
  (gen_random_uuid(), 'Sport'),
  (gen_random_uuid(), 'Tiere'),
  (gen_random_uuid(), 'Spiele'),
  (gen_random_uuid(), 'Abenteuer'),
  (gen_random_uuid(), 'Gemeinschaft'),
  (gen_random_uuid(), 'Unterhaltung'),
  (gen_random_uuid(), 'Entspannung'),
  (gen_random_uuid(), 'Drinnen')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Users
-- =====================================================================
INSERT INTO users (id, name, password, email, social_battery, currency, experience_points, last_login, setting)
VALUES
  (gen_random_uuid(), 'Alice Johnson', '$2a$10$hash1',  'alice@example.com',  80,   0,    0, NOW(), 'default'),
  (gen_random_uuid(), 'Bob Smith',     '$2a$10$hash2',  'bob@example.com',    60,   0,    0, NOW(), 'default'),
  (gen_random_uuid(), 'Carol White',   '$2a$10$hash3',  'carol@example.com',  90, 750, 2400, NOW(), 'default'),
  (gen_random_uuid(), 'David Brown',   '$2a$10$hash4',  'david@example.com',  40, 100,  400, NOW(), 'default'),
  (gen_random_uuid(), 'Eva Martinez',  '$2a$10$hash5',  'eva@example.com',    70, 600, 1800, NOW(), 'default'),
  (gen_random_uuid(), 'Frank Lee',     '$2a$10$hash6',  'frank@example.com',  50, 200,  600, NOW(), 'default'),
  (gen_random_uuid(), 'Grace Kim',     '$2a$10$hash7',  'grace@example.com',  85, 900, 3200, NOW(), 'default'),
  (gen_random_uuid(), 'Henry Wilson',  '$2a$10$hash8',  'henry@example.com',  30, 150,  300, NOW(), 'default'),
  (gen_random_uuid(), 'Isla Davis',    '$2a$10$hash9',  'isla@example.com',   75, 450, 1500, NOW(), 'default'),
  (gen_random_uuid(), 'Jack Taylor',   '$2a$10$hash10', 'jack@example.com',   65, 350, 1000, NOW(), 'default')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- User interests
-- =====================================================================
INSERT INTO user_interests (user_id, topic_id)
SELECT u.id, t.id
FROM users u
CROSS JOIN topics t
WHERE
  (u.email = 'alice@example.com' AND t.name IN ('Politik', 'Lesen', 'Kunst & Kreatives')) OR
  (u.email = 'bob@example.com'   AND t.name IN ('Spazieren', 'Tiere', 'Essen & Kochen')) OR
  (u.email = 'carol@example.com' AND t.name IN ('Wandern', 'Sport', 'Programmieren')) OR
  (u.email = 'david@example.com' AND t.name IN ('Café', 'Essen & Kochen', 'Tiere')) OR
  (u.email = 'eva@example.com'   AND t.name IN ('Kunst & Kreatives', 'Handwerk & Garten')) OR
  (u.email = 'frank@example.com' AND t.name IN ('Musik')) OR
  (u.email = 'grace@example.com' AND t.name IN ('Essen & Kochen', 'Kunst & Kreatives', 'Programmieren')) OR
  (u.email = 'henry@example.com' AND t.name IN ('Tiere', 'Spazieren'))
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Companies
-- =====================================================================
INSERT INTO companies (id, name, password, email, currency, street, house_number,
  zip_code, city, latitude, longitude)
VALUES
  -- Original demo companies
  (gen_random_uuid(), 'GoTogether', '$2a$10$hashCompany1', 'contact@gotogether.example.com',
   1000, 'Musterstraße',  '42', '12345', 'Musterstadt',   52.5200, 13.4000),
  (gen_random_uuid(), 'TechCorp',   '$2a$10$hashCompany2', 'contact@techcorp.example.com',
   2000, 'Technostraße',  '24', '54321', 'Technostadt',   48.8500,  2.3500),

  -- Hosts referenced by Amberg dataset challenges
  (gen_random_uuid(), 'GoTogether Community',  '$2a$10$hashCommunity',
   'community@gotogether-community.example.com',
   3200, 'Marktplatz',     '1',  '92224', 'Amberg', 49.4423, 11.8621),
  (gen_random_uuid(), 'Cafe Central',          '$2a$10$hashCafeCentral',
   'cafe-central@example.com',
   1500, 'Georgenstraße', '12',  '92224', 'Amberg', 49.4431, 11.8615),
  (gen_random_uuid(), 'Cafe Zentral',          '$2a$10$hashCafeZentral',
   'cafe-zentral@example.com',
   1800, 'Rathausstraße',  '5',  '92224', 'Amberg', 49.4440, 11.8601),
  (gen_random_uuid(), 'Jugendzentrum Klärwerk', '$2a$10$hashKlaerwerk',
   'klaerwerk@example.com',
    500, 'Vilsstraße',    '21',  '92224', 'Amberg', 49.4404, 11.8673),

  -- Additional regional companies (no challenges hosted yet)
  (gen_random_uuid(), 'Weidener Sportclub',    '$2a$10$hashWeidenSport',
   'sport@weidener-sportclub.example.com',
   4200, 'Sportplatzweg',  '3',  '92637', 'Weiden in der Oberpfalz',     49.6770, 12.1561),
  (gen_random_uuid(), 'Sulzbach Kulturkreis',  '$2a$10$hashSulzbach',
   'kultur@sulzbach-kulturkreis.example.com',
   2700, 'Luitpoldplatz',  '7',  '92237', 'Sulzbach-Rosenberg',          49.5012, 11.7474),
  (gen_random_uuid(), 'Hirschau Events',       '$2a$10$hashHirschau',
   'events@hirschau-events.example.com',
   1200, 'Hauptstraße',   '14',  '92242', 'Hirschau',                    49.5435, 11.9457),
  (gen_random_uuid(), 'Neustadt Treff',        '$2a$10$hashNeustadt',
   'treff@neustadt-waldnaab.example.com',
   6800, 'Stadtplatz',     '9',  '92660', 'Neustadt an der Waldnaab',    49.7330, 12.1797),
  (gen_random_uuid(), 'Altenstadt Community',  '$2a$10$hashAltenstadt',
   'community@altenstadt-waldnaab.example.com',
    900, 'Bahnhofstraße',  '4',  '92665', 'Altenstadt an der Waldnaab',  49.7187, 12.1701),
  (gen_random_uuid(), 'Oberpfalz Outdoor',     '$2a$10$hashOutdoor',
   'outdoor@oberpfalz.example.com',
   9500, 'Maximilianstraße', '40', '92637', 'Weiden in der Oberpfalz',   49.6770, 12.1561)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Challenges
-- =====================================================================
INSERT INTO challenges (id, title, description, is_archived, start_time, latitude, longitude,
  duration_minutes, currency, experience_points, min_social_battery, verification_code,
  max_players, host_company_id)
SELECT
  gen_random_uuid(),
  ch.title,
  ch.description,
  ch.is_archived,
  ch.start_time,
  ch.latitude,
  ch.longitude,
  ch.duration_minutes,
  ch.currency,
  ch.experience_points,
  ch.min_social_battery,
  ch.verification_code,
  ch.max_players,
  c.id
FROM (
  VALUES
    -- Original demo challenges
    ('Gemeinsam Wandern im Park',  'Lockere Wanderung durch den Stadtpark.',
     false, NOW() + INTERVAL '1 day',  52.5200, 13.4050,  90,  50, 100, 20, 'K7M2Q', 10,
     'contact@gotogether.example.com'),
    ('Kaffee & Kunst',             'Gemütliches Treffen mit Galeriebesuch.',
     false, NOW() + INTERVAL '2 day',  52.5150, 13.3880,  60,  30,  50, 10, 'X4P9B',  6,
     'contact@gotogether.example.com'),
    ('Tech Meetup: Programmieren', 'Open-Space Coding Session.',
     false, NOW() + INTERVAL '3 day',  48.8520,  2.3490, 120,  80, 150, 40, 'R3T8N',  0,
     'contact@techcorp.example.com'),

    -- Amberg dataset challenges
    ('Social Raid',
     'Trefft euch am Marktplatz und löst gemeinsam drei kleine Aufgaben.',
     false, TIMESTAMP '2026-06-10 18:30:00', 49.4423, 11.8621,  45,  40, 220, 3, 'H5J1W',  8,
     'community@gotogether-community.example.com'),
    ('Coffee Rush',
     'Vier Personen treffen sich in einem Café und beantworten ein paar lockere Fragen.',
     true,  TIMESTAMP '2026-06-11 16:00:00', 49.4431, 11.8615,  30,  25, 120, 2, 'D9F2L',  4,
     'cafe-central@example.com'),
    ('Sunset Meetup',
     'Trefft euch im Park und schaut gemeinsam den Sonnenuntergang an.',
     false, TIMESTAMP '2026-06-12 20:00:00', 49.4451, 11.8592,  60,  35, 180, 2, 'S6V3Z', 12,
     'community@gotogether-community.example.com'),
    ('Night Walk',
     'Kleine Gruppe für einen ruhigen Abendspaziergang durch die Innenstadt.',
     false, TIMESTAMP '2026-06-13 21:15:00', 49.4417, 11.8635,  40,  20, 120, 1, 'A8Y4C',  7,
     'community@gotogether-community.example.com'),
    ('City Hunt',
     'Löst gemeinsam kleine Aufgaben in der Innenstadt und entdeckt neue Orte.',
     false, TIMESTAMP '2026-06-14 15:30:00', 49.4435, 11.8618,  75,  60, 260, 4, 'M2G7E', 10,
     'community@gotogether-community.example.com'),
    ('Coffee Connect',
     'Vier zufällige Personen treffen sich auf einen Kaffee und reden über einfache Einstiegsthemen.',
     false, TIMESTAMP '2026-06-15 17:00:00', 49.4440, 11.8601,  35,  25, 140, 2, 'B1U6Q',  4,
     'cafe-zentral@example.com'),
    ('Park Games',
     'Trefft euch im Park für kleine Gruppenspiele und lockere Team-Aufgaben.',
     false, TIMESTAMP '2026-06-16 18:45:00', 49.4462, 11.8587,  50,  70, 320, 5, 'N9K3T', 14,
     'community@gotogether-community.example.com'),
    ('Food Spot Tour',
     'Gemeinsam verschiedene kleine Food-Spots in der Stadt ausprobieren.',
     false, TIMESTAMP '2026-06-17 19:00:00', 49.4429, 11.8610,  90,  50, 240, 3, 'P4W8H', 10,
     'community@gotogether-community.example.com'),
    ('Photo Mission',
     'Macht gemeinsam kreative Bilder an verschiedenen Orten in der Stadt.',
     false, TIMESTAMP '2026-06-18 16:30:00', 49.4438, 11.8627,  70,  45, 200, 2, 'C7R2X',  8,
     'community@gotogether-community.example.com'),
    ('Late Night Talk',
     'Lockeres Gruppentreffen am Abend. Einfach zusammensitzen und reden.',
     false, TIMESTAMP '2026-06-19 22:00:00', 49.4408, 11.8641,  60,  30, 170, 1, 'F5L9D',  9,
     'community@gotogether-community.example.com'),
    ('River Walk',
     'Gemeinsamer Spaziergang entlang der Vils mit kleinen Gesprächsimpulsen.',
     false, TIMESTAMP '2026-06-20 18:00:00', 49.4412, 11.8650,  50,  30, 160, 2, 'T3Z6V',  9,
     'community@gotogether-community.example.com'),
    ('Castle Team Quest',
     'Trefft euch an der Burg und löst gemeinsam kleine Team-Aufgaben.',
     false, TIMESTAMP '2026-06-21 17:30:00', 49.4448, 11.8582,  65,  55, 280, 4, 'Y1A8M', 12,
     'community@gotogether-community.example.com'),
    ('Music Meetup',
     'Bringt eure Lieblingssongs mit und lernt neue Leute über Musik kennen.',
     false, TIMESTAMP '2026-06-22 19:00:00', 49.4460, 11.8594,  70,  35, 190, 2, 'Q2J7G', 10,
     'community@gotogether-community.example.com'),
    ('Street Quiz',
     'Kleine Gruppen treten gegeneinander in einem Stadt-Quiz an.',
     false, TIMESTAMP '2026-06-23 16:30:00', 49.4423, 11.8621,  45,  45, 240, 3, 'E9N4B', 14,
     'community@gotogether-community.example.com'),
    ('Morning Coffee Circle',
     'Lockeres Frühstückstreffen für alle, die morgens neue Leute kennenlernen möchten.',
     false, TIMESTAMP '2026-06-24 09:00:00', 49.4439, 11.8604,  40,  20, 110, 1, 'U6K1S',  6,
     'cafe-zentral@example.com'),
    ('Team Picnic',
     'Gemeinsames Picknick mit kleinen Gruppenspielen im Park.',
     false, TIMESTAMP '2026-06-25 18:15:00', 49.4454, 11.8589,  90,  65, 300, 3, 'W3P8R', 15,
     'community@gotogether-community.example.com'),
    ('Hidden Spots',
     'Entdeckt gemeinsam unbekannte Orte in der Altstadt.',
     false, TIMESTAMP '2026-06-26 17:45:00', 49.4433, 11.8616,  80,  50, 250, 3, 'H7C2L',  8,
     'community@gotogether-community.example.com'),
    ('Board Game Evening',
     'Spieleabend mit kleinen Teams und wechselnden Gruppen.',
     false, TIMESTAMP '2026-06-27 19:30:00', 49.4404, 11.8673, 120,  70, 320, 2, 'X4D9F', 16,
     'klaerwerk@example.com'),
    ('Sunrise Challenge',
     'Frühes Treffen mit kleiner Gruppe zum Sonnenaufgang.',
     false, TIMESTAMP '2026-06-28 05:15:00', 49.4386, 11.8724,  60,  40, 220, 1, 'V5M3T',  7,
     'community@gotogether-community.example.com'),
    ('Weekend Social Raid',
     'Große Gruppenquest am Wochenende mit mehreren Mini-Challenges in der Innenstadt.',
     false, TIMESTAMP '2026-06-29 14:00:00', 49.4428, 11.8617, 120, 120, 500, 5, 'G8Z1Q', 20,
     'community@gotogether-community.example.com')
) AS ch(title, description, is_archived, start_time, latitude, longitude, duration_minutes,
        currency, experience_points, min_social_battery, verification_code, max_players, host_email)
JOIN companies c ON c.email = ch.host_email
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Challenge topics
-- =====================================================================
INSERT INTO challenge_topics (challenge_id, topic_id)
SELECT ch.id, t.id
FROM challenges ch
CROSS JOIN topics t
WHERE
  (ch.title = 'Gemeinsam Wandern im Park'  AND t.name IN ('Wandern', 'Spazieren')) OR
  (ch.title = 'Kaffee & Kunst'             AND t.name IN ('Café', 'Kunst & Kreatives')) OR
  (ch.title = 'Tech Meetup: Programmieren' AND t.name IN ('Programmieren')) OR
  (ch.title = 'Social Raid'                AND t.name IN ('Gemeinschaft', 'Natur')) OR
  (ch.title = 'Coffee Rush'                AND t.name IN ('Café', 'Unterhaltung', 'Entspannung')) OR
  (ch.title = 'Sunset Meetup'              AND t.name IN ('Natur', 'Entspannung', 'Gemeinschaft')) OR
  (ch.title = 'Night Walk'                 AND t.name IN ('Spazieren', 'Unterhaltung', 'Entspannung')) OR
  (ch.title = 'City Hunt'                  AND t.name IN ('Abenteuer', 'Gemeinschaft', 'Stadt & Geschichte')) OR
  (ch.title = 'Coffee Connect'             AND t.name IN ('Café', 'Gemeinschaft', 'Unterhaltung')) OR
  (ch.title = 'Park Games'                 AND t.name IN ('Spiele', 'Natur', 'Gemeinschaft')) OR
  (ch.title = 'Food Spot Tour'             AND t.name IN ('Essen & Kochen', 'Abenteuer', 'Gemeinschaft')) OR
  (ch.title = 'Photo Mission'              AND t.name IN ('Kunst & Kreatives', 'Natur')) OR
  (ch.title = 'Late Night Talk'            AND t.name IN ('Entspannung', 'Unterhaltung')) OR
  (ch.title = 'River Walk'                 AND t.name IN ('Spazieren', 'Unterhaltung', 'Natur')) OR
  (ch.title = 'Castle Team Quest'          AND t.name IN ('Abenteuer', 'Stadt & Geschichte', 'Gemeinschaft')) OR
  (ch.title = 'Music Meetup'               AND t.name IN ('Musik', 'Entspannung', 'Gemeinschaft')) OR
  (ch.title = 'Street Quiz'                AND t.name IN ('Spiele', 'Gemeinschaft')) OR
  (ch.title = 'Morning Coffee Circle'      AND t.name IN ('Café', 'Entspannung', 'Unterhaltung')) OR
  (ch.title = 'Team Picnic'                AND t.name IN ('Essen & Kochen', 'Natur', 'Spiele')) OR
  (ch.title = 'Hidden Spots'               AND t.name IN ('Abenteuer', 'Stadt & Geschichte')) OR
  (ch.title = 'Board Game Evening'         AND t.name IN ('Spiele', 'Drinnen', 'Gemeinschaft')) OR
  (ch.title = 'Sunrise Challenge'          AND t.name IN ('Natur', 'Entspannung')) OR
  (ch.title = 'Weekend Social Raid'        AND t.name IN ('Abenteuer', 'Gemeinschaft'))
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Challenge attendees (0-2 users each)
-- =====================================================================
INSERT INTO challenge_users (challenge_id, user_id)
SELECT ch.id, u.id
FROM challenges ch
CROSS JOIN users u
WHERE
  (ch.title = 'Gemeinsam Wandern im Park'  AND u.email IN ('alice@example.com', 'bob@example.com')) OR
  (ch.title = 'Kaffee & Kunst'             AND u.email IN ('carol@example.com')) OR
  (ch.title = 'Tech Meetup: Programmieren' AND u.email IN ('grace@example.com', 'carol@example.com')) OR
  (ch.title = 'Social Raid'                AND u.email IN ('alice@example.com', 'bob@example.com')) OR
  (ch.title = 'Coffee Rush'                AND u.email IN ('carol@example.com', 'david@example.com')) OR
  (ch.title = 'Sunset Meetup'              AND u.email IN ('eva@example.com')) OR
  (ch.title = 'Night Walk'                 AND u.email IN ('frank@example.com', 'henry@example.com')) OR
  (ch.title = 'City Hunt'                  AND u.email IN ('grace@example.com', 'jack@example.com')) OR
  (ch.title = 'Coffee Connect'             AND u.email IN ('isla@example.com')) OR
  (ch.title = 'Park Games'                 AND u.email IN ('alice@example.com', 'grace@example.com')) OR
  (ch.title = 'Food Spot Tour'             AND u.email IN ('bob@example.com', 'eva@example.com')) OR
  (ch.title = 'Photo Mission'              AND u.email IN ('carol@example.com')) OR
  (ch.title = 'Late Night Talk'            AND u.email IN ('henry@example.com', 'jack@example.com')) OR
  (ch.title = 'River Walk'                 AND u.email IN ('alice@example.com', 'isla@example.com')) OR
  (ch.title = 'Castle Team Quest'          AND u.email IN ('david@example.com', 'frank@example.com')) OR
  (ch.title = 'Music Meetup'               AND u.email IN ('frank@example.com', 'grace@example.com')) OR
  (ch.title = 'Street Quiz'                AND u.email IN ('bob@example.com', 'jack@example.com')) OR
  (ch.title = 'Morning Coffee Circle'      AND u.email IN ('carol@example.com')) OR
  (ch.title = 'Team Picnic'                AND u.email IN ('eva@example.com', 'henry@example.com')) OR
  (ch.title = 'Hidden Spots'               AND u.email IN ('alice@example.com', 'isla@example.com')) OR
  (ch.title = 'Board Game Evening'         AND u.email IN ('grace@example.com', 'jack@example.com')) OR
  (ch.title = 'Sunrise Challenge'          AND u.email IN ('david@example.com')) OR
  (ch.title = 'Weekend Social Raid'        AND u.email IN ('bob@example.com', 'eva@example.com'))
ON CONFLICT DO NOTHING;
