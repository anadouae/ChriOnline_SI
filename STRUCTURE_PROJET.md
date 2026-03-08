# Structure du projet – ChriOnline-v1 (projet complet)

**ChriOnline-v1** est le **dossier du projet complet** : serveur, base de données, client (interface client + admin), common et documentation. Il répond au cahier des charges (Mini-Projet 1 – Mod. SSI-AT 2026).

---

## 1. Arborescence complète (ChriOnline-v1)

```
ChriOnline-v1/
├── pom.xml                          # Parent Maven (modules common, server, client)
├── README.md
├── .gitignore
├── STRUCTURE_PROJET.md              # Ce fichier
│
├── common/                           # Partagé client + serveur
│   ├── pom.xml
│   └── src/main/java/com/chrionline/common/
│       ├── Protocol.java             # REGISTER, LOGIN, GET_PRODUCTS, ADD_TO_CART, etc.
│       └── models/
│           ├── User.java
│           └── Product.java
│
├── server/                           # Serveur TCP + JDBC
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/chrionline/server/
│       │   ├── ChriOnlineServer.java     # ServerSocket, 1 thread par client
│       │   ├── ClientHandler.java        # Dispatcher (handlers)
│       │   ├── handler/
│       │   │   ├── RequestHandler.java
│       │   │   ├── AuthRequestHandler.java
│       │   │   ├── ProductRequestHandler.java
│       │   │   ├── CartRequestHandler.java
│       │   │   └── OrderRequestHandler.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── ProductService.java
│       │   │   ├── CartService.java
│       │   │   └── OrderService.java
│       │   └── db/
│       │       └── DatabaseManager.java   # Connexion JDBC
│       └── resources/
│           └── database.properties
│
├── client/                           # Interface client + admin (Swing)
│   ├── pom.xml
│   └── src/main/java/com/chrionline/app/
│       ├── ChriOnlineApp.java        # Point d'entrée → HomeFrame
│       ├── model/                    # User, Product, Order, CartItem, etc.
│       ├── network/
│       │   └── ApiService.java       # Données mock (remplaçable par NetworkClient + serveur)
│       └── ui/
│           ├── home/HomeFrame.java
│           ├── auth/LoginFrame.java
│           ├── client/               # Espace client
│           │   ├── ClientMainFrame.java
│           │   ├── CataloguePanel.java
│           │   ├── CartPanel.java
│           │   ├── OrdersPanel.java
│           │   └── PaiementDialog.java
│           ├── admin/                # Espace administrateur
│           │   ├── AdminFrame.java
│           │   ├── AdminHeader.java
│           │   ├── DashboardPanel.java
│           │   ├── ProductsPanel.java
│           │   ├── AddProductDialog.java
│           │   ├── EditProductDialog.java
│           │   ├── AdminOrdersPanel.java
│           │   ├── ChangeStatusDialog.java
│           │   └── UsersPanel.java
│           └── components/
│               ├── ClientHeader.java
│               ├── StatusBadge.java
│               └── UiConstants.java
│
├── database/                         # Base de données
│   ├── schema_postgresql.sql
│   ├── schema.sql
│   ├── schema_sqlite.sql
│   ├── create_database.sql
│   ├── init_postgresql.bat
│   └── README_POSTGRESQL.md
│
└── docs/                             # Documentation
    ├── STRUCTURE_PROJET.md           # (copie dans docs/)
    ├── PROTOCOLE.md
    ├── REPARTITION_TACHES.md
    ├── SERVEUR_BINOME_SANS_CONFLITS.md
    ├── SYNTHESE_PROJET_ET_LIVRABLES.md
    ├── STACK_TECHNOLOGIQUE.md
    ├── MAQUETTES_FIGMA_DESCRIPTIONS.md
    └── uml/                          # Diagrammes (classes, séquences, etc.)
```

---

## 2. Rôle des parties

| Partie | Rôle |
|--------|------|
| **common** | Protocole (`Protocol.java`) et modèles partagés (User, Product). Utilisé par server et client. |
| **server** | Serveur TCP, multi-clients, handlers (Auth, Product, Cart, Order), services, JDBC. |
| **client** | Application desktop Swing : accueil, connexion, espace client (catalogue, panier, commandes), espace admin (produits, commandes, utilisateurs). Données mock par défaut (`ApiService`). |
| **database** | Scripts SQL (PostgreSQL, MySQL, SQLite). Schéma : users, products, categories, cart_items, orders, order_items. |
| **docs** | Cahier des charges, protocole, répartition des tâches, maquettes, UML. |

---

## 3. Conformité au cahier des charges

| Exigence | Où c’est géré |
|----------|----------------|
| Communication **TCP** | `server/` (ChriOnlineServer, ClientHandler) ; `common/Protocol` ; client peut utiliser NetworkClient (à brancher sur ApiService). |
| **Base de données** | `database/schema_postgresql.sql` (et autres schémas). |
| **JDBC** | `server/db/DatabaseManager` + services. |
| **Serveur multi-clients** | `ChriOnlineServer` : 1 thread par client. |
| **Auth, produits, panier, commandes** | Handlers et services dans `server/`. |
| **Interface desktop** (Swing) | `client/` (ChriOnlineApp, ui client + admin). |
| **Rôles / statuts** | BDD (users.role, order_status) ; UI admin dans `client/`. |

---

## 4. Lancer le projet

**Compilation globale :**
```bash
cd ChriOnline-v1
mvn clean install
```

**Lancer le serveur :**
```bash
cd server
mvn exec:java -Dexec.mainClass="com.chrionline.server.ChriOnlineServer"
```

**Lancer l’application (client + admin) :**
```bash
cd client
mvn exec:java -Dexec.mainClass="com.chrionline.app.ChriOnlineApp"
```

**Base de données :** créer la base (ex. PostgreSQL), exécuter `database/schema_postgresql.sql`, puis configurer `server/src/main/resources/database.properties`.
