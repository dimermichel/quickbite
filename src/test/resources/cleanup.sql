-- Delete in correct order to respect foreign key constraints
DELETE FROM menu_items;
DELETE FROM restaurants;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM addresses;

-- Reset all sequences to avoid conflicts
SELECT setval('addresses_id_seq', (SELECT COALESCE(MAX(id), 1) FROM addresses));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('restaurants_id_seq', (SELECT COALESCE(MAX(id), 1) FROM restaurants));
SELECT setval('menu_items_id_seq', (SELECT COALESCE(MAX(id), 1) FROM menu_items));