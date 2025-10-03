CREATE TABLE IF NOT EXISTS restaurants (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    cuisine VARCHAR(255) NOT NULL,
    rating DECIMAL(2, 1) CHECK (rating >= 0 AND rating <= 5) DEFAULT 0.0,
    opening_hours VARCHAR(255) NOT NULL,
    is_open BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS menu_items (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(512),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);