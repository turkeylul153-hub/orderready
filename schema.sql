,
CREATE DATABASE IF NOT EXISTS orderready;
USE orderready;

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    production_batch_size DECIMAL(10,2),
    production_time_days DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL
);

CREATE TABLE recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity_per_10kg DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (material_id) REFERENCES materials(id)
);

CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL UNIQUE,
    current_quantity DECIMAL(10,2) NOT NULL DEFAULT 0,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials(id)
);

INSERT INTO products (name) VALUES
('Yüksek Yoğunluklu Orijinal Poşet'),
('Yüksek Yoğunluklu Kalsitli Poşet'),
('Yüksek Yoğunluklu Geri Dönüşümlü Poşet'),
('Alçak Yoğunluklu Orijinal Poşet');

INSERT INTO materials (name, type, unit) VALUES
('HDPE', 'RAW_MATERIAL', 'kg'),
('LLDPE', 'RAW_MATERIAL', 'kg'),
('LDPE', 'RAW_MATERIAL', 'kg'),
('Kalsit (Mermer Tozu)', 'RAW_MATERIAL', 'kg'),
('Geri Dönüştürülmüş Hammadde', 'RAW_MATERIAL', 'kg'),
('İç Ambalaj', 'PACKAGING', 'adet'),
('Dış Ambalaj', 'PACKAGING', 'adet');

INSERT INTO recipes (product_id, material_id, quantity_per_10kg) VALUES
(1, 1, 8.5), (1, 2, 1.5), (1, 6, 10), (1, 7, 1),
(2, 4, 5), (2, 2, 1), (2, 1, 4), (2, 6, 10), (2, 7, 1),
(3, 5, 4), (3, 1, 5), (3, 2, 1), (3, 6, 10), (3, 7, 1),
(4, 2, 5), (4, 3, 5), (4, 6, 10), (4, 7, 1);

CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL UNIQUE,
    current_quantity DECIMAL(10,2) NOT NULL DEFAULT 0,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials(id)
);

INSERT INTO inventory (material_id, current_quantity) VALUES
(1, 500),
(2, 150),
(3, 200),
(4, 300),
(5, 80),
(6, 5000),
(7, 400);

CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255)
);

CREATE TABLE supplier_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    lead_time_batch_size DECIMAL(10,2) NOT NULL,
    lead_time_days DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (material_id) REFERENCES materials(id)
);

INSERT INTO suppliers (name, contact_email) VALUES
('Petrokim Kimya A.Ş.', 'siparis@petrokim.com'),
('Anadolu Plastik Hammadde', 'tedarik@anadoluplastik.com'),
('Mermer Tozu San. Tic.', 'info@mermertozu.com');

INSERT INTO supplier_materials (supplier_id, material_id, lead_time_batch_size, lead_time_days) VALUES
(1, 1, 10, 2),
(1, 2, 10, 2),
(2, 3, 10, 3),
(2, 5, 10, 4),
(3, 4, 10, 1);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    supplier_id BIGINT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

INSERT INTO users (username, password, role) VALUES
('depo1', '$2a$10$/0PNZer21KQ3i0Mpevh9M.5PjJ3n2aWwA5kexjkGOBUZoUL8MSSnW', 'WAREHOUSE'),
('planlamaci1', '$2a$10$ortvqcumyvYHhqa4QNFK.eqrLYhUoEl3C461XId3XxiKBRxIGkkJy', 'PLANNER');

INSERT INTO users (username, password, role, supplier_id) VALUES
('petrokim_user', '$2a$10$IvvSLGAGiA6MONsjEmjB/.ORjzsb7haZD7EC0wOrcN3spdfInjIwa', 'SUPPLIER', 1);

-- Tedarikçi lead time değerleri güncellendi (daha gerçekçi parti büyüklükleri)
UPDATE supplier_materials SET lead_time_batch_size = 500, lead_time_days = 3 WHERE supplier_id = 1 AND material_id = 1;
UPDATE supplier_materials SET lead_time_batch_size = 500, lead_time_days = 3 WHERE supplier_id = 1 AND material_id = 2;
UPDATE supplier_materials SET lead_time_batch_size = 300, lead_time_days = 4 WHERE supplier_id = 2 AND material_id = 3;
UPDATE supplier_materials SET lead_time_batch_size = 300, lead_time_days = 5 WHERE supplier_id = 2 AND material_id = 5;
UPDATE supplier_materials SET lead_time_batch_size = 400, lead_time_days = 2 WHERE supplier_id = 3 AND material_id = 4;

-- Tedarikçi hazır stok miktarları daha gerçekçi değerlerle güncellendi
UPDATE supplier_materials SET available_quantity = 800 WHERE supplier_id = 1 AND material_id = 1;
UPDATE supplier_materials SET available_quantity = 600 WHERE supplier_id = 1 AND material_id = 2;
UPDATE supplier_materials SET available_quantity = 500 WHERE supplier_id = 2 AND material_id = 3;
UPDATE supplier_materials SET available_quantity = 400 WHERE supplier_id = 2 AND material_id = 5;
UPDATE supplier_materials SET available_quantity = 600 WHERE supplier_id = 3 AND material_id = 4;

-- Tedarikçi/lead-time hesaplama kısmı kaldırıldı, yerine sipariş ve transaction bazlı stok sistemi geldi
DROP TABLE IF EXISTS supplier_materials;

ALTER TABLE users DROP FOREIGN KEY users_ibfk_1;
ALTER TABLE users DROP COLUMN supplier_id;

DROP TABLE IF EXISTS suppliers;

DELETE FROM users WHERE username = 'petrokim_user';

CREATE TABLE warehouses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO warehouses (name) VALUES ('Ana Fabrika Deposu');

ALTER TABLE materials ADD COLUMN low_stock_threshold DECIMAL(10,2) DEFAULT 0;

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity_kg DECIMAL(10,2) NOT NULL,
    customer_name VARCHAR(255),
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE material_stock_tx (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity_change DECIMAL(10,2) NOT NULL,
    balance_after DECIMAL(10,2) NOT NULL,
    type VARCHAR(30) NOT NULL,
    order_id BIGINT,
    performed_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (performed_by) REFERENCES users(id)
);

CREATE TABLE product_stock_tx (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity_change DECIMAL(10,2) NOT NULL,
    balance_after DECIMAL(10,2) NOT NULL,
    type VARCHAR(30) NOT NULL,
    order_id BIGINT,
    performed_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (performed_by) REFERENCES users(id)
);

-- Mevcut fabrika stoklarını yeni transaction tablosuna başlangıç kaydı olarak taşı
INSERT INTO material_stock_tx (material_id, warehouse_id, quantity_change, balance_after, type, created_at)
SELECT material_id, 1, current_quantity, current_quantity, 'INITIAL', last_updated_at
FROM inventory;

INSERT INTO users (username, password, role) VALUES
('satis1', '$2a$10$RUjU7sOMoaz/EgwGxD97s.kcv5hIDezRj4uSIg8ct7m9fvpH0V5Xu', 'SALES');

-- Tedarikçi sistemi basitleştirilmiş haliyle geri eklendi (lead-time hesaplaması olmadan)
CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    contact_email VARCHAR(150)
);

CREATE TABLE supplier_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (material_id) REFERENCES materials(id)
);

INSERT INTO suppliers (name, contact_email) VALUES
('Petrokim Kimya A.Ş.', 'siparis@petrokim.com'),
('Anadolu Plastik Hammadde', 'tedarik@anadoluplastik.com'),
('Mermer Tozu San. Tic.', 'info@mermertozu.com');

INSERT INTO supplier_materials (supplier_id, material_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 5),
(3, 4);

ALTER TABLE orders ADD COLUMN notes TEXT;

-- malzemelere açıklama alanı eklendi
ALTER TABLE materials ADD COLUMN description VARCHAR(300);
