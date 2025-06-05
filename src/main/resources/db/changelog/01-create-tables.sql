-- Tabla de categorías
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- Tabla de subcategorías
CREATE TABLE IF NOT EXISTS category_subcategory (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES category(id)
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    stock INTEGER,
    status VARCHAR(50),
    brand VARCHAR(100),
    category_id BIGINT NOT NULL REFERENCES category(id),
    subcategory_id BIGINT REFERENCES category_subcategory(id),
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)
);

-- Tabla de imágenes de producto
CREATE TABLE IF NOT EXISTS product_image (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    url_imagen TEXT,
    priority INTEGER
);

-- Tabla de atributos de producto
CREATE TABLE IF NOT EXISTS product_attribute (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    nombre_atributo VARCHAR(255),
    valor_atributo VARCHAR(255)
);

-- Tabla de precios de producto
CREATE TABLE IF NOT EXISTS product_price (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    price NUMERIC(15,2),
    price_currency VARCHAR(10),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_active BOOLEAN,
    fecha_creacion TIMESTAMP
);

-- Tabla de auditoría de producto
CREATE TABLE IF NOT EXISTS product_audit (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT REFERENCES product(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES category(id) ON DELETE CASCADE,
    subcategory_id BIGINT REFERENCES category_subcategory(id) ON DELETE CASCADE,
    action VARCHAR(100),
    username VARCHAR(100),
    entity VARCHAR(100),
    details TEXT,
    ip_address VARCHAR(50),
    date TIMESTAMP
);