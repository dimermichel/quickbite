CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    address_id BIGINT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Insert default roles if they do not exist
INSERT INTO roles (name) VALUES ('USER') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('OWNER') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;

-- Create a join table for many-to-many relationship between users and roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insert default admin user if not exists
INSERT INTO users (name, username, password, email, enabled)
VALUES ('Admin User', 'admin', '$2a$10$ScXSXlH8BHun5ej50hABZulcZRAZg7el0xwMPX6CeJ2llFkqnRKuC',
        'admin@email.com', TRUE) ON CONFLICT (username) DO NOTHING;

-- Assign an ADMIN role to the default admin user
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'admin'),
        (SELECT id FROM roles WHERE name = 'ADMIN')
) ON CONFLICT DO NOTHING;

