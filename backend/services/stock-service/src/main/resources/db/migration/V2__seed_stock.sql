-- ============================================================
-- Jeu de donnees de demonstration
-- ============================================================

INSERT INTO category (id, name, description) VALUES
 (1, 'Ordinateurs portables', 'Portables et ultrabooks'),
 (2, 'Peripheriques',         'Souris, claviers, casques'),
 (3, 'Ecrans',                'Moniteurs et videoprojecteurs'),
 (4, 'Stockage',              'Disques durs, SSD, cles USB');

INSERT INTO supplier (id, name, registration_number, phone, email) VALUES
 (1, 'TechDistrib SARL',  'RC-2019-0451', '+237 690 11 22 33', 'contact@techdistrib.cm'),
 (2, 'Global Hardware',   'RC-2020-1188', '+237 677 44 55 66', 'ventes@globalhardware.cm'),
 (3, 'Bureautique Plus',  'RC-2018-0092', '+237 655 77 88 99', 'info@bureautiqueplus.cm');

INSERT INTO product (id, name, description, available_quantity, price, version, category_id, supplier_id) VALUES
 (1, 'Dell Latitude 5440',   'Portable 14 pouces, i5, 16 Go',        25, 685000.00, 0, 1, 1),
 (2, 'HP ProBook 450 G10',   'Portable 15 pouces, i7, 16 Go',        12, 790000.00, 0, 1, 1),
 (3, 'Souris Logitech MX',   'Souris sans fil ergonomique',          80,  62000.00, 0, 2, 2),
 (4, 'Clavier mecanique K2', 'Clavier compact retroeclaire',         45,  78000.00, 0, 2, 2),
 (5, 'Ecran Dell 27 pouces', 'Moniteur IPS 2560x1440',               18, 215000.00, 0, 3, 1),
 (6, 'SSD Samsung 1 To',     'NVMe PCIe 4.0',                        60,  95000.00, 0, 4, 3),
 (7, 'Cle USB 128 Go',       'USB 3.2, boitier metal',              150,  12500.00, 0, 4, 3);

-- Repositionner les sequences au-dela des identifiants inseres manuellement
ALTER SEQUENCE category_seq       RESTART WITH 51;
ALTER SEQUENCE supplier_seq       RESTART WITH 51;
ALTER SEQUENCE product_seq        RESTART WITH 51;
ALTER SEQUENCE stock_movement_seq RESTART WITH 51;
