-- =============================================================================
-- ChriOnline - Schéma base de données PostgreSQL
-- Conforme au cahier des charges (niveau minimum + niveau avancé)
-- =============================================================================
-- 1) Créer la base : CREATE DATABASE chrionline;
-- 2) Se connecter à chrionline et exécuter ce script.
-- =============================================================================

-- Types énumérés (création si pas déjà existants)
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('CLIENT', 'ADMIN');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
DO $$ BEGIN
    CREATE TYPE order_status AS ENUM ('EN_ATTENTE', 'VALIDEE', 'EXPEDIEE', 'LIVREE', 'ANNULEE');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
-- Si le type order_status existait déjà sans ANNULEE : ALTER TYPE order_status ADD VALUE 'ANNULEE';

-- -----------------------------------------------------------------------------
-- Catégories de produits (niveau avancé)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- -----------------------------------------------------------------------------
-- Utilisateurs (inscription, authentification, rôle admin)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'CLIENT',
    phone VARCHAR(50),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger pour updated_at (équivalent ON UPDATE CURRENT_TIMESTAMP)
-- Les NOTICE "trigger does not exist, skipping" au 1er run sont normaux.
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS users_updated_at ON users;
CREATE TRIGGER users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();

-- -----------------------------------------------------------------------------
-- Produits (nom, prix, description, stock, catégorie)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    category_id INT REFERENCES categories(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NOTICE "does not exist, skipping" = normal au 1er run
DROP TRIGGER IF EXISTS products_updated_at ON products;
CREATE TRIGGER products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();

-- -----------------------------------------------------------------------------
-- Panier (niveau minimum)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

-- -----------------------------------------------------------------------------
-- Commandes (ID unique, statuts niveau avancé)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(50) PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    total DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    status order_status NOT NULL DEFAULT 'EN_ATTENTE',
    payment_method VARCHAR(50) DEFAULT 'CARTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NOTICE "does not exist, skipping" = normal au 1er run
DROP TRIGGER IF EXISTS orders_updated_at ON orders;
CREATE TRIGGER orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();

-- -----------------------------------------------------------------------------
-- Articles de commande
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0)
);

-- -----------------------------------------------------------------------------
-- Sessions utilisateurs
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_sessions (
    id VARCHAR(100) PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- Index pour les performances
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created ON orders(created_at);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_user ON cart_items(user_id);

-- =============================================================================
-- DONNÉES INITIALES
-- =============================================================================
-- Admin par défaut : mot de passe "admin123" (hash = Java hashCode, côté serveur AuthService)
INSERT INTO users (email, password_hash, name, role)
VALUES ('admin@chrionline.com', '-1892187234', 'Administrateur', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
-- Si l'admin existait déjà avec l'ancien hash, exécuter une fois : UPDATE users SET password_hash = '-1892187234' WHERE email = 'admin@chrionline.com';

-- Catégories
INSERT INTO categories (name, description) VALUES
('Électronique', 'Produits électroniques'),
('Vêtements', 'Mode et vêtements'),
('Alimentation', 'Produits alimentaires')
ON CONFLICT (name) DO NOTHING;

-- Produits de test
INSERT INTO products (name, description, price, stock, category_id)
SELECT 'Ordinateur portable', 'PC portable 15 pouces, 8 Go RAM', 599.99, 50, 1
WHERE EXISTS (SELECT 1 FROM categories WHERE id = 1);
INSERT INTO products (name, description, price, stock, category_id)
SELECT 'Smartphone', 'Smartphone 128 Go', 399.99, 100, 1
WHERE EXISTS (SELECT 1 FROM categories WHERE id = 1);
INSERT INTO products (name, description, price, stock, category_id)
SELECT 'Casque audio', 'Casque sans fil Bluetooth', 79.99, 200, 1
WHERE EXISTS (SELECT 1 FROM categories WHERE id = 1);
INSERT INTO products (name, description, price, stock, category_id)
SELECT 'T-shirt basique', 'T-shirt coton 100%', 19.99, 150, 2
WHERE EXISTS (SELECT 1 FROM categories WHERE id = 2);
INSERT INTO products (name, description, price, stock, category_id)
SELECT 'Jean slim', 'Jean coupe slim', 49.99, 80, 2
WHERE EXISTS (SELECT 1 FROM categories WHERE id = 2);
INSERT INTO products (name, description, price, stock, category_id)
SELECT 'Café moulu', 'Paquet 500g', 12.99, 300, 3
WHERE EXISTS (SELECT 1 FROM categories WHERE id = 3);
