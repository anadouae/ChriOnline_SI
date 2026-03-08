# Vérification du diagramme UML et proposition d’architecture – ChriOnline

**Projet :** Application E-Commerce ChriOnline  
**Équipe :** Binôme  
**Référence :** Cahier des charges Mini-Projet 1 – Mod. SSI-AT UAE-ENSAT 2026

---

## 1. Vérification du diagramme de classes (UML fourni)

### 1.1 Points conformes au cahier des charges

| Exigence | Élément du diagramme | Conforme |
|----------|----------------------|----------|
| Architecture client-serveur sockets | `Server`, `GestionnaireClient` (TCP) | Oui |
| Notifications UDP | `NotificationsUDP` (DatagramSocket) | Oui |
| Multi-clients (threads) | `Server.listDeThreadsClientsConnectes`, `GestionnaireClient.threadClientConnecte` | Oui |
| Utilisateurs (inscription, auth) | `Utilisateur`, `Client`, `Administrateur`, `UtilisateurDAO` | Oui |
| Produits (liste, détail, stock) | `Produit`, `Categorie`, `ProduitDAO` | Oui |
| Panier (ajout, suppression, total) | `Panier`, `LignePanier`, méthodes côté `Client` | Oui |
| Commande (ID unique, validation) | `Commande.genererIdUnique()`, `Commande.valider(paiement)` | Oui |
| Paiement simulé (carte / fictif) | `Paiement`, `PaiementCarte`, `PaiementFictif` | Oui |
| Statuts de commande | `StatutCommande` : EN_ATTENTE, VALIDEE, EXPEDIEE, LIVREE, ANNULEE | Oui |
| Persistance BDD via JDBC | `ConnexionDAO`, DAOs (Utilisateur, Produit, Commande) | Oui |
| Gestion des erreurs / validation | Méthodes de validation dans les entités (ex. `validerCarte()`) | Oui |

---

### 1.2 Éléments manquants ou à préciser dans le diagramme

| Élément | Description | Recommandation |
|--------|-------------|----------------|
| **Session utilisateur** | Cahier : « Gestion des sessions utilisateurs ». Pas de classe dédiée sur le diagramme. | Ajouter une classe `Session` ou `UserSession` (id session, userId, expiresAt) et le lien avec `Utilisateur`. Le projet actuel a déjà `UserSession` en BDD et dans le diagramme PlantUML. |
| **DAO Panier** | Le diagramme montre uniquement `UtilisateurDAO`, `ProduitDAO`, `CommandeDAO`. Le panier est géré où ? | Soit ajouter un `PanierDAO` (ou `CartDAO`) pour `ADD_TO_CART`, `REMOVE_FROM_CART`, `GET_CART`, soit documenter que le panier est géré via une table `cart_items` et un service/DAO dédié (comme dans l’implémentation actuelle). |
| **Persistance des paiements** | Les classes `Paiement` / `PaiementCarte` / `PaiementFictif` sont présentes mais pas de table ou DAO associé. | Pour le niveau minimum, le paiement peut rester simulé en mémoire (pas de table). Pour le niveau avancé (traçabilité), ajouter une table `payments` et éventuellement un `PaiementDAO` ou gérer dans `CommandeDAO`. |
| **Lien GestionnaireClient ↔ DAOs** | Le diagramme indique que `GestionnaireClient` dépend des DAOs. | Préciser qu’il délègue à des **services** (AuthService, ProductService, CartService, OrderService) qui eux utilisent les DAOs, pour respecter une couche métier entre réseau et accès données. |
| **Protocole (contrat client-serveur)** | Aucune classe « Protocole » ou liste des commandes TCP. | Ajouter une classe/stéréotype `Protocol` (constantes : REGISTER, LOGIN, GET_PRODUCTS, ADD_TO_CART, VALIDATE_ORDER, etc.) partagée entre client et serveur, comme dans `common/Protocol.java`. |
| **Image produit (niveau avancé)** | `Produit` a `imageUrl` sur le diagramme. | Conserver `imageUrl` pour l’avancé ; prévoir la colonne `image_url` en BDD et dans le modèle commun. |

---

### 1.3 Synthèse vérification diagramme

- **Conforme :** Réseau (TCP/UDP), utilisateurs, rôles (Client/Administrateur), produits, catégories, panier, commandes, statuts, paiement simulé (carte/fictif), multi-threads, DAOs.
- **À compléter :** Session utilisateur, DAO ou service Panier explicite, optionnellement persistance paiements et classe Protocole.
- **Cohérence avec l’existant :** Le projet ChriOnline (PlantUML dans `docs/uml/`) est déjà aligné avec cette vision ; les écarts listés ci-dessus peuvent être intégrés au diagramme commun pour qu’il reflète à la fois le cahier des charges et l’implémentation.

---

## 2. Architecture proposée et structure du projet

### 2.1 Principes

- **Client – Serveur natif** : communication TCP pour toutes les opérations métier (auth, panier, commandes, paiement).
- **UDP** : réservé aux notifications (ex. confirmation de commande).
- **Séparation des responsabilités** :  
  **Réseau** → **Handlers / Services** → **DAO** → **BDD**.
- **Partage client / serveur** : module `common` (protocole, modèles de données) pour éviter les duplications et les incohérences.

### 2.2 Structure des dossiers (recommandée)

```
ChriOnline/
├── common/                          # Partagé client + serveur
│   └── src/main/java/com/chrionline/common/
│       ├── Protocol.java            # Constantes du protocole (REGISTER, LOGIN, etc.)
│       └── models/                  # Entités sérialisables (User, Product, Order, CartItem, etc.)
│           ├── User.java
│           ├── Product.java
│           ├── Order.java
│           ├── OrderItem.java
│           ├── CartItem.java
│           └── PaymentInfo.java
│
├── server/                          # Serveur Java (Personne 1)
│   └── src/main/
│       ├── java/com/chrionline/server/
│       │   ├── ChriOnlineServer.java      # ServerSocket, boucle accept(), pool de threads
│       │   ├── ClientHandler.java         # 1 thread par client, lecture/écriture TCP
│       │   ├── db/
│       │   │   └── DatabaseManager.java   # Connexion JDBC (équivalent ConnexionDAO)
│       │   ├── service/                   # Couche métier (équivalent logique des DAOs + règles)
│       │   │   ├── AuthService.java
│       │   │   ├── ProductService.java
│       │   │   ├── CartService.java
│       │   │   └── OrderService.java
│       │   └── (optionnel) udp/
│       │       └── NotificationsUDP.java   # Notifications UDP
│       └── resources/
│           └── database.properties
│
├── client/                          # Client Java + IHM (Personne 2)
│   └── src/main/java/com/chrionline/client/
│       ├── ChriOnlineClient.java    # Point d’entrée
│       ├── network/
│       │   └── NetworkClient.java   # Socket TCP, sendRequest / receive
│       ├── ui/                      # Swing ou JavaFX
│       │   ├── LoginFrame.java
│       │   ├── MainFrame.java
│       │   ├── ProductListPanel.java
│       │   ├── CartPanel.java
│       │   └── CheckoutPanel.java
│       └── (avancé) ui/admin/       # Interface administrateur
│
├── database/                        # Scripts BDD (Personne 1, utilisés par le serveur)
│   ├── schema_postgresql.sql        # Schéma PostgreSQL
│   ├── schema_sqlite.sql            # Option SQLite
│   └── README_POSTGRESQL.md
│
└── docs/
    ├── PROTOCOLE.md                 # Spécification des commandes (à définir ensemble)
    ├── REPARTITION_TACHES.md        # Répartition binôme
    ├── VERIFICATION_DIAGRAMME_ET_ARCHITECTURE.md  # Ce document
    └── uml/
        └── diagramme_classes.puml   # Diagramme de classes à jour
```

### 2.3 Flux de communication

1. **Client** : IHM → `NetworkClient.sendRequest(req)` → Socket TCP → serveur.
2. **Serveur** : `ClientHandler` reçoit la ligne, parse `COMMANDE|...`, délègue au bon **Service** (Auth, Product, Cart, Order).
3. **Service** : utilise `DatabaseManager` (ou DAOs) pour lire/écrire en BDD, applique les règles métier (stock, validation commande, paiement simulé).
4. **Réponse** : Service retourne une chaîne `OK|...` ou `ERROR|...` ; `ClientHandler` renvoie au client.
5. **Optionnel** : après validation d’une commande, le serveur envoie une notification UDP (ex. « Commande XYZ validée »).

---

## 3. Répartition des tâches en binôme (sans tâches partagées)

Objectif : **zéro tâche partagée** – chaque livrable est attribué à une seule personne, avec des interfaces claires (protocole, `common`).

### 3.1 Décision commune (avant de coder)

- **Une seule fois, ensemble :**
  - Valider le **protocole** (PROTOCOLE.md + `Protocol.java`) : noms des commandes, format des paramètres et des réponses.
  - Valider le **schéma BDD** (tables, types enum) et le fichier `database.properties` (URL, utilisateur).
  - S’accorder sur le **diagramme de classes** (ajout Session, CartDAO/service, Protocole si besoin).

### 3.2 Personne 1 – Serveur et base de données

| # | Tâche | Fichiers / modules | Dépendances |
|---|--------|---------------------|-------------|
| 1 | Serveur TCP (ServerSocket, 1 thread par client) | `ChriOnlineServer`, `ClientHandler` | Aucune |
| 2 | Connexion BDD (JDBC) + schéma | `DatabaseManager`, `database.properties`, `schema_postgresql.sql` | BDD créée |
| 3 | Authentification (REGISTER, LOGIN, LOGOUT) + sessions | `AuthService`, tables `users`, `user_sessions` | 1, 2 |
| 4 | Produits (GET_PRODUCTS, GET_PRODUCT) | `ProductService`, table `products` (+ `categories` si avancé) | 1, 2 |
| 5 | Panier (ADD_TO_CART, REMOVE_FROM_CART, GET_CART) | `CartService`, table `cart_items` | 1, 2, 3 |
| 6 | Commandes (VALIDATE_ORDER, ID unique, paiement simulé) | `OrderService`, tables `orders`, `order_items` | 1–5 |
| 7 | (Avancé) Mise à jour du stock après commande | `OrderService` → décrémenter `products.stock` | 6 |
| 8 | (Avancé) Historique commandes (GET_ORDERS) | `OrderService` / handler | 6 |
| 9 | (Avancé) Rôles (CLIENT / ADMIN) | Colonne `users.role`, vérification dans les services | 3 |
| 10 | (Avancé) Notifications UDP | `NotificationsUDP`, envoi après validation commande | 6 |

**Tests :** Client console ou script TCP qui envoie des commandes du protocole (sans IHM).

---

### 3.3 Personne 2 – Client et interface graphique

| # | Tâche | Fichiers / modules | Dépendances |
|---|--------|---------------------|-------------|
| 1 | Connexion TCP + protocole | `NetworkClient` (socket, envoi/réception, utilisation de `Protocol`) | Protocole validé |
| 2 | Écran connexion / inscription | `LoginFrame` (ou équivalent), appels REGISTER / LOGIN | 1 |
| 3 | Catalogue produits (liste + détail) | `ProductListPanel`, `ProductDetailPanel`, GET_PRODUCTS / GET_PRODUCT | 1, 2 |
| 4 | Panier (ajout, suppression, total) | `CartPanel`, ADD_TO_CART, REMOVE_FROM_CART, GET_CART | 1, 2 |
| 5 | Validation commande + formulaire paiement fictif | `CheckoutPanel`, VALIDATE_ORDER (carte ou fictif) | 1–4 |
| 6 | Gestion des erreurs et validation des champs | Affichage des `ERROR|...` serveur, validation des formulaires | Dès le début |
| 7 | (Avancé) Interface ergonomique | Thème, navigation, feedback utilisateur | 1–6 |
| 8 | (Avancé) Historique des commandes | Panneau / écran GET_ORDERS | 1, 2 |
| 9 | (Avancé) Interface administrateur | Gestion produits, commandes, utilisateurs (écrans CRUD) | 1 + protocole étendu |

**Tests :** Mock des réponses serveur (fichier ou serveur minimal) pour développer l’IHM, puis intégration avec le serveur de la Personne 1.

---

### 3.4 Synthèse des zones de responsabilité

| Zone | Personne 1 | Personne 2 |
|------|------------|------------|
| Réseau | Serveur TCP (et UDP si avancé), lecture/écriture protocole | Client TCP, envoi/réception protocole |
| Données | PostgreSQL (ou SQLite), JDBC, schéma, services/DAO | Aucune (tout via le serveur) |
| IHM | Aucune | Tous les écrans (login, catalogue, panier, commande, admin) |
| Logique métier | Auth, produits, panier, commandes, stocks, rôles, notifications | Validation des formulaires, affichage des erreurs |

Chacun peut avancer en parallèle ; l’intégration se fait en connectant le client au serveur une fois que les deux respectent le protocole défini dans `common` et PROTOCOLE.md.

---

## 4. Modifications techniques recommandées

- **Schéma BDD :** Ajouter le statut `ANNULEE` à l’enum `order_status` (alignement avec le diagramme et le cahier des charges).
- **Diagramme UML :** Intégrer les compléments listés en 1.2 (Session, DAO/Service Panier, Protocole, optionnel Paiement persisté) pour qu’il soit la référence unique du projet.
- **Protocole :** Garder un seul document (PROTOCOLE.md) et un seul fichier `Protocol.java` dans `common`, mis à jour en cas d’extension (ex. commandes admin).

---

## 5. Conclusion

- Le **diagramme fourni** couvre l’essentiel du cahier des charges (niveau minimum et avancé). Les ajouts proposés (sessions, DAO/service panier, protocole, statut ANNULEE) le rendent complet et aligné avec l’implémentation actuelle de ChriOnline.
- L’**architecture** en trois modules (`common`, `server`, `client`) avec une couche services côté serveur et un protocole partagé permet un développement en binôme sans chevauchement et une intégration simple.
- La **répartition des tâches** ci-dessus respecte l’exigence « pas de tâches partagées » et permet un suivi d’avancement clair (Git, branches ou livrables par personne).
