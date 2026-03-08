-- =============================================================================
-- ChriOnline - Schéma base de données SQLite
-- Conforme au cahier des charges (niveau minimum + niveau avancé)
-- Alternative à MySQL pour déploiement simplifié
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Catégories de produits (niveau avancé)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

-- -----------------------------------------------------------------------------
-- Utilisateurs (inscription, profil, rôle admin)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'CLIENT' CHECK (role IN ('CLIENT', 'ADMIN')),
    phone TEXT,
    address TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- Produits (détails, stock, catégorie)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    category_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    CHECK (price >= 0),
    CHECK (stock >= 0)
);

-- -----------------------------------------------------------------------------
-- Panier
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE (user_id, product_id),
    CHECK (quantity > 0)
);

-- -----------------------------------------------------------------------------
-- Commandes (ID unique, statuts)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id TEXT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total REAL NOT NULL,
    status TEXT NOT NULL DEFAULT 'EN_ATTENTE' 
        CHECK (status IN ('EN_ATTENTE', 'VALIDEE', 'EXPEDIEE', 'LIVREE')),
    payment_method TEXT DEFAULT 'CARTE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK (total >= 0)
);

-- -----------------------------------------------------------------------------
-- Articles de commande
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id TEXT NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price REAL NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CHECK (quantity > 0),
    CHECK (unit_price >= 0)
);

-- -----------------------------------------------------------------------------
-- Sessions utilisateurs
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_sessions (
    id TEXT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created ON orders(created_at);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_user ON cart_items(user_id);

-- Données initiales
INSERT INTO users (email, password_hash, name, role) VALUES
('admin@chrionline.com', 'CHANGER_COTE_SERVEUR_AVEC_BCRYPT', 'Administrateur', 'ADMIN');

INSERT INTO categories (name, description) VALUES
('Électronique', 'Produits électroniques'),
('Vêtements', 'Mode et vêtements'),
('Alimentation', 'Produits alimentaires');

INSERT INTO products (name, description, price, stock, category_id) VALUES
('Ordinateur portable', 'PC portable 15 pouces, 8 Go RAM', 599.99, 50, 1),
('Smartphone', 'Smartphone 128 Go', 399.99, 100, 1),
('Casque audio', 'Casque sans fil Bluetooth', 79.99, 200, 1),
('T-shirt basique', 'T-shirt coton 100%', 19.99, 150, 2),
('Jean slim', 'Jean coupe slim', 49.99, 80, 2),
('Café moulu', 'Paquet 500g', 12.99, 300, 3);
