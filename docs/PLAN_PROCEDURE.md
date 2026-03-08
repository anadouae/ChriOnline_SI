# Plan de procédure – ChriOnline

**Équipe : Douae et Merveille**  
**Base de données : PostgreSQL**

---

## 1. Ordre de travail recommandé

### Phase 0 – Préparation (ensemble, 1 jour)
- [ ] Dépôt Git (GitHub/GitLab) créé et cloné en local
- [ ] Structure Maven à jour : `common`, `server`, `client`
- [ ] **Protocole validé ensemble** : [PROTOCOLE.md](PROTOCOLE.md) et `Protocol.java` dans `common/`
- [ ] PostgreSQL installé, base `chrionline` créée, schéma exécuté : `database/schema_postgresql.sql`

### Phase 1 – Niveau minimum (semaines 1–3)

| Étape | Qui | Quoi | Livrable |
|-------|-----|------|----------|
| 1.1 | **Douae** | Serveur TCP (ServerSocket, threads), connexion JDBC PostgreSQL | Serveur qui accepte les connexions |
| 1.2 | **Douae** | Auth (REGISTER, LOGIN, LOGOUT) + tables `users` | Authentification fonctionnelle |
| 1.3 | **Douae** | Produits (GET_PRODUCTS, GET_PRODUCT) | Catalogue consultable |
| 1.4 | **Douae** | Panier (ADD_TO_CART, REMOVE_FROM_CART, GET_CART) | Panier côté serveur |
| 1.5 | **Douae** | Commandes + paiement simulé (VALIDATE_ORDER), ID unique | Validation commande OK |
| 2.1 | **Merveille** | Client socket + envoi/réception messages (protocole) | Client qui parle au serveur |
| 2.2 | **Merveille** | Écrans login / inscription | IHM connexion |
| 2.3 | **Merveille** | Catalogue produits (liste + détail) | IHM produits |
| 2.4 | **Merveille** | Panier (ajout, suppression, total) | IHM panier |
| 2.5 | **Merveille** | Validation commande + formulaire paiement fictif | IHM checkout |

### Phase 2 – Niveau avancé (semaines 4–6)

| Étape | Qui | Quoi |
|-------|-----|------|
| 3.1 | **Douae** | Mise à jour automatique du stock après commande |
| 3.2 | **Douae** | GET_ORDERS (historique commandes) |
| 3.3 | **Douae** | Rôles (CLIENT/ADMIN), préparation API pour l’admin |
| 3.4 | **Douae** | Notifications UDP (optionnel) |
| 4.1 | **Merveille** | Interface complète et ergonomique (Swing/JavaFX) |
| 4.2 | **Merveille** | Historique des commandes côté client |
| 4.3 | **Merveille** | Interface administrateur (gestion produits, commandes, utilisateurs) |

---

## 2. Déploiement (cahier des charges §6)

1. **Gestion de code** : GitHub ou GitLab, branches par fonctionnalité si besoin
2. **Base de données** : PostgreSQL configurée et opérationnelle (schéma + données de test)
3. **Exécution** : application livrable en `.jar` (client + serveur) ou double `.jar`
4. **Environnement** : déploiement local ou sur un serveur (IP/port documentés)

---

## 3. Points de synchronisation (sans tâches partagées)

- **Début** : validation du protocole (PROTOCOLE.md + constantes dans `common`) → chacun code de son côté selon ce contrat.
- **Milieu phase 1** : première intégration client/serveur (Douae livre un serveur testable, Merveille branche le client dessus).
- **Fin phase 1** : démo niveau minimum (inscription → connexion → produits → panier → commande).
- **Fin phase 2** : démo niveau avancé (stocks, historique, admin).

Aucune tâche n’est attribuée à deux personnes en même temps : **Douae = serveur + BDD**, **Merveille = client + IHM**.

---

## 4. Outils et méthodologie

- **Modélisation UML** : diagramme de classes (déjà dans `docs/uml/`)
- **Suivi d’avancement** : tableau dans REPARTITION_TACHES.md ou tableau type Kanban
- **Contrôle de version** : Git (commits réguliers, messages clairs)

---

## 5. Ce que l’assistant (IA) peut faire pour le projet

- **Documentation** : rédiger ou mettre à jour le plan, la répartition des tâches, le protocole, les README.
- **Base de données** : fournir le schéma PostgreSQL (`schema_postgresql.sql`), les requêtes SQL, et la config JDBC.
- **Code serveur (pour Douae)** : proposer ou compléter le code Java (ServerSocket, ClientHandler, gestion des commandes protocole, DAO JDBC pour users, products, cart, orders).
- **Code client (pour Merveille)** : proposer ou compléter le code Java (connexion socket, envoi/réception selon le protocole, écrans Swing/JavaFX).
- **Module common** : maintenir `Protocol.java` et les modèles partagés (User, Product, etc.) pour rester aligné avec le protocole.
- **Dépannage** : aider à corriger erreurs de compilation, de connexion BDD, ou de communication client/serveur.

Chacune peut demander de l’aide sur sa partie (serveur ou client) sans bloquer l’autre.

---

## 6. Résumé

1. Valider le protocole ensemble.
2. Installer PostgreSQL et exécuter `schema_postgresql.sql`.
3. Douae : serveur + BDD + auth + produits + panier + commandes (niveau min puis avancé).
4. Merveille : client socket + toutes les écrans (login, catalogue, panier, commande, puis admin).
5. Intégrer dès que le serveur répond correctement au protocole.
6. Livrer : BDD opérationnelle + JAR(s) + (optionnel) notifications UDP.
