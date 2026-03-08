# Stack technologique – ChriOnline (application desktop)

**Conforme aux contraintes techniques du cahier des charges – Application desktop client-serveur.**

---

## 1. Langage

| Élément | Choix | Contrainte |
|--------|-------|------------|
| **Langage** | **Java** | Imposé (Java uniquement) |
| **Version** | **Java 11** (ou 17 LTS) | Compatible avec Socket, JDBC, Swing, JavaFX |

---

## 2. Réseau (communication client-serveur)

| Élément | Technologie | Usage |
|--------|-------------|--------|
| **Opérations principales** | **TCP – Sockets Java** | `java.net.Socket` (côté client), `java.net.ServerSocket` (côté serveur) |
| **Protocole** | Messages texte (chaînes) avec séparateur (ex. `\|`) | Format : `COMMANDE\|param1\|param2\|...` ; réponses `OK\|...` ou `ERROR\|...` |
| **Notifications (optionnel)** | **UDP – DatagramSocket** | `java.net.DatagramSocket`, `DatagramPacket` pour envoi/réception de notifications rapides (ex. confirmation de commande) |
| **Multi-clients** | **Threads Java** | Un `Thread` (ou `Runnable`) par client accepté ; `ExecutorService` possible pour le pool |

**Résumé :** TCP pour tout le métier (auth, panier, commandes, paiement) ; UDP uniquement pour les notifications si vous l’implémentez.

---

## 3. Base de données

| Élément | Choix | Contrainte |
|--------|-------|------------|
| **SGBD** | **Un parmi :** MySQL **ou** PostgreSQL **ou** SQLite | Imposé (un des trois) |
| **Accès** | **JDBC** (`java.sql.*`) | Imposé |
| **Driver** | Dépend du SGBD choisi : | |
| | • **PostgreSQL** : `org.postgresql:postgresql` | Ex. dans `server/pom.xml` |
| | • **MySQL** : `com.mysql:mysql-connector-j` | |
| | • **SQLite** : `org.xerial:sqlite-jdbc` | |
| **Connexion** | `Connection`, `PreparedStatement`, `ResultSet` | Pas d’ORM imposé ; SQL direct via JDBC |

**Tables attendues (schéma)** : utilisateurs, produits, catégories (optionnel), panier (lignes panier), commandes, lignes de commande, sessions utilisateurs (optionnel).

---

## 4. Interface graphique (application desktop)

| Élément | Choix | Contrainte |
|--------|-------|------------|
| **Type** | **Application desktop** | Imposé |
| **Framework UI** | **Swing** **ou** **JavaFX** (un des deux) | Imposé |
| **Swing** | `javax.swing.*` (JFrame, JPanel, JTable, JButton, etc.) | Inclus dans le JDK, aucun module additionnel |
| **JavaFX** | `javafx.*` (Stage, Scene, contrôleurs FXML) | À inclure comme dépendance si JDK 11+ (retiré du JDK standard) |

**Rôle de l’interface :** consulter les produits, gérer le panier, effectuer un achat (formulaire de paiement simulé). Pas de navigateur, pas de web.

---

## 5. Sécurité et sessions

| Élément | Technologie / approche | Contrainte |
|--------|------------------------|------------|
| **Authentification** | Login + mot de passe | Imposé |
| **Vérification** | Côté **serveur** uniquement | Le serveur vérifie identifiants (BDD), renvoie OK ou ERROR |
| **Stockage mot de passe** | Hash côté serveur (ex. **BCrypt**) avant stockage en BDD | Bonne pratique (recommandé) |
| **Sessions** | Identifiant utilisateur (ex. `userId`) renvoyé après LOGIN ; le client l’envoie dans chaque requête (panier, commande) | Gestion des sessions utilisateurs |
| **Optionnel** | Table `user_sessions` en BDD (token/session_id, user_id, expires_at) | Pour invalidation / expiration de session |

Aucun HTTPS imposé (communication TCP brute) ; pas de JWT ni de cookie navigateur (application desktop).

---

## 6. Build et exécution

| Élément | Choix | Rôle |
|--------|-------|------|
| **Build** | **Maven** | Compilation, dépendances, packaging |
| **Structure** | Projet multi-modules : `common`, `server`, `client` | Séparation protocole/modèles, backend, frontend desktop |
| **Exécution** | `mvn exec:java` ou JAR exécutables | Un processus pour le serveur, un pour le client |
| **Packaging** | `mvn package` → génération de JAR (optionnel) | Pour livrable « application exécutable » |

---

## 7. Gestion de versions et déploiement

| Élément | Choix | Contrainte / usage |
|--------|-------|---------------------|
| **VCS** | **Git** | Imposé (gestionnaire de versions) |
| **Hébergement** | **GitHub** ou **GitLab** | Imposé (outil de gestion de code) |
| **Déploiement** | Local ou serveur | Application exécutable (.jar) + BDD configurée et opérationnelle |

---

## 8. Récapitulatif de la stack (tout en un)

| Couche | Technologie |
|--------|-------------|
| **Langage** | Java 11 (ou 17) |
| **Réseau** | TCP : `Socket` / `ServerSocket` ; UDP (optionnel) : `DatagramSocket` |
| **Base de données** | MySQL **ou** PostgreSQL **ou** SQLite |
| **Accès données** | JDBC (`java.sql.*`) + driver du SGBD choisi |
| **Interface graphique** | **Swing** ou **JavaFX** (application desktop) |
| **Sécurité** | Login/mot de passe, vérification serveur, hash (ex. BCrypt), gestion sessions (userId / table sessions) |
| **Architecture** | Serveur multi-clients (threads) ; client Java connecté au serveur via socket |
| **Build** | Maven |
| **Versions** | Git (GitHub / GitLab) |

---

## 9. Ce qu’on n’utilise pas (hors contraintes)

Pour rester strictement dans le cahier des charges, on **n’utilise pas** :

- HTTP / REST / serveur web (la communication est en **sockets TCP/UDP**).
- Frontend web (HTML, React, Vue, etc.) : l’interface est **desktop** (Swing ou JavaFX).
- ORM (Hibernate, JPA) : accès BDD via **JDBC** uniquement.
- Spring (ou autre framework) : pas imposé ; possible en évolution, mais pas requis par le cahier.
- Base NoSQL : uniquement **MySQL, PostgreSQL ou SQLite**.
- Autre langage que **Java** pour le client et le serveur.

Cette stack respecte entièrement les contraintes techniques du cahier des charges pour une application desktop ChriOnline.
