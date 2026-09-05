CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    min_safe_temp_c DOUBLE PRECISION NOT NULL,
    max_safe_temp_c DOUBLE PRECISION NOT NULL,
    shelf_life_days INTEGER NOT NULL
);

CREATE TABLE storage_units (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(200) NOT NULL,
    unit_type VARCHAR(50) NOT NULL
);

CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    origin_unit_id BIGINT NOT NULL REFERENCES storage_units(id),
    destination_unit_id BIGINT REFERENCES storage_units(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    departed_at TIMESTAMP,
    arrived_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE temperature_readings (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id),
    temperature_c DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id),
    severity VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    storage_unit_id BIGINT NOT NULL REFERENCES storage_units(id),
    quantity INTEGER NOT NULL,
    batch_number VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_temperature_readings_shipment ON temperature_readings(shipment_id);
CREATE INDEX idx_alerts_shipment ON alerts(shipment_id);
CREATE INDEX idx_alerts_resolved ON alerts(resolved);
CREATE INDEX idx_inventory_product ON inventory_items(product_id);
CREATE INDEX idx_inventory_storage_unit ON inventory_items(storage_unit_id);
