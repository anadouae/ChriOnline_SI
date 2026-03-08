# Séparer le frontend du serveur – Architecture ChriOnline

Ce document explique comment organiser le projet pour avoir une **séparation nette entre frontend et backend** : deux parties distinctes, déployables séparément, avec un contrat d’interface clair.

---

## 1. Ce que vous avez déjà (séparation actuelle)

Le projet est déjà découpé en **modules Maven** :

| Module   | Rôle                    | C’est quoi dans “frontend / backend” |
|----------|-------------------------|--------------------------------------|
| **client** | IHM + connexion au serveur | **Frontend** (tout ce que l’utilisateur voit et utilise) |
| **server** | Logique métier + BDD + sockets | **Backend** (traitement, données, réseau serveur) |
| **common** | Protocole + modèles partagés | **Contrat** entre frontend et backend |

Donc :
- **Frontend** = module `client` (Swing/JavaFX + `NetworkClient`).
- **Backend** = module `server` (ServerSocket, Services, DatabaseManager, BDD).

La séparation est déjà là. On peut la **renforcer** et, si vous voulez, **passer à un frontend web** plus tard.

---

## 2. Renforcer la séparation (même stack Java + Sockets)

### 2.1 Principe

- **Backend** : ne connaît pas l’IHM. Il expose uniquement un **protocole texte** sur TCP (et éventuellement UDP pour les notifications).
- **Frontend** : ne connaît pas la BDD ni les services. Il envoie des requêtes selon le protocole et affiche les réponses.
- **Contrat** : le fichier `common/Protocol.java` et `docs/PROTOCOLE.md` définissent les commandes et formats. Tout changement de contrat se fait en commun.

### 2.2 Règles à respecter

| Côté     | À faire | À éviter |
|----------|---------|----------|
| **Backend (server)** | Lire/écrire en BDD, vérifier auth, gérer panier/commandes, renvoyer `OK\|...` ou `ERROR\|...` | Aucune logique d’affichage, aucun code spécifique à Swing/JavaFX |
| **Frontend (client)** | Envoyer les commandes du protocole, parser les réponses, mettre à jour l’IHM | Accès direct à la BDD, logique métier (ex. calcul du total panier doit venir du serveur) |
| **common** | Constantes du protocole, DTOs/modèles sérialisables (User, Product, etc.) | Logique métier ou code d’affichage |

### 2.3 Structure des dossiers (frontend / backend bien séparés)

```
ChriOnline/
├── backend/                    # Renommage optionnel de "server" pour clarté
│   ├── pom.xml                 # Dépend : common
│   └── src/main/
│       ├── java/.../server/
│       │   ├── ChriOnlineServer.java
│       │   ├── ClientHandler.java
│       │   ├── db/
│       │   └── service/
│       └── resources/
│           └── database.properties
│
├── frontend/                   # Renommage optionnel de "client"
│   ├── pom.xml                 # Dépend : common
│   └── src/main/java/.../client/
│       ├── ChriOnlineClient.java
│       ├── network/             # Seule couche qui parle au backend
│       │   └── NetworkClient.java
│       └── ui/                  # Uniquement affichage + appels NetworkClient
│           ├── LoginFrame.java
│           ├── MainFrame.java
│           └── ...
│
├── common/                     # Contrat partagé
│   └── src/main/java/.../common/
│       ├── Protocol.java
│       └── models/
│
├── database/                   # Lié au backend uniquement
│   └── schema_postgresql.sql
│
└── docs/
```

Vous pouvez garder les noms `server` et `client` dans le code et dans Maven ; l’important est de **ne jamais** mettre de logique métier ou d’accès BDD dans le client, et jamais d’UI dans le serveur.

### 2.4 Déploiement séparé

- **Backend** : une JVM, un processus.  
  - Commande : `cd server && mvn exec:java -Dexec.mainClass="com.chrionline.server.ChriOnlineServer"`  
  - Ou génération d’un JAR : `mvn package` dans `server/` → `server.jar` exécutable.
- **Frontend** : une autre JVM, un processus (ou une machine différente).  
  - Commande : `cd client && mvn exec:java -Dexec.mainClass="com.chrionline.client.ChriOnlineClient"`  
  - Le client se connecte à l’IP/port du serveur (ex. `localhost:12345`).

Les deux peuvent tourner sur des machines différentes : le frontend n’a besoin que de l’adresse et du port du backend.

### 2.5 Résumé (séparation stricte, toujours en Java + Sockets)

- **Frontend** = module client (IHM + `NetworkClient`), ne parle qu’au backend via le protocole TCP.
- **Backend** = module server (sockets, services, BDD), n’a pas d’IHM.
- **Contrat** = `common` (Protocol + modèles).
- Déploiement : 2 processus (ou 2 JAR), éventuellement 2 machines.

---

## 3. Option : frontend web (navigateur) + backend Java

Si par “séparer frontend du serveur” vous voulez un **frontend dans le navigateur** (HTML/CSS/JavaScript, React, Vue, etc.) au lieu du client Java desktop :

- Le **navigateur** ne peut pas ouvrir de **sockets TCP bruts** vers votre serveur (contraintes de sécurité et d’API du navigateur).
- Il faut donc que le **backend** expose une **API HTTP** (REST ou similaire) en plus (ou à la place) des sockets TCP.

### 3.1 Architecture cible (frontend web)

```
┌──────────────────┐         HTTP (REST)        ┌──────────────────┐         JDBC        ┌─────────────┐
│  FRONTEND WEB    │  ───────────────────────►  │  BACKEND JAVA    │  ─────────────────►  │  Base de    │
│  (navigateur)    │  ◄───────────────────────  │  (serveur HTTP    │                      │  données    │
│  React / Vue /   │      JSON                 │   + éventuellement│                      │             │
│  HTML/JS         │                           │   sockets TCP    │                      └─────────────┘
└──────────────────┘                           └──────────────────┘
```

### 3.2 Ce qu’il faut ajouter côté backend

1. **Une couche HTTP** qui expose des endpoints (API REST), par exemple :
   - `POST /api/auth/register`, `POST /api/auth/login`
   - `GET /api/products`, `GET /api/products/{id}`
   - `GET /api/cart`, `POST /api/cart`, `DELETE /api/cart/{productId}`
   - `POST /api/orders/validate`, `GET /api/orders`

2. **Technologies possibles en Java** :
   - **Spring Boot** (très courant) : contrôleurs REST, JSON, CORS.
   - **Jersey** ou **Jakarta REST** (JAX-RS).
   - Ou un petit serveur HTTP manuel (ex. `com.sun.net.httpserver`) si vous voulez rester en “pur” Java sans framework.

3. Les **Services** existants (AuthService, ProductService, CartService, OrderService) restent inchangés : la couche HTTP appelle ces services au lieu du `ClientHandler` socket.

### 3.3 Côté frontend web

- Un projet **séparé** (autre dossier, autre repo si vous voulez) :
  - **React**, **Vue**, **Angular**, ou simple **HTML/CSS/JS**.
- Le frontend appelle le backend en **HTTP** (fetch, axios, etc.) sur l’URL du backend (ex. `http://localhost:8080/api/...`).
- Plus de module Java “client” pour l’IHM : l’interface est entièrement dans le navigateur.

### 3.4 Structure possible (frontend web + backend Java)

```
chrionline-backend/             # Backend (Java)
├── pom.xml
├── src/main/java/.../
│   ├── ChriOnlineApplication.java   # Point d’entrée (ex. Spring Boot)
│   ├── api/                          # Contrôleurs REST
│   │   ├── AuthController.java
│   │   ├── ProductController.java
│   │   ├── CartController.java
│   │   └── OrderController.java
│   ├── service/                     # Réutilisation AuthService, etc.
│   └── db/
└── src/main/resources/
    └── application.properties

chrionline-frontend/            # Frontend (Web) – projet séparé
├── package.json               # npm / yarn
├── src/
│   ├── App.jsx                 # ou .vue / .ts
│   ├── pages/
│   │   ├── Login.jsx
│   │   ├── Catalog.jsx
│   │   ├── Cart.jsx
│   │   └── Checkout.jsx
│   └── services/
│       └── api.js              # appels HTTP vers le backend
└── ...
```

### 3.5 Attention au cahier des charges

- Le mini-projet impose **sockets TCP (et éventuellement UDP)** pour la communication client–serveur et une **interface desktop (Swing ou JavaFX)**.
- Une architecture **frontend web + API HTTP** est une **évolution** du projet : elle sépare bien frontend et backend, mais elle ne respecte plus à la lettre “client Java + sockets TCP” pour l’interface utilisateur.
- Si vous voulez rester 100 % conforme au cahier :
  - Gardez la **séparation logique** (client = frontend, server = backend) comme en section 2.
  - Vous pouvez **en plus** ajouter une API HTTP sur le serveur pour de futurs clients (web ou mobile), tout en gardant le client Java + TCP pour le rendu du projet.

---

## 4. En résumé

| Objectif | Comment faire |
|----------|----------------|
| **Séparer frontend et backend** (tout en restant en Java + Sockets) | Garder **client** = frontend (IHM + NetworkClient), **server** = backend (sockets, services, BDD). Ne pas mélanger logique métier/BDD dans le client ni IHM dans le serveur. Contrat = `common` (Protocol + modèles). Déployer les deux processus (ou JAR) séparément. |
| **Frontend vraiment “séparé” (web)** | Backend : ajouter une **API HTTP** (ex. Spring Boot) qui appelle les mêmes services. Frontend : projet **web** (React/Vue/HTML/JS) qui appelle cette API. Note : ça dépasse le strict cahier des charges (sockets + desktop). |

Pour le mini-projet tel qu’il est défini, la **section 2** suffit pour avoir une architecture qui sépare clairement le frontend (client) du serveur (backend), tout en restant conforme au cahier des charges.
