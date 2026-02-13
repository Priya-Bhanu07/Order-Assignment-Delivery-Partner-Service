--------------------------------------------------
-- ORDERS (matches your Order entity)
--------------------------------------------------
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,

    contact_id BIGINT NOT NULL REFERENCES contacts(id),
    location_id BIGINT REFERENCES locations(id),
    assigned_partner_id BIGINT REFERENCES users(id),

    delivery_address TEXT,

    delivery_latitude NUMERIC(11,8),
    delivery_longitude NUMERIC(11,8),
    delivery_location GEOGRAPHY(POINT,4326),

    order_items JSONB NOT NULL,
    description TEXT,
    priority VARCHAR(50),

    estimated_weight NUMERIC(10,2),
    estimated_volume NUMERIC(10,2),

    status VARCHAR(50),
    assigned_at TIMESTAMP,
    picked_at TIMESTAMP,
    dispatched_at TIMESTAMP,
    delivered_at TIMESTAMP,

    distance_km NUMERIC(10,2),
    estimated_delivery_minutes INTEGER,

    order_value NUMERIC(15,2),
    delivery_charge NUMERIC(10,2),

    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

--------------------------------------------------
-- DELIVERY PARTNERS (matches your entity)
--------------------------------------------------
CREATE TABLE delivery_partners (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id),

    vehicle_type VARCHAR(50),
    vehicle_number VARCHAR(50),
    vehicle_registration JSONB,

    vehicle_capacity_items INTEGER,
    vehicle_capacity_weight NUMERIC(10,2),
    vehicle_capacity_volume NUMERIC(10,2),

    max_orders_per_day INTEGER,
    current_active_orders INTEGER,

    service_zones JSONB,
    warehouse_location_id BIGINT REFERENCES locations(id),

    availability_status VARCHAR(50),
    last_status_update TIMESTAMP,
    on_leave_until TIMESTAMP,
    leave_reason VARCHAR(255),

    current_latitude NUMERIC(11,8),
    current_longitude NUMERIC(11,8),
    current_location GEOGRAPHY(POINT,4326),
    location_updated_at TIMESTAMP,

    total_deliveries INTEGER,
    successful_deliveries INTEGER,
    failed_deliveries INTEGER,
    average_delivery_time_minutes INTEGER,
    rating NUMERIC(3,2),

    metadata JSONB,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

--------------------------------------------------
-- SERVICE ZONES (matches your entity)
--------------------------------------------------
CREATE TABLE service_zones (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,

    zone_name VARCHAR(255),
    zone_code VARCHAR(50),
    description TEXT,

    center_latitude NUMERIC(11,8),
    center_longitude NUMERIC(11,8),
    area_sqkm NUMERIC(12,4),
    estimated_population INTEGER,

    status VARCHAR(50),
    is_primary BOOLEAN,

    metadata JSONB,

    boundary GEOMETRY(POLYGON,4326),
    center_point GEOMETRY(POINT,4326),

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

--------------------------------------------------
-- PARTNER ASSIGNMENTS (matches your entity)
--------------------------------------------------
CREATE TABLE partner_assignments (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,

    order_id BIGINT NOT NULL REFERENCES orders(id),
    partner_id BIGINT NOT NULL REFERENCES delivery_partners(id),
    previous_partner_id BIGINT REFERENCES delivery_partners(id),

    status VARCHAR(50),
    assignment_reason VARCHAR(255),

    distance_to_delivery NUMERIC(10,2),
    partner_current_load INTEGER,
    assignment_algorithm_version VARCHAR(50),
    score NUMERIC(10,4),

    assigned_at TIMESTAMP,
    accepted_at TIMESTAMP,
    completed_at TIMESTAMP,
    reassigned_at TIMESTAMP,

    assigned_by BIGINT REFERENCES users(id),
    reassignment_reason TEXT,

    metadata JSONB,

    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

--------------------------------------------------
-- AVAILABILITY LOGS (matches your entity)
--------------------------------------------------
CREATE TABLE partner_availability_logs (
    id BIGSERIAL PRIMARY KEY,
    partner_id BIGINT NOT NULL REFERENCES delivery_partners(id),

    previous_status VARCHAR(50),
    new_status VARCHAR(50),
    change_reason VARCHAR(255),
    triggered_by VARCHAR(50),
    metadata JSONB,

    created_at TIMESTAMP
);

--------------------------------------------------
-- CACHE TABLES (match your entities)
--------------------------------------------------
CREATE TABLE geocoded_addresses (
   id BIGSERIAL PRIMARY KEY,
   address VARCHAR(500) UNIQUE NOT NULL,
   latitude NUMERIC(11,8) NOT NULL,
   longitude NUMERIC(11,8) NOT NULL,
   created_at TIMESTAMP
);

CREATE TABLE distance_cache (
   id BIGSERIAL PRIMARY KEY,
   origin_lat NUMERIC(11,8) NOT NULL,
   origin_lng NUMERIC(11,8) NOT NULL,
   dest_lat NUMERIC(11,8) NOT NULL,
   dest_lng NUMERIC(11,8) NOT NULL,
   distance_km NUMERIC(10,2) NOT NULL,
   created_at TIMESTAMP,
   UNIQUE (origin_lat, origin_lng, dest_lat, dest_lng)
);
