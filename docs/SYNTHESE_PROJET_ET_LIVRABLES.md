# ChriOnline – Synthèse du projet, architecture, stack et livrables

**Mini-Projet 1 – Application E-Commerce « ChriOnline »**  
**Cahier des charges – Mod. SSI-AT UAE-ENSAT 2026**

---

## 1. Le but du projet

### 1.1 Objectif général

Développer une **application e-commerce en Java** qui permet aux utilisateurs de :

- **Consulter** des produits,
- **Les ajouter au panier** et les retirer,
- **Effectuer des achats en ligne** (validation de commande + paiement simulé),

en utilisant une **communication réseau basée sur les sockets TCP/UDP** (sans HTTP ni framework web).

### 1.2 Contraintes imposées

- **Architecture client-serveur native** : le client et le serveur communiquent via **TCP** pour toutes les opérations (auth, panier, commandes, paiement) et éventuellement **UDP** pour les notifications.
- **Multi-clients** : le serveur accepte plusieurs clients simultanés (threads).
- **Persistance** : les données (utilisateurs, produits, paniers, commandes) sont stockées dans une **base de données** (MySQL, PostgreSQL ou SQLite) via **JDBC**.
- **Interface** : application **desktop** (JavaFX ou Swing).
- **Sécurité** : authentification login/mot de passe, vérification côté serveur, gestion des sessions.

### 1.3 Périmètre

| Niveau | Contenu |
|--------|--------|
| **Minimum** | Processus d’achat complet **sans** interface graphique avancée (console ou IHM simple). |
| **Avancé** | Interface graphique ergonomique, BDD complète, UML, gestion avancée (statuts commandes, historique, admin, notifications). |

---

## 2. Architecture proposée

### 2.1 Schéma global

```
                    ┌─────────────────────────────────────────────────────────┐
                    │                     RÉSEAU                               │
                    │  TCP : auth, panier, commandes, paiement                   │
                    │  UDP (optionnel) : notifications (ex. confirmation)      │
                    └─────────────────────────────────────────────────────────┘
                                          │
         ┌────────────────────────────────┼────────────────────────────────┐
         │                                │                                  │
         ▼                                ▼                                  ▼
┌─────────────────┐            ┌─────────────────┐                ┌─────────────────┐
│     CLIENT      │            │     SERVEUR     │                │  BASE DE DONNÉES │
│  (Application   │  Socket    │  (Application   │     JDBC       │  (PostgreSQL /   │
│   Java desktop) │ ◄────────► │   Java)         │ ◄────────────► │   MySQL/SQLite)  │
│                 │   TCP      │                 │                │                  │
│  - IHM (Swing/  │            │  - ServerSocket │                │  users          │
│    JavaFX)      │            │  - 1 thread par  │                │  products       │
│  - NetworkClient│            │    client       │                │  categories     │
│  - Protocole    │            │  - Services     │                │  cart_items     │
│    (common)     │            │  - DatabaseManager               │  orders         │
└─────────────────┘            └─────────────────┘                │  order_items    │
         │                                │                        │  user_sessions  │
         │                                │                        └─────────────────┘
         └────────────────────────────────┘
                    │
                    ▼
         ┌─────────────────────┐
         │  MODULE COMMON      │
         │  (partagé client +  │
         │   serveur)          │
         │  - Protocol.java    │
         │  - Modèles (User,   │
         │    Product, etc.)   │
         └─────────────────────┘
```

### 2.2 Couches côté serveur

| Couche | Rôle | Exemples |
|--------|------|----------|
| **Réseau** | Accepter les connexions TCP, lire/écrire les lignes de protocole | `ChriOnlineServer`, `ClientHandler` |
| **Traitement des requêtes** | Parser la commande (REGISTER, LOGIN, GET_PRODUCTS, etc.) et déléguer | `ClientHandler.processRequest()` |
| **Métier** | Logique applicative (auth, panier, commandes, paiement simulé) | `AuthService`, `ProductService`, `CartService`, `OrderService` |
| **Accès données** | Connexion BDD, requêtes SQL | `DatabaseManager` (ou DAOs) |
| **Optionnel – UDP** | Envoi de notifications (ex. confirmation de commande) | `NotificationsUDP` |

### 2.3 Couches côté client

| Couche | Rôle | Exemples |
|--------|------|----------|
| **Interface** | Fenêtres, formulaires, tableaux (Swing ou JavaFX) | `LoginFrame`, `MainFrame`, `ProductListPanel`, `CartPanel`, `CheckoutPanel` |
| **Réseau** | Connexion TCP, envoi/réception des messages du protocole | `NetworkClient` |
| **Partagé** | Constantes et modèles communs avec le serveur | `Protocol`, `User`, `Product`, etc. (module `common`) |

### 2.4 Flux d’une requête type

1. **Utilisateur** clique « Ajouter au panier » dans l’IHM.
2. **Client** construit la chaîne : `ADD_TO_CART|userId|productId|quantity` et l’envoie via `NetworkClient.send()` (Socket TCP).
3. **Serveur** : `ClientHandler` reçoit la ligne, identifie la commande `ADD_TO_CART`, appelle `CartService.addToCart(...)`.
4. **CartService** vérifie le produit et le stock, met à jour la table `cart_items` via `DatabaseManager`, retourne `OK` ou `ERROR|message`.
5. **ClientHandler** renvoie cette réponse au client.
6. **Client** met à jour l’affichage (panier, message d’erreur).

### 2.5 Structure des dossiers du projet

```
ChriOnline/
├── common/                    # Partagé client + serveur
│   └── src/main/java/.../common/
│       ├── Protocol.java      # Constantes (REGISTER, LOGIN, GET_PRODUCTS, etc.)
│       └── models/            # User, Product, Order, CartItem, PaymentInfo...
│
├── server/                    # Serveur Java
│   └── src/main/
│       ├── java/.../server/
│       │   ├── ChriOnlineServer.java
│       │   ├── ClientHandler.java
│       │   ├── db/DatabaseManager.java
│       │   ├── service/       # AuthService, ProductService, CartService, OrderService
│       │   └── (optionnel) udp/NotificationsUDP.java
│       └── resources/database.properties
│
├── client/                    # Client Java + IHM
│   └── src/main/java/.../client/
│       ├── ChriOnlineClient.java
│       ├── network/NetworkClient.java
│       └── ui/                # LoginFrame, MainFrame, ProductListPanel, CartPanel, Checkout...
│
├── database/                  # Scripts SQL
│   ├── schema_postgresql.sql  # (ou schema.sql pour MySQL, schema_sqlite.sql)
│   └── README_*.md
│
└── docs/                      # Documentation, UML, protocole, répartition des tâches
    ├── PROTOCOLE.md
    ├── REPARTITION_TACHES.md
    └── uml/diagramme_classes.puml
```

---

## 3. Stack technologique

| Domaine | Technologie imposée / choisie | Usage |
|---------|-------------------------------|--------|
| **Langage** | **Java** (11 ou +) | Client, serveur, logique métier |
| **Réseau** | **Sockets Java** | `Socket` / `ServerSocket` (TCP), `DatagramSocket` (UDP) |
| **Build** | **Maven** | Compilation, dépendances, exécution (`mvn exec:java`) |
| **Base de données** | **MySQL** ou **PostgreSQL** ou **SQLite** | Stockage utilisateurs, produits, paniers, commandes |
| **Accès BDD** | **JDBC** | Connexion et requêtes SQL depuis le serveur |
| **Interface graphique** | **Swing** ou **JavaFX** | Application desktop (fenêtres, formulaires, tableaux) |
| **Sérialisation protocole** | Texte (chaînes avec séparateur `|`) | Format `COMMANDE|param1|param2|...` et `OK|...` / `ERROR|...` |
| **Gestion de versions** | **Git** | Dépôt GitHub/GitLab, branches, suivi d’avancement |
| **Documentation / UML** | Fichiers texte, PlantUML (optionnel) | Diagramme de classes, spécification du protocole |

**Résumé :** Java + Sockets TCP/UDP + JDBC + BDD (PostgreSQL/MySQL/SQLite) + Swing ou JavaFX + Maven + Git.

---

## 4. Résultat attendu – Livrables pour respecter le cahier des charges

Pour **respecter parfaitement** le cahier des charges, le projet doit livrer les éléments suivants, en distinguant **niveau minimum** et **niveau avancé**.

---

### 4.1 Niveau minimum (obligatoire)

| # | Exigence du cahier | Livrable / critère de validation |
|---|--------------------|----------------------------------|
| 1 | Enregistrement et authentification des utilisateurs | Inscription (nom, email, mot de passe) et connexion (email, mot de passe) ; vérification côté serveur ; réponse OK/ERROR. |
| 2 | Consultation de la liste des produits disponibles | Commande type GET_PRODUCTS ; le serveur renvoie la liste ; le client l’affiche (tableau ou liste). |
| 3 | Affichage des détails d’un produit (nom, prix, description, stock) | GET_PRODUCT ou données dans GET_PRODUCTS ; affichage détail dans l’IHM ou la console. |
| 4 | Ajout et suppression de produits dans le panier | ADD_TO_CART et REMOVE_FROM_CART ; persistance en BDD (table panier / cart_items). |
| 5 | Calcul du total du panier | GET_CART renvoie les lignes + total ; affichage du total côté client. |
| 6 | Validation d’une commande + gestion des erreurs et validation des données | Commande VALIDATE_ORDER ; vérification stock, panier non vide ; messages d’erreur clairs (ERROR|...). |
| 7 | Système de paiement simulé (carte bancaire ou paiement fictif) | Formulaire (numéro carte, expiration, CVV) ou code fictif ; traitement côté serveur sans vrai paiement ; enregistrement de la commande. |
| 8 | Génération d’un identifiant unique pour chaque commande | Chaque commande a un ID unique (ex. CMD-2026-0001) ; renvoyé au client après validation. |
| 9 | Communication TCP pour les opérations principales | Toutes les opérations (auth, panier, commande, paiement) passent par Socket TCP. |
| 10 | Base de données (MySQL, PostgreSQL ou SQLite) + JDBC | Schéma SQL avec tables utilisateurs, produits, panier, commandes ; serveur utilise JDBC pour lire/écrire. |
| 11 | Serveur multi-clients (threads) | Un thread (ou Runnable) par client connecté ; plusieurs clients peuvent utiliser l’application en parallèle. |
| 12 | Gestion des sessions / accès sécurisés | Vérification login/mot de passe côté serveur ; identification de l’utilisateur (userId) pour panier et commandes. |

**Livrables livrables concrets :**

- Code source (client + serveur + common + scripts BDD).
- Schéma de base de données (fichier .sql).
- Application exécutable : **serveur** lançable (ex. `java -jar server.jar` ou `mvn exec:java`), **client** lançable (ex. `java -jar client.jar` ou `mvn exec:java`).
- Base de données configurée et opérationnelle (instructions dans un README ou doc).

---

### 4.2 Niveau avancé (pour une réalisation complète)

| # | Exigence du cahier | Livrable / critère de validation |
|---|--------------------|----------------------------------|
| 1 | Interface graphique ergonomique | IHM desktop (Swing ou JavaFX) : écrans connexion, catalogue, panier, paiement ; navigation claire, messages d’erreur visibles. |
| 2 | Gestion complète des utilisateurs (profil, historique des commandes) | Affichage du profil (nom, email) et liste des commandes passées (GET_ORDERS). |
| 3 | Gestion des catégories de produits | Table catégories en BDD ; produits liés à une catégorie ; affichage/filtre par catégorie possible. |
| 4 | Gestion du stock (mise à jour automatique après achat) | Lors de la validation d’une commande, décrémenter le stock des produits en BDD ; refuser si stock insuffisant. |
| 5 | Gestion des statuts de commande (en attente, validée, expédiée, livrée) | Enum/table statuts ; chaque commande a un statut ; possibilité de le modifier (côté admin). |
| 6 | Historique des commandes | Commande GET_ORDERS|userId ; affichage liste des commandes (ID, date, total, statut) dans l’IHM. |
| 7 | Notifications de confirmation de commande | Après validation, envoi d’un message UDP (ex. notification « Commande XYZ validée ») ou affichage dans l’IHM. |
| 8 | Interface administrateur : ajouter, modifier, supprimer des produits | Écrans admin (après connexion avec un compte admin) : liste produits, formulaire ajout/édition, suppression. |
| 9 | Interface administrateur : gérer les commandes | Liste des commandes, changement de statut (en attente → validée → expédiée → livrée). |
| 10 | Interface administrateur : gérer les utilisateurs | Liste des utilisateurs (email, nom, rôle) ; modification du rôle (Client/Admin) si prévu. |
| 11 | Modélisation UML | Diagramme de classes réalisé (et si possible diagrammes de séquence/cas d’utilisation) ; cohérent avec le code. |
| 12 | Répartition des tâches sans tâches partagées | Document (ex. REPARTITION_TACHES.md) indiquant qui fait quoi ; pas de fichier développé à deux. |
| 13 | Utilisation d’un gestionnaire de versions (Git) | Dépôt Git (GitHub/GitLab) ; commits réguliers ; historique lisible. |
| 14 | Déploiement : application exécutable (.jar) et BDD opérationnelle | Build JAR (ou lancement Maven) pour le serveur et pour le client ; README avec instructions de lancement et configuration BDD. |

---

### 4.3 Checklist synthétique « livrables finaux »

À la remise du projet, vous devez pouvoir cocher :

**Fonctionnel (niveau minimum)**  
- [ ] Inscription et connexion utilisateur (côté serveur + client).  
- [ ] Liste et détail des produits.  
- [ ] Ajout / suppression au panier, calcul du total.  
- [ ] Validation de commande avec paiement simulé et ID unique.  
- [ ] Gestion des erreurs (stock, panier vide, données invalides).  
- [ ] Communication TCP pour toutes ces opérations.  
- [ ] BDD (MySQL/PostgreSQL/SQLite) avec schéma fourni, accès JDBC.  
- [ ] Serveur multi-clients (plusieurs clients en même temps).  

**Fonctionnel (niveau avancé)**  
- [ ] Interface graphique desktop (Swing ou JavaFX) complète et lisible.  
- [ ] Catégories de produits et gestion du stock (décrément après achat).  
- [ ] Statuts de commande et historique des commandes (GET_ORDERS).  
- [ ] Notifications (UDP ou équivalent).  
- [ ] Interface admin : produits (CRUD), commandes (statuts), utilisateurs (liste/rôles).  

**Technique et méthodologie**  
- [ ] Diagramme de classes UML.  
- [ ] Répartition des tâches documentée (binôme, pas de tâches partagées).  
- [ ] Projet sous Git (GitHub/GitLab).  
- [ ] Application exécutable (JAR ou Maven) + BDD configurée et documentée (README).  

---

## 5. En résumé

| Question | Réponse |
|----------|---------|
| **But du projet** | Application e-commerce Java (ChriOnline) : consulter des produits, gérer un panier, valider une commande avec paiement simulé, en client-serveur **sockets TCP/UDP**, avec BDD et IHM desktop. |
| **Architecture proposée** | Client (IHM + NetworkClient) ↔ TCP ↔ Serveur (ServerSocket, 1 thread par client, Services + DatabaseManager) ↔ JDBC ↔ BDD ; module `common` pour le protocole et les modèles ; UDP optionnel pour les notifications. |
| **Stack technologique** | Java 11+ • Maven • Sockets TCP/UDP • JDBC • MySQL/PostgreSQL/SQLite • Swing ou JavaFX • Git. |
| **Résultat attendu** | Niveau minimum : processus d’achat complet (auth, produits, panier, commande, paiement simulé, BDD, multi-clients). Niveau avancé : IHM ergonomique, catégories, stock, statuts et historique des commandes, interface admin, UML, répartition des tâches, dépôt Git, JAR + BDD opérationnelle. |

En suivant cette synthèse et la checklist des livrables, le projet respecte parfaitement le cahier des charges du Mini-Projet 1 ChriOnline.
