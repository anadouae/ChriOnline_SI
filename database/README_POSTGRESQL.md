# Base de données ChriOnline – PostgreSQL

## Prérequis

- PostgreSQL installé (par ex. https://www.postgresql.org/download/)
- Client `psql` ou outil type pgAdmin, DBeaver, etc.

## Création de la base

```bash
# Se connecter à PostgreSQL (utilisateur postgres par défaut)
psql -U postgres

# Dans psql :
CREATE DATABASE chrionline;
\c chrionline

# Exécuter le schéma (depuis le dossier database du projet)
\i schema_postgresql.sql
```

Sous Windows (PowerShell) depuis le dossier `ChriOnline` :

```powershell
psql -U postgres -d chrionline -f database\schema_postgresql.sql
```

(En créant d’abord la base `chrionline` si besoin.)

## Configuration du serveur

Dans `server/src/main/resources/database.properties` :

- `db.url=jdbc:postgresql://localhost:5432/chrionline`
- `db.user=postgres`
- `db.password=` (ou votre mot de passe)

Le driver JDBC PostgreSQL est déjà déclaré dans `server/pom.xml`.
