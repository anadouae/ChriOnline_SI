# Protocole de communication ChriOnline

Format : `COMMANDE|param1|param2|...`

Séparateur : `|`

---

## Utilisateurs

### REGISTER
- Requête : `REGISTER|email|password|nom`
- Réponse OK : `OK|Inscription réussie`
- Réponse KO : `ERROR|message`

### LOGIN
- Requête : `LOGIN|email|password`
- Réponse OK : `OK|userId|nom`
- Réponse KO : `ERROR|message`

### LOGOUT
- Requête : `LOGOUT|userId`
- Réponse : `OK`

---

## Produits

### GET_PRODUCTS
- Requête : `GET_PRODUCTS`
- Réponse : `OK|id1|nom1|prix1|stock1|description1||id2|nom2|...`

### GET_PRODUCT
- Requête : `GET_PRODUCT|productId`
- Réponse : `OK|id|nom|prix|stock|description`
- Réponse KO : `ERROR|message`

---

## Panier

### ADD_TO_CART
- Requête : `ADD_TO_CART|userId|productId|quantity`
- Réponse : `OK` ou `ERROR|message`

### REMOVE_FROM_CART
- Requête : `REMOVE_FROM_CART|userId|productId`
- Réponse : `OK` ou `ERROR|message`

### GET_CART
- Requête : `GET_CART|userId`
- Réponse : `OK|productId1|qty1|prix1||productId2|qty2|prix2||total`

---

## Commande et Paiement

### VALIDATE_ORDER
- Requête : `VALIDATE_ORDER|userId|cardNumber|cardExpiry|cvv`
- Réponse OK : `OK|orderId`
- Réponse KO : `ERROR|message`

### GET_ORDERS
- Requête : `GET_ORDERS|userId`
- Réponse : `OK|orderId1|date1|total1||orderId2|...`

---

## Codes d'erreur courants

- `ERROR|AUTH_FAILED` - Authentification échouée
- `ERROR|PRODUCT_NOT_FOUND` - Produit introuvable
- `ERROR|OUT_OF_STOCK` - Stock insuffisant
- `ERROR|INVALID_DATA` - Données invalides
