-- Insert the test user address
INSERT INTO addresses (street, city, state, zip_code)
VALUES ('Owner Test Street', 'Test City', 'TS', '12345-678');

-- Insert test user
INSERT INTO users (name, username, email, password, enabled, created_at, updated_at, address_id)
VALUES ('owner','testowner', 'owner@test.com', '$2a$10$ScXSXlH8BHun5ej50hABZulcZRAZg7el0xwMPX6CeJ2llFkqnRKuC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        (SELECT id FROM addresses WHERE street = 'Owner Test Street' LIMIT 1));

-- Assign an OWNER role to the test user
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'testowner'),
        (SELECT id FROM roles WHERE name = 'OWNER')
       );

-- Insert admin user
INSERT INTO users (name, username, email, password, enabled, created_at, updated_at)
VALUES ('admin','admin', 'admin@test.com', '$2a$10$ScXSXlH8BHun5ej50hABZulcZRAZg7el0xwMPX6CeJ2llFkqnRKuC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign an ADMIN role to the test user
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'admin'),
        (SELECT id FROM roles WHERE name = 'ADMIN')
       );

-- Insert normal user
INSERT INTO users (name, username, email, password, enabled, created_at, updated_at)
VALUES ('testnormaluser','testnormaluser', 'normaluser@test.com', '$2a$10$ScXSXlH8BHun5ej50hABZulcZRAZg7el0xwMPX6CeJ2llFkqnRKuC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign a USER role to the test user
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'testnormaluser'),
        (SELECT id FROM roles WHERE name = 'USER')
       );

-- Insert the test restaurant address
INSERT INTO addresses (street, city, state, zip_code)
VALUES ('Restaurant Test Street', 'Test City', 'TS', '12345-678');

-- Insert test restaurant
INSERT INTO restaurants (id, owner_id, address_id, name, cuisine, rating, opening_hours, is_open, created_at, updated_at)
VALUES (1, (SELECT id FROM users WHERE email = 'owner@test.com' LIMIT 1),
        (SELECT id FROM addresses WHERE street = 'Restaurant Test Street' LIMIT 1),
    'Test Restaurant', 'Italian', 4.5, '9:00-22:00', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test menu items
INSERT INTO menu_items (id, restaurant_id, name, description, image_url, price, is_available, created_at, updated_at)
VALUES (1, (SELECT id FROM restaurants WHERE owner_id = (SELECT id FROM users WHERE name = 'owner')),'Test Pizza', 'Delicious test pizza', 'http://example.com/pizza.jpg', 25.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
