# CHRIONLINE-PROJECT--SI (ChriOnline v1)

Application e-commerce **complète** : **serveur**, **base de données**, **client** (interface client + admin).  
Conforme au cahier des charges (Mini-Projet 1 – Mod. SSI-AT 2026).

## Structure du projet

- **common/** – Protocole et modèles partagés (client + serveur)
- **server/** – Serveur TCP, multi-clients, handlers (Auth, Product, Cart, Order), JDBC
- **client/** – Interface desktop Swing (accueil, connexion, espace client + espace admin)
- **database/** – Scripts SQL (PostgreSQL, MySQL, SQLite)
- **docs/** – Documentation, protocole, répartition des tâches, maquettes, UML

Voir **STRUCTURE_PROJET.md** pour l’arborescence détaillée.

## Lancer le projet

**Compilation :**
```bash
cd ChriOnline-v1
mvn clean install
```

**Serveur :**
```bash
cd server
mvn exec:java -Dexec.mainClass="com.chrionline.server.ChriOnlineServer"
```

**Application (client + admin) :**
```bash
cd client
mvn exec:java -Dexec.mainClass="com.chrionline.app.ChriOnlineApp"
```

**Base de données :** créer la base (ex. PostgreSQL), exécuter `database/schema_postgresql.sql`, configurer `server/src/main/resources/database.properties`.

## Comptes de test (mode mock)

- **Client :** `jean@example.com` (mot de passe : 1234)
- **Admin :** `admin@chrionline.com` (mot de passe : 1234)

L’interface client utilise par défaut des données mock (`ApiService`). Pour utiliser le vrai serveur, remplacer par des appels TCP (protocole dans `common/Protocol.java`).
