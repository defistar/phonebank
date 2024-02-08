CREATE TABLE IF NOT EXISTS phone (
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

CREATE TABLE IF NOT EXISTS phone_booking (
    id VARCHAR(255) PRIMARY KEY,
    phone_entity_id VARCHAR(255),
    user_name VARCHAR(255),
    is_returned BOOLEAN,
    booking_time TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
);

CREATE INDEX IF NOT EXISTS idx_phonebooking_username ON phone_booking(user_name);
CREATE INDEX IF NOT EXISTS idx_phonebooking_id_isreturned ON phone_booking(id, is_returned);
CREATE INDEX IF NOT EXISTS idx_phonebooking_bookingtime ON phone_booking(booking_time);

CREATE TABLE device_info (
    id VARCHAR(255) PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    model_code VARCHAR(255) NOT NULL,
    technology TEXT,
    _2g_bands TEXT,
    _3g_bands TEXT,
    _4g_bands TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE UNIQUE INDEX idx_brand_model ON device_info (brand_name, model_code);
