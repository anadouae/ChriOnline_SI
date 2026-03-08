# 7. Planification du Développement - ChriOnline

Réponse structurée au cahier des charges, section 7.

---

## 7.1. Niveau Minimum

| Point du cahier des charges | Réalisation prévue | Livrable / Fichier |
|----------------------------|--------------------|--------------------|
| **Conception de l'architecture** | Architecture client-serveur avec sockets TCP, module common (protocole partagé), séparation serveur/client | `docs/uml/diagramme_classes.puml`, `docs/uml/diagramme_composants.puml`, structure Maven (common, server, client) |
| **Gestion des utilisateurs (inscription, connexion)** | Inscription (REGISTER), connexion (LOGIN), déconnexion (LOGOUT), vérification côté serveur, sessions | Table `users`, `AuthHandler`, protocole REGISTER\|LOGIN\|LOGOUT |
| **Gestion des produits** | Consultation liste (GET_PRODUCTS), détails d'un produit (GET_PRODUCT) : nom, prix, description, stock | Table `products`, `ProductHandler`, protocole GET_PRODUCTS\|GET_PRODUCT |
| **Gestion du panier** | Ajout (ADD_TO_CART), suppression (REMOVE_FROM_CART), calcul du total (GET_CART) | Table `cart_items`, `CartHandler`, protocole ADD_TO_CART\|REMOVE_FROM_CART\|GET_CART |
| **Gestion des paiements simulés** | Validation commande avec données carte fictives (numéro, expiry, CVV), pas de paiement réel | Protocole VALIDATE_ORDER\|userId\|cardNum\|exp\|cvv, table `orders.payment_method` |
| **Validation des commandes** | Validation du panier, vérification stock, génération ID unique, gestion des erreurs (stock insuffisant, données invalides) | `OrderHandler`, table `orders`, `order_items`, génération UUID ou ORD-YYYYMMDD-NNN |

**Planning niveau minimum (semaines 1–3) :**
1. Semaine 1 : Architecture, BDD, authentification, produits  
2. Semaine 2 : Panier, validation commande, paiement simulé  
3. Semaine 3 : Tests, corrections, livrable niveau minimum  

---

## 7.2. Niveau Avancé

| Point du cahier des charges | Réalisation prévue | Livrable / Fichier |
|----------------------------|--------------------|--------------------|
| **Intégration de l'interface graphique** | Interface Swing ou JavaFX : login, catalogue, panier, checkout, gestion des erreurs | Frames/Panels dans `client/`, écrans ergonomiques |
| **Intégration de la base de données** | Connexion JDBC, requêtes pour users, products, cart, orders, configuration MySQL/PostgreSQL/SQLite | `DatabaseManager`, `database/schema.sql`, `database.properties` |
| **Gestion des paiements simulés** | Déjà prévu au niveau minimum ; formulaire carte côté interface | `CheckoutPanel`, formulaire carte bancaire fictif |
| **Gestion des stocks** | Mise à jour automatique du stock après validation de commande (décrémentation) | `OrderHandler` → `updateStock(productId, qty)`, table `products.stock` |
| **Gestion des rôles administrateur** | Rôle CLIENT/ADMIN dans `users`, interface admin distincte | Colonne `users.role`, écrans admin (gestion produits, commandes, utilisateurs) |
| **Ajout de l'historique et des notifications** | Historique : liste des commandes par utilisateur (GET_ORDERS). Notifications : envoi UDP ou message de confirmation | Protocole GET_ORDERS, `DatagramSocket` UDP pour notification de confirmation |

**Planning niveau avancé (semaines 4–6) :**
1. Semaine 4 : Interface graphique complète, intégration BDD  
2. Semaine 5 : Gestion stocks, rôles admin, historique commandes  
3. Semaine 6 : Notifications, tests, livrable final  

---

## Planning global

```
Semaine 1-2 : Niveau minimum (backend + protocole)
Semaine 3   : Niveau minimum (tests, correctifs)
Semaine 4   : Interface graphique + intégration BDD
Semaine 5   : Rôles admin, stocks, historique
Semaine 6   : Notifications, polish, livraison
```

---

## Correspondance cahier des charges ↔ projet

| Cahier des charges | Réponse |
|-------------------|---------|
| Conception de l'architecture | Diagrammes UML, structure Maven, protocole commun |
| Gestion des utilisateurs | `users`, `AuthHandler`, REGISTER/LOGIN/LOGOUT |
| Gestion des produits | `products`, `ProductHandler`, GET_PRODUCTS/GET_PRODUCT |
| Gestion du panier | `cart_items`, `CartHandler`, ADD_TO_CART/REMOVE_FROM_CART/GET_CART |
| Paiements simulés | VALIDATE_ORDER avec données carte fictives |
| Validation des commandes | `orders`, `order_items`, ID unique, gestion erreurs |
| Interface graphique | Swing/JavaFX dans `client/` |
| Base de données | JDBC, MySQL/PostgreSQL/SQLite, schéma fourni |
| Gestion des stocks | Décrémentation `products.stock` à la validation |
| Rôles administrateur | `users.role`, interface admin |
| Historique et notifications | GET_ORDERS, DatagramSocket UDP |
