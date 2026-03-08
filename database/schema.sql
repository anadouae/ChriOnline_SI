-- =============================================================================
-- ChriOnline - Schéma base de données
-- Conforme au cahier des charges (niveau minimum + niveau avancé)
-- MySQL / MariaDB
-- =============================================================================

CREATE DATABASE IF NOT EXISTS chrionline;
USE chrionline;

-- =============================================================================
-- NIVEAU MINIMUM + AVANCÉ
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Catégories de produits (niveau avancé - "Gestion des catégories de produits")
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- -----------------------------------------------------------------------------
-- Utilisateurs
-- Inscription, authentification (niveau min)
-- Profil complet, rôle admin (niveau avancé - "Interface administrateur")
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role ENUM('CLIENT', 'ADMIN') NOT NULL DEFAULT 'CLIENT',
    phone VARCHAR(50),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- Produits
-- Détails: nom, prix, description, stock (niveau min)
-- Catégorie (niveau avancé)
-- Stock mis à jour après achat (niveau avancé - "Gestion du stock")
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    CHECK (price >= 0),
    CHECK (stock >= 0)
);

-- -----------------------------------------------------------------------------
-- Panier (niveau minimum)
-- Ajout et suppression de produits dans le panier
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product (user_id, product_id),
    CHECK (quantity > 0)
);

-- -----------------------------------------------------------------------------
-- Commandes
-- Identifiant unique (niveau min - "Génération d'un identifiant unique")
-- Statuts: en attente, validée, expédiée, livrée (niveau avancé)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(50) PRIMARY KEY,
    user_id INT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status ENUM('EN_ATTENTE', 'VALIDEE', 'EXPEDIEE', 'LIVREE') NOT NULL DEFAULT 'EN_ATTENTE',
    payment_method VARCHAR(50) DEFAULT 'CARTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK (total >= 0)
);

-- -----------------------------------------------------------------------------
-- Articles de commande (détail des produits achetés)
-- Permet l'historique des commandes (niveau avancé)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CHECK (quantity > 0),
    CHECK (unit_price >= 0)
);

-- -----------------------------------------------------------------------------
-- Sessions utilisateurs (pour "Gestion des sessions utilisateurs")
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_sessions (
    id VARCHAR(100) PRIMARY KEY,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- Index pour les performances
-- -----------------------------------------------------------------------------
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created ON orders(created_at);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_cart_items_user ON cart_items(user_id);

-- =============================================================================
-- DONNÉES INITIALES
-- =============================================================================

-- Admin par défaut (mot de passe à hasher côté application - ex: "admin123")
INSERT INTO users (email, password_hash, name, role) VALUES
('admin@chrionline.com', 'CHANGER_COTE_SERVEUR_AVEC_BCRYPT', 'Administrateur', 'ADMIN');

-- Catégories
INSERT INTO categories (name, description) VALUES
('Électronique', 'Produits électroniques'),
('Vêtements', 'Mode et vêtements'),
('Alimentation', 'Produits alimentaires');

-- Produits de test
INSERT INTO products (name, description, price, stock, category_id) VALUES
('Ordinateur portable', 'PC portable 15 pouces, 8 Go RAM', 599.99, 50, 1),
('Smartphone', 'Smartphone 128 Go', 399.99, 100, 1),
('Casque audio', 'Casque sans fil Bluetooth', 79.99, 200, 1),
('T-shirt basique', 'T-shirt coton 100%', 19.99, 150, 2),
('Jean slim', 'Jean coupe slim', 49.99, 80, 2),
('Café moulu', 'Paquet 500g', 12.99, 300, 3);
