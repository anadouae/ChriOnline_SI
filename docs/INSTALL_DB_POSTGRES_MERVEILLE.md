## Installation de la base PostgreSQL – Merveille

Objectif : que **Merveille** ait exactement la même base `chrionline` que **Douae**.

### 1. Prérequis

- PostgreSQL installé (même version ou proche de celle de Douae).
- Accès à un compte PostgreSQL (souvent `postgres`).
- Le projet **ChriOnline** copié sur la machine de Merveille (même structure de dossiers).

### 2. Récupérer le projet ChriOnline

1. Copier le dossier `ChriOnline` de Douae vers la machine de Merveille  
   (clé USB, partage réseau, Git, etc.).
2. Vérifier que le fichier suivant existe bien :
   - `ChriOnline/database/schema_postgresql.sql`

### 3. Créer la base `chrionline`

1. Ouvrir un terminal (PowerShell ou CMD).
2. Se placer dans le dossier du projet :

   ```powershell
   cd C:\Users\<UTILISATEUR>\Desktop\ChriOnline
   ```

3. Créer la base vide :

   ```powershell
   psql -U postgres -c "CREATE DATABASE chrionline;"
   ```

   > Adapter l’utilisateur (`postgres`) si besoin.

### 4. Appliquer le schéma PostgreSQL

Toujours depuis le dossier `ChriOnline` :

```powershell
psql -U postgres -d chrionline -f database\schema_postgresql.sql
```

- Cette commande crée :
  - les types (`user_role`, `order_status`),
  - les tables (`users`, `products`, `categories`, `cart_items`, `orders`, `order_items`, `user_sessions`),
  - les index,
  - les données de test (admin par défaut, produits, etc.).

### 5. Vérifier la connexion depuis le serveur Java

1. Ouvrir le fichier :

   - `server/src/main/resources/database.properties`

2. Vérifier / adapter les lignes suivantes :

   ```properties
   db.url=jdbc:postgresql://localhost:5432/chrionline
   db.user=postgres
   db.password=VOTRE_MOT_DE_PASSE
   ```

3. Lancer le serveur depuis Merveille pour vérifier que la connexion à PostgreSQL fonctionne.

### 6. Résumé

- Douae et Merveille utilisent le **même script** `schema_postgresql.sql`.
- Si les deux machines exécutent ce script sur une base `chrionline`, la structure de la base et les données initiales sont identiques sur les deux postes.

