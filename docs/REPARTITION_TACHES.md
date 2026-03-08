# Répartition des tâches – ChriOnline

**Équipe : Douae et Merveille**  
**Objectif : répartition claire et logique pour que chacune travaille sa partie sans dépendre de l’autre.**

---

## Principe

- **Douae** : tout ce qui est **serveur + base de données PostgreSQL** (backend).
- **Merveille** : tout ce qui est **client Java + interface graphique** (frontend).
- Le **protocole** (PROTOCOLE.md + `common/Protocol.java`) est défini une fois pour toutes au début ; chacune code de son côté en respectant ce contrat.

---

## DOUAE – Serveur et base de données

| # | Tâche | Fichiers / modules | Dépendances |
|---|--------|---------------------|-------------|
| 1 | Architecture serveur (ServerSocket, 1 thread par client) | `ChriOnlineServer`, `ClientHandler` | Aucune |
| 2 | Connexion JDBC PostgreSQL + config | `database.properties`, `DatabaseManager` (ou équivalent), `database/schema_postgresql.sql` | Schéma BDD créé |
| 3 | Authentification : REGISTER, LOGIN, LOGOUT | AuthHandler / AuthService, table `users` | Tâches 1, 2 |
| 4 | Gestion produits : GET_PRODUCTS, GET_PRODUCT | ProductHandler / ProductDAO, table `products` | Tâches 1, 2 |
| 5 | Gestion panier : ADD_TO_CART, REMOVE_FROM_CART, GET_CART | CartHandler / CartService, table `cart_items` | Tâches 1, 2, 3 |
| 6 | Validation commande + paiement simulé + ID unique | OrderHandler / OrderService, tables `orders`, `order_items` | Tâches 1–5 |
| 7 | (Avancé) Mise à jour du stock après commande | OrderService → décrémenter `products.stock` | Tâche 6 |
| 8 | (Avancé) GET_ORDERS (historique commandes) | OrderHandler | Tâche 6 |
| 9 | (Avancé) Rôles admin (CLIENT/ADMIN) | Colonne `users.role`, vérification dans les handlers | Tâche 3 |
| 10 | (Avancé) Notifications UDP (optionnel) | DatagramSocket, envoi confirmation commande | Tâche 6 |

**Tests possibles sans le client graphique :** script de test TCP, ou petit client console qui envoie les commandes du protocole (LOGIN, GET_PRODUCTS, etc.).

---

## MERVEILLE – Client et interface graphique

| # | Tâche | Fichiers / modules | Dépendances |
|---|--------|---------------------|-------------|
| 1 | Connexion socket TCP au serveur + envoi/réception messages | `NetworkClient` ou équivalent, utilisation de `Protocol` (common) | Protocole validé |
| 2 | Écran login + inscription | LoginFrame, RegisterFrame (Swing ou JavaFX) | Tâche 1 |
| 3 | Catalogue produits : liste + détail (nom, prix, description, stock) | ProductListPanel, ProductDetailPanel, appels GET_PRODUCTS / GET_PRODUCT | Tâches 1, 2 |
| 4 | Panier : ajout, suppression, affichage total | CartPanel, appels ADD_TO_CART, REMOVE_FROM_CART, GET_CART | Tâches 1, 2 |
| 5 | Validation commande + formulaire paiement fictif (carte) | CheckoutPanel, appel VALIDATE_ORDER | Tâches 1–4 |
| 6 | Gestion des erreurs et validation des champs | ValidationUtils, affichage des messages ERROR du serveur | Dès le début |
| 7 | (Avancé) Interface ergonomique et complète | Thème, navigation, feedback utilisateur | Tâches 1–6 |
| 8 | (Avancé) Historique des commandes (GET_ORDERS) | OrdersHistoryPanel ou équivalent | Tâches 1, 2 |
| 9 | (Avancé) Interface administrateur (gestion produits, commandes, utilisateurs) | AdminFrame, écrans CRUD selon les besoins | Tâche 1 + protocole étendu si besoin |

**Tests possibles sans le serveur final :** mock/stub des réponses (fichier ou serveur minimal qui renvoie des réponses fixes) pour développer l’IHM, puis connexion au vrai serveur de Douae pour l’intégration.

---

## Protocole (défini ensemble)

Voir [PROTOCOLE.md](PROTOCOLE.md). À valider ensemble avant de coder pour éviter les blocages.

---

## Synthèse

| Zone | Douae | Merveille |
|------|--------|-----------|
| Réseau | Serveur TCP, threads, lecture/écriture protocole | Client TCP, envoi/réception protocole |
| Données | PostgreSQL, JDBC, schéma, DAO/Services | Aucune (tout passe par le serveur) |
| IHM | Aucune | Tous les écrans (login, catalogue, panier, commande, admin) |
| Logique métier | Auth, produits, panier, commandes, stocks, rôles | Validation formulaire, affichage erreurs |

Chacune peut avancer en parallèle ; l’intégration se fait en branchant le client sur le serveur une fois que les deux parties respectent le protocole.
