-- =============================================================
-- Marketplace shared schema (single database used by all services)
-- Runs automatically on first startup of the postgres container.
-- NOTE: if the volume already exists, delete it to re-run:
--   docker compose -f docker/docker-compose.yml down -v
-- =============================================================

CREATE TABLE IF NOT EXISTS customers (
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR(255),
    email     VARCHAR(255),
    password  VARCHAR(255),
    phone_no  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS products (
    id    VARCHAR(255) PRIMARY KEY,
    name  VARCHAR(255),
    price NUMERIC(16, 2),
    stock INT
);

CREATE TABLE IF NOT EXISTS orders (
    order_id         VARCHAR(255) PRIMARY KEY,
    total            NUMERIC(16, 2),
    shipping_cost    NUMERIC(16, 2),
    discount         NUMERIC(16, 2),
    sub_total        NUMERIC(16, 2),
    payment_method   VARCHAR(255),
    payment_status   VARCHAR(255),
    customer_username VARCHAR(255),
    email            VARCHAR(255),
    status           VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     VARCHAR(255),
    product_id   VARCHAR(255),
    product_name VARCHAR(255),
    quantity     INT,
    price        NUMERIC(16, 2)
);

CREATE TABLE IF NOT EXISTS discount_rates (
    id               BIGSERIAL PRIMARY KEY,
    min_total        NUMERIC(16, 2),
    discount_percent INT
);

CREATE TABLE IF NOT EXISTS shipping_rates (
    id     BIGSERIAL PRIMARY KEY,
    region VARCHAR(255),
    rate   NUMERIC(16, 2)
);

CREATE TABLE IF NOT EXISTS email_logs (
    id        BIGSERIAL PRIMARY KEY,
    recipient VARCHAR(255),
    subject   VARCHAR(255),
    body      VARCHAR(255),
    status    VARCHAR(255),
    createdAt TIMESTAMP,
    order_id  VARCHAR(255)
);

-- =============================================================
-- Seed data
-- =============================================================

-- Password semua akun seed: password123 (BCrypt)
INSERT INTO customers (username, email, password, phone_no) VALUES
    ('budi',   'budi@example.com',   '$2a$10$BMx1wC7tZH.j4Gz.R2XNY.L8X3rkBmfIo45kEKTTuwVDOjQSyJ4z2', '081234567890'),
    ('siti',   'siti@example.com',   '$2a$10$BMx1wC7tZH.j4Gz.R2XNY.L8X3rkBmfIo45kEKTTuwVDOjQSyJ4z2', '081298765432')
ON CONFLICT (username) DO NOTHING;

INSERT INTO products (id, name, price, stock) VALUES
    ('P001', 'Kaos Polos',       75000,  50),
    ('P002', 'Celana Jeans',     250000, 30),
    ('P003', 'Jaket Hoodie',     350000, 20),
    ('P004', 'Sepatu Sneakers',  450000, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO discount_rates (min_total, discount_percent) VALUES
    (0,        0),
    (200000,   5),
    (500000,   10)
ON CONFLICT (min_total) DO NOTHING;

INSERT INTO shipping_rates (region, rate) VALUES
    ('Jakarta',  10000),
    ('Bandung',  15000),
    ('Jogja',    20000),
    ('Surabaya', 25000)
ON CONFLICT (region) DO NOTHING;

-- Contoh order + item seed (total = subtotal + shipping - discount)
-- subtotal 425000, discount 5% = 21250, shipping 10000 -> total 413750
INSERT INTO orders (order_id, total, shipping_cost, discount, sub_total, payment_method, payment_status, customer_username, email, status) VALUES
    ('ORD-001', 413750, 10000, 21250, 425000, 'BANK_TRANSFER', 'PAID', 'budi', 'budi@example.com', 'PROCESSED')
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO order_items (order_id, product_id, product_name, quantity, price) VALUES
    ('ORD-001', 'P001', 'Kaos Polos',      1, 75000),
    ('ORD-001', 'P003', 'Jaket Hoodie',    1, 350000)
ON CONFLICT (id) DO NOTHING;

INSERT INTO email_logs (recipient, subject, body, status, createdAt, order_id) VALUES
    ('budi@example.com', 'Welcome to Marketplace', 'Hi budi, your account has been created successfully!', 'SENT', NOW(), NULL)
ON CONFLICT (id) DO NOTHING;
