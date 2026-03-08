# Structure complète du projet ChriOnline

Arborescence du projet pour démarrer le travail (serveur en binôme, client, common, BDD, docs).

---

## Arborescence

```
ChriOnline/
├── pom.xml                          # Parent Maven (modules common, server, client)
├── README.md
├── .gitignore
│
├── common/                          # Partagé client + serveur
│   ├── pom.xml
│   └── src/main/java/com/chrionline/common/
│       ├── Protocol.java            # Constantes protocole (REGISTER, LOGIN, SEPARATOR, etc.)
│       └── models/
│           ├── User.java
│           └── Product.java
│
├── server/                          # Serveur Java (TCP, multi-clients)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/chrionline/server/
│       │   ├── ChriOnlineServer.java    # Point d'entrée, ServerSocket, enregistrement des handlers
│       │   ├── ClientHandler.java       # Dispatcher : lit la requête, appelle le bon handler
│       │   ├── handler/                 # Handlers par bloc (binôme sans conflits)
│       │   │   ├── RequestHandler.java     # Interface (accepts, handle)
│       │   │   ├── AuthRequestHandler.java # Bloc A : REGISTER, LOGIN, LOGOUT
│       │   │   ├── ProductRequestHandler.java # Bloc A : GET_PRODUCTS, GET_PRODUCT
│       │   │   ├── CartRequestHandler.java   # Bloc B : ADD_TO_CART, REMOVE_FROM_CART, GET_CART
│       │   │   └── OrderRequestHandler.java  # Bloc B : VALIDATE_ORDER, GET_ORDERS
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── ProductService.java
│       │   │   ├── CartService.java
│       │   │   └── OrderService.java
│       │   └── db/
│       │       └── DatabaseManager.java  # Connexion JDBC (getConnection)
│       └── resources/
│           └── database.properties
│
├── client/                          # Client Java (Swing)
│   ├── pom.xml
│   └── src/main/java/com/chrionline/client/
│       ├── ChriOnlineClient.java    # Point d'entrée
│       ├── network/
│       │   └── NetworkClient.java   # Socket TCP, send/receive
│       └── ui/
│           ├── LoginFrame.java      # Connexion + Inscription
│           ├── MainFrame.java       # Onglets Catalogue, Panier
│           ├── ProductListPanel.java
│           └── CartPanel.java
│
├── database/                       # Scripts BDD
│   ├── schema_postgresql.sql        # Schéma PostgreSQL (tables, enum, index)
│   ├── schema.sql                  # Schéma MySQL
│   ├── schema_sqlite.sql
│   ├── create_database.sql
│   ├── init_postgresql.bat
│   └── README_POSTGRESQL.md
│
└── docs/                            # Documentation
    ├── STRUCTURE_PROJET.md          # Ce fichier
    ├── PROTOCOLE.md                 # Spécification des commandes TCP
    ├── REPARTITION_TACHES.md        # Qui fait quoi (Douae / Merveille)
    ├── SERVEUR_BINOME_SANS_CONFLITS.md  # Répartition serveur en binôme
    ├── SYNTHESE_PROJET_ET_LIVRABLES.md
    ├── STACK_TECHNOLOGIQUE.md
    ├── ARCHITECTURE_FRONTEND_BACKEND.md
    ├── VERIFICATION_DIAGRAMME_ET_ARCHITECTURE.md
    ├── MAQUETTES_FIGMA_DESCRIPTIONS.md
    ├── GUIDE_REALISATION_MAQUETTES_FRONTEND.md
    ├── CAHIER_DES_CHARGES_BDD.md
    ├── INSTALL_DB_POSTGRES_MERVEILLE.md
    ├── PLAN_PROCEDURE.md
    ├── PLANIFICATION_DEVELOPPEMENT.md
    └── uml/
        ├── README.md
        ├── diagramme_classes.puml
        ├── diagramme_classes_principales.puml
        ├── diagramme_classes_vertical.puml
        ├── diagramme_composants.puml
        ├── diagramme_cas_utilisation.puml
        ├── diagramme_sequence_achat.puml
        └── ...
```

---

## Rôle des dossiers

| Dossier | Rôle |
|--------|------|
| **common** | Protocole et modèles partagés. Ne pas dupliquer entre client et server. |
| **server** | Serveur TCP, handlers (Auth, Product, Cart, Order), services, BDD. Travail en binôme : Bloc A = Auth + Product, Bloc B = Cart + Order. |
| **client** | Application desktop Swing : connexion, catalogue, panier, commande. |
| **database** | Scripts SQL (PostgreSQL, MySQL, SQLite). Créer la base puis exécuter le schéma. |
| **docs** | Cahier des charges, protocole, répartition, maquettes, UML. |

---

## Démarrer le travail

1. **Cloner / ouvrir** le projet (Git).
2. **Créer la BDD** : ex. PostgreSQL, exécuter `database/schema_postgresql.sql`. Configurer `server/src/main/resources/database.properties`.
3. **Lancer le serveur** : `cd server && mvn exec:java -Dexec.mainClass="com.chrionline.server.ChriOnlineServer"`.
4. **Lancer le client** : `cd client && mvn exec:java -Dexec.mainClass="com.chrionline.client.ChriOnlineClient"`.
5. **Répartition** : voir `docs/REPARTITION_TACHES.md` (client vs serveur) et `docs/SERVEUR_BINOME_SANS_CONFLITS.md` (serveur en binôme).

---

## Fichiers modifiés pour le binôme serveur

- **handler/RequestHandler.java** : interface commune.
- **handler/AuthRequestHandler.java**, **ProductRequestHandler.java** : Bloc A (Personne 1).
- **handler/CartRequestHandler.java**, **OrderRequestHandler.java** : Bloc B (Personne 2).
- **ClientHandler** : utilise une liste de `RequestHandler` au lieu d’un gros switch.
- **ChriOnlineServer** : construit la liste des handlers et la passe à `ClientHandler`.

Chacun travaille dans ses handlers et services sans modifier ceux de l’autre.
