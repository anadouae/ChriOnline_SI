# Correspondance Cahier des Charges ↔ Base de données

Référence explicite entre le cahier des charges et le schéma SQL.

---

## Niveau minimum

| Cahier des charges | Table / Colonne |
|-------------------|-----------------|
| Enregistrement et authentification des utilisateurs | `users` (email, password_hash, name) |
| Consultation liste des produits | `products` |
| Détails produit (nom, prix, description, stock) | `products` (name, price, description, stock) |
| Ajout/suppression panier | `cart_items` |
| Calcul du total du panier | Dérivé de `cart_items` + `products.price` |
| Validation commande + gestion erreurs | `orders`, `order_items` |
| Paiement simulé (carte bancaire ou fictif) | `orders.payment_method` |
| Identifiant unique par commande | `orders.id` (VARCHAR, ex: UUID ou ORD-YYYYMMDD-NNN) |

---

## Niveau avancé

| Cahier des charges | Table / Colonne |
|-------------------|-----------------|
| Gestion complète utilisateurs (profil, historique) | `users` (phone, address), `orders` (historique) |
| Gestion des catégories de produits | `categories`, `products.category_id` |
| Gestion du stock (mise à jour après achat) | `products.stock` (décrémenté dans order_items) |
| Statuts de commande (attente, validée, expédiée, livrée) | `orders.status` (ENUM) |
| Historique des commandes | `orders` + `order_items` par user_id |
| Interface admin - produits (CRUD) | `products`, `categories` |
| Interface admin - commandes | `orders`, `order_items` |
| Interface admin - utilisateurs | `users` |
| Gestion des sessions utilisateurs | `user_sessions` |
| Notifications de confirmation de commande | UDP côté serveur ou log |

---

## Contraintes techniques (cahier des charges)

- **Base de données** : MySQL, PostgreSQL ou SQLite ✓ (2 scripts fournis)
- **Stockage** : utilisateurs, produits, paniers, commandes ✓
