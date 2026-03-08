# Travailler sur le serveur en binôme sans s’influencer

Ce document explique comment **répartir le travail serveur entre deux personnes** pour que chacune code **ses parties sans modifier les fichiers de l’autre**, et pour limiter les conflits Git.

---

## 1. Principe : découper par « blocs » et une interface commune

- On garde **un seul serveur** (une JVM, un `ServerSocket`).
- On sépare le serveur en **deux blocs métier** :
  - **Bloc A : Auth + Produits** (utilisateurs, connexion, catalogue).
  - **Bloc B : Panier + Commandes** (panier, validation commande, paiement simulé, historique).
- On définit **une fois ensemble** :
  - le **protocole** (PROTOCOLE.md, `common/Protocol.java`) ;
  - le **dispatcher** : un fichier qui reçoit la ligne, regarde la commande (REGISTER, LOGIN, GET_PRODUCTS, ADD_TO_CART, etc.) et appelle le bon « handler ».
- Chaque personne **n’écrit que** :
  - ses **handlers** (classes qui traitent ses commandes),
  - ses **services** (logique métier),
  - sa **partie BDD** (tables + requêtes).

Comme ça, vous travaillez en parallèle sur des **fichiers différents** et vous ne vous marchez pas dessus.

---

## 2. Répartition recommandée

| Qui | Bloc | Fichiers / responsabilités | Commandes protocole |
|-----|------|----------------------------|----------------------|
| **Personne 1** | Auth + Produits | `AuthService`, `ProductService`, handlers Auth/Produits, tables `users`, `user_sessions`, `categories`, `products` | REGISTER, LOGIN, LOGOUT, GET_PRODUCTS, GET_PRODUCT |
| **Personne 2** | Panier + Commandes | `CartService`, `OrderService`, handlers Panier/Commandes, tables `cart_items`, `orders`, `order_items` | ADD_TO_CART, REMOVE_FROM_CART, GET_CART, VALIDATE_ORDER, GET_ORDERS |

**Partagé (à faire une fois, ou une seule personne responsable)** :

- `ChriOnlineServer` : boucle `accept()`, crée un `ClientHandler` par client.
- `ClientHandler` : lit une ligne, appelle le **dispatcher** (voir ci‑dessous).
- `DatabaseManager` : uniquement `getConnection()` (ou équivalent). Pas de logique métier dedans.
- Schéma BDD : un fichier SQL par bloc (ex. `schema_auth_products.sql` et `schema_cart_orders.sql`) ou une convention claire (Personne 1 = users, products, categories ; Personne 2 = cart_items, orders, order_items).

---

## 3. Éviter le conflit sur `ClientHandler` : dispatcher + handlers

Aujourd’hui, `ClientHandler` contient un gros `switch` avec toutes les commandes. Si les deux modifient ce fichier, vous aurez des conflits Git.

**Solution : dispatcher + une classe « handler » par bloc.**

### 3.1 Interface commune (une fois, dans `server`)

Créer une interface que chaque bloc implémente :

```java
// server/handler/RequestHandler.java (créé une fois)
package com.chrionline.server.handler;

public interface RequestHandler {
    /** Retourne true si ce handler gère la commande. */
    boolean accepts(String command);
    /** Traite la requête et retourne la réponse (OK|... ou ERROR|...). */
    String handle(String command, String[] parts);
}
```

### 3.2 ClientHandler = dispatcher (un seul fichier, modifié une fois)

Au lieu d’un gros `switch`, le `ClientHandler` :

1. Reçoit la ligne, la découpe avec `Protocol.SEPARATOR`.
2. Prend le premier token (la commande).
3. Parcourt une **liste de handlers** enregistrés et appelle le premier pour lequel `accepts(command)` est vrai.
4. Envoie la réponse au client.

Exemple (à adapter à votre package) :

```java
// Dans ClientHandler : liste fournie au constructeur
private final List<RequestHandler> handlers;

public ClientHandler(Socket socket, List<RequestHandler> handlers) {
    this.socket = socket;
    this.handlers = handlers;
}

private String processRequest(String request) {
    String[] parts = request.split("\\" + Protocol.SEPARATOR, -1);
    if (parts.length == 0) return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
    String cmd = parts[0].toUpperCase();
    for (RequestHandler h : handlers) {
        if (h.accepts(cmd)) return h.handle(cmd, parts);
    }
    return Protocol.ERROR + Protocol.SEPARATOR + "UNKNOWN_COMMAND";
}
```

L’enregistrement des handlers se fait au démarrage (dans `ChriOnlineServer` ou une classe `Main`), par exemple :

```java
List<RequestHandler> handlers = new ArrayList<>();
handlers.add(new AuthRequestHandler(authService));   // Personne 1
handlers.add(new ProductRequestHandler(productService)); // Personne 1
handlers.add(new CartRequestHandler(cartService));   // Personne 2
handlers.add(new OrderRequestHandler(orderService)); // Personne 2
// puis passer handlers au ClientHandler
```

Comme ça :

- **Personne 1** crée uniquement `AuthRequestHandler` et `ProductRequestHandler` (et ses services).
- **Personne 2** crée uniquement `CartRequestHandler` et `OrderRequestHandler` (et ses services).
- Le fichier **ClientHandler** et la **liste des handlers** sont mis en place une fois (ou une personne « chef de serveur » les maintient). Ensuite, chaque binôme n’ajoute qu’**une ou deux lignes** pour enregistrer ses handlers, ce qui limite les conflits.

---

## 4. Base de données : ne pas se marcher dessus

- **Option simple : un seul fichier de schéma**
  - Découper le SQL en deux parties claires (commentaires `-- Bloc A` / `-- Bloc B`).
  - Personne 1 écrit tout ce qui touche `users`, `user_sessions`, `categories`, `products`.
  - Personne 2 écrit tout ce qui touche `cart_items`, `orders`, `order_items`.
  - Vous évitez de modifier les mêmes `CREATE TABLE` en même temps.

- **Option plus propre : deux fichiers**
  - `schema_auth_products.sql` (Personne 1) : users, user_sessions, categories, products.
  - `schema_cart_orders.sql` (Personne 2) : cart_items, orders, order_items (avec clés étrangères vers users et products).
  - Au déploiement, exécuter les deux dans l’ordre.

- **DatabaseManager**
  - Une seule classe, avec uniquement `getConnection()` (et éventuellement lecture de `database.properties`). Pas de requêtes métier dedans.
  - Chaque personne utilise cette connexion dans **ses** services / DAO. Personne 1 n’écrit pas dans les méthodes de panier/commandes ; Personne 2 n’écrit pas dans les méthodes auth/products.

---

## 5. Ordre de travail pour ne pas se bloquer

1. **En commun (dès le début)**  
   - Valider le protocole (PROTOCOLE.md + `Protocol.java`).  
   - Créer `RequestHandler`, le nouveau `ClientHandler` (dispatcher) et l’enregistrement des handlers dans `ChriOnlineServer`.  
   - Créer `DatabaseManager.getConnection()` et la structure des tables (au moins les noms et les FK).

2. **Personne 1 (Auth + Produits)**  
   - Implémenter `AuthService`, `ProductService`.  
   - Implémenter `AuthRequestHandler`, `ProductRequestHandler` (avec `accepts` + `handle`).  
   - Créer / maintenir les tables users, user_sessions, categories, products.  
   - Enregistrer ses deux handlers au démarrage.

3. **Personne 2 (Panier + Commandes)**  
   - Implémenter `CartService`, `OrderService` (en utilisant `DatabaseManager.getConnection()` et les tables users/products déjà définies).  
   - Implémenter `CartRequestHandler`, `OrderRequestHandler`.  
   - Créer / maintenir les tables cart_items, orders, order_items.  
   - Enregistrer ses deux handlers au démarrage.

Vous pouvez avancer **en parallèle** dès que le dispatcher et le contrat (protocole + `RequestHandler`) sont en place.

---

## 6. Récap : qui touche quoi

| Fichier / zone | Personne 1 | Personne 2 |
|----------------|------------|------------|
| `ChriOnlineServer` | Oui (une fois : boucle + enregistrement des handlers) | Non (sauf ajout de ses 2 lignes d’enregistrement) |
| `ClientHandler` | Oui (une fois : dispatcher) | Non |
| `RequestHandler` (interface) | Oui (une fois) | Non |
| `AuthService`, `AuthRequestHandler` | Oui | Non |
| `ProductService`, `ProductRequestHandler` | Oui | Non |
| `CartService`, `CartRequestHandler` | Non | Oui |
| `OrderService`, `OrderRequestHandler` | Non | Oui |
| `DatabaseManager` | Oui (getConnection uniquement) | Non (elle l’utilise) |
| Tables users, products, categories | Oui | Non |
| Tables cart_items, orders, order_items | Non | Oui |

En suivant cette répartition, vous travaillez sur le **côté serveur en binôme** avec un minimum d’influence l’un sur l’autre et peu de conflits Git (surtout au moment d’ajouter les lignes d’enregistrement des handlers).
