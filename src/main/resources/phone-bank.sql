CREATE TABLE IF NOT EXISTS phone_entity (
    id VARCHAR(255) PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    model_name VARCHAR(255) NOT NULL,
    model_code VARCHAR(255) NOT NULL,
    phone_count INT NOT NULL,
    available_count INT NOT NULL,
    booked_by VARCHAR(255),
    booking_time TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE (brand_name, model_code)
);