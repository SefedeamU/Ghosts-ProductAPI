TRUNCATE TABLE product_audit RESTART IDENTITY CASCADE;
TRUNCATE TABLE product RESTART IDENTITY CASCADE;
TRUNCATE TABLE subcategory RESTART IDENTITY CASCADE;
TRUNCATE TABLE category RESTART IDENTITY CASCADE;

INSERT INTO category (id, name, description) VALUES (1, 'TestCategory', 'Test category');
INSERT INTO subcategory (id, name, description, category_id) VALUES (1, 'TestSubCategory', 'Test subcategory', 1);
INSERT INTO product (id, name, brand, description, stock, status, category_id, subcategory_id, creado_por)
VALUES (1, 'TestProduct-Public', 'TestBrand', 'A test product', 10, 'ACTIVE', 1, 1, 'adminUser');