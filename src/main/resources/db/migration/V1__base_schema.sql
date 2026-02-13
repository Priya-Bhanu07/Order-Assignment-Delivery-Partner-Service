CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

--------------------------------------------------
-- USERS (matches your User entity)
--------------------------------------------------
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,

    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    salutation VARCHAR(10),

    address TEXT,
    street_address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    pincode VARCHAR(20),

    role_id BIGINT,
    department VARCHAR(100),
    designation VARCHAR(100),
    date_of_joining TIMESTAMP,
    monthly_target NUMERIC(20,2),

    status VARCHAR(50),
    is_deactivated BOOLEAN,
    last_login TIMESTAMP,
    last_tracked_at TIMESTAMP,
    has_active_session BOOLEAN,

    application_settings JSONB,
    communication_preferences JSONB,
    metadata JSONB,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

--------------------------------------------------
-- CONTACTS (matches your Contact entity)
--------------------------------------------------
CREATE TABLE contacts (
    id BIGSERIAL PRIMARY KEY,
    contact_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

--------------------------------------------------
-- LOCATIONS (matches your Location entity)
--------------------------------------------------
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID,

    user_id BIGINT REFERENCES users(id),

    address_id VARCHAR(255),
    address TEXT,
    formatted_address TEXT,
    attention VARCHAR(255),

    street VARCHAR(255),
    street2 VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip VARCHAR(50),
    country VARCHAR(255),
    postal_code VARCHAR(50),

    latitude NUMERIC(11,8),
    longitude NUMERIC(11,8),

    location GEOGRAPHY(POINT,4326),
    coordinates GEOGRAPHY(POINT,4326),
    coordinates_mercator GEOMETRY(POINT,3857),

    device_latitude NUMERIC(11,8),
    device_longitude NUMERIC(11,8),
    device_location GEOGRAPHY(POINT,4326),

    accuracy NUMERIC(11,8),
    horizontal_accuracy NUMERIC(11,8),
    altitude NUMERIC(11,8),
    altitude_accuracy FLOAT,
    heading FLOAT,
    speed FLOAT,
    satellites SMALLINT,

    structured_address JSONB,
    address_components JSONB,
    reverse_geocode JSONB,
    routing_metadata JSONB,

    place_id VARCHAR(255),
    plus_code VARCHAR(255),
    types JSONB,

    source_type VARCHAR(255),
    device_platform VARCHAR(255),
    device_id VARCHAR(255),
    session_id VARCHAR(255),
    timezone VARCHAR(255),

    location_verified BOOLEAN,
    verification_method VARCHAR(255),
    verification_data JSONB,

    sequence_number INTEGER,
    parent_id BIGINT REFERENCES locations(id),

    status VARCHAR(50),
    captured_at TIMESTAMP,
    processed_at TIMESTAMP,
    expires_at TIMESTAMP,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

--------------------------------------------------
-- LOCATIONABLES (matches your entity)
--------------------------------------------------
CREATE TABLE locationables (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id),

    locationable_type VARCHAR(50),
    locationable_id BIGINT,

    address_id VARCHAR(255),
    purpose VARCHAR(50),
    type VARCHAR(50),
    is_primary BOOLEAN,

    context JSONB,

    valid_from TIMESTAMP,
    valid_to TIMESTAMP,

    attached_by BIGINT REFERENCES users(id),
    attached_at TIMESTAMP,
    visited_at TIMESTAMP,

    version INTEGER,

    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
