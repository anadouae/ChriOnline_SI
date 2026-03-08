# Comment réaliser les maquettes dans le frontend ChriOnline

Ce document décrit **comment mettre en place** les maquettes (designs fournis) dans votre application desktop Java. **Aucune modification de code n’est faite tant que vous n’avez pas donné votre accord.**

---

## 1. Correspondance maquettes ↔ écrans actuels

| Maquette | Écran à réaliser | Existant ou à créer |
|----------|-------------------|---------------------|
| Landing page (hero, « Pourquoi ChriOnline », stats, Comment ça marche) | Page d’accueil avant connexion | **À créer** (optionnel : ou aller direct au login) |
| Connexion (email, mot de passe, « Créer un compte », comptes de test) | Écran de login | **Existant** : `LoginFrame` – à adapter au design |
| Inscription (nom, email, mot de passe, « Déjà inscrit ? Connexion ») | Écran d’inscription | **Existant** : même `LoginFrame` (onglet/carte inscription) – à adapter |
| Catalogue (liste produits + détail à droite, onglets Catalogue / Panier / Mes commandes) | Catalogue + détail produit | **Existant** : `MainFrame` + `ProductListPanel` – à adapter (ajouter onglet « Mes commandes », mise en page 2 colonnes) |
| Mon panier (tableau Produit, Quantité, Prix, Total, retirer, valider commande) | Panier | **Existant** : `CartPanel` – à adapter (colonnes, badge nombre, boutons) |
| Paiement simulé (modal : montant, numéro carte, expiration, CVV, Valider/Annuler) | Dialogue paiement | **Existant** dans `CartPanel.doCheckout()` – à remplacer par une **JDialog** dédiée au design de la maquette |
| Mes commandes (tableau ID, Date, Total, Statut) | Historique des commandes | **À créer** : nouveau panel + onglet dans `MainFrame` |
| Admin – Tableau de bord (KPIs, dernières commandes) | Dashboard admin | **À créer** : fenêtre ou panel admin |
| Admin – Gestion des produits (table + « Ajouter un produit ») | Liste produits admin | **À créer** : panel + table avec colonnes ID, Nom, Description, Prix, Stock, Catégorie, Actions |
| Admin – Modal « Ajouter un produit » | Formulaire ajout produit | **À créer** : `JDialog` avec champs Nom, Description, Prix, Stock, Catégorie |
| Admin – Modal « Modifier le produit » | Formulaire modification produit | **À créer** : même type de dialog, pré-rempli |
| Admin – Gestion des commandes (table + « Changer statut ») | Liste commandes admin | **À créer** : panel + table ID, Client, Date, Total, Statut, Actions |
| Admin – Modal « Changer le statut » (liste déroulante statuts, détail commande) | Dialogue changement statut | **À créer** : `JDialog` avec infos commande + ComboBox statuts + Mettre à jour |
| Admin – Gestion des utilisateurs (table ID, Nom, Email, Rôle, Date, Actions) | Liste utilisateurs admin | **À créer** : panel + table + action Modifier (rôle) |
| Catalogue vu en tant qu’admin (« Bonjour, Admin », lien Administration) | Même catalogue avec barre admin | **Adapter** : selon rôle (admin), afficher « Administration » et lien vers l’interface admin |

---

## 2. Stack et composants à utiliser (Swing)

Vous êtes déjà en **Swing**. Pour coller aux maquettes :

- **Fenêtres** : `JFrame` (écrans principaux), `JDialog` (modales : paiement, ajout/modif produit, changement statut).
- **Mise en page** : `BorderLayout`, `GridBagLayout`, `FlowLayout`, `BoxLayout`, éventuellement `JSplitPane` (catalogue | détail produit).
- **Listes tabulaires** : `JTable` + `DefaultTableModel` (catalogue, panier, commandes, produits admin, utilisateurs).
- **Formulaires** : `JTextField`, `JPasswordField`, `JSpinner` (quantité), `JComboBox` (catégorie, statut), `JTextArea` (description).
- **Boutons** : `JButton` ; pour « lien » : `JButton` sans bordure ou `JLabel` + curseur main.
- **Onglets** : `JTabbedPane` (Catalogue, Mon panier, Mes commandes ; côté admin : Tableau de bord, Produits, Commandes, Utilisateurs).
- **Couleurs / polices** : `setBackground`, `setForeground`, `setFont` sur les composants ; pour les « pastilles » de statut (Livrée, En attente, etc.), un `JLabel` avec fond coloré et bordures arrondies (ou rendu personnalisé dans la table).

Aucun changement de stack : tout reste en **Swing** (pas de passage à JavaFX obligatoire).

---

## 3. Ordre de réalisation recommandé

Sans toucher au code tant que vous n’avez pas dit OK, voici l’ordre logique pour **quand** vous déciderez d’implémenter :

1. **Connexion et Inscription**  
   Adapter `LoginFrame` : même disposition que les maquettes (carte blanche centrée, champs, boutons, lien « Créer un compte » / « Déjà inscrit ? »). Option : bloc « Comptes de test » en bas.

2. **En-tête commun (client)**  
   Barre haute avec logo « ChriOnline », à droite « Bonjour, [Nom] » et « Déconnexion ». Mettre cette barre dans `MainFrame` (au nord), pour que Catalogue, Panier et Mes commandes aient le même header.

3. **Onglets client**  
   Dans `MainFrame`, garder Catalogue et Mon panier, **ajouter** l’onglet « Mes commandes » (panel vide d’abord, puis tableau alimenté par `GET_ORDERS`).

4. **Catalogue**  
   Dans `ProductListPanel` :  
   - Gauche : tableau (ID, Nom, Prix, Stock) comme sur la maquette.  
   - Droite : panneau « Détail du produit » (nom, prix, description, catégorie, stock, quantité, « Ajouter au panier »). Utiliser un `JSplitPane` ou deux panels côte à côte.

5. **Panier**  
   Dans `CartPanel` : colonnes comme sur la maquette (Produit, Quantité, Prix unitaire, Total), icône poubelle ou bouton « Retirer », total en bas, bouton « Valider la commande (paiement) ». Option : badge avec le nombre d’articles sur l’onglet « Mon panier ».

6. **Modal Paiement**  
   Créer une classe `PaiementDialog extends JDialog` : titre « Paiement simulé », montant, champs carte (numéro, MM/AA, CVV), boutons Annuler / Valider. Dans `CartPanel.doCheckout()`, ouvrir ce dialog au lieu du `JOptionPane` actuel ; à la validation, envoyer `VALIDATE_ORDER` comme aujourd’hui.

7. **Mes commandes**  
   Nouveau `OrdersPanel` : `JTable` avec colonnes ID commande, Date, Total, Statut. Remplir avec la réponse de `GET_ORDERS`. Afficher le statut avec des couleurs (ex. vert « Livrée », jaune/orange « En attente »).

8. **Landing page (optionnel)**  
   Si vous voulez la page d’accueil (hero, « Pourquoi ChriOnline », Comment ça marche, etc.) : nouveau `JFrame` ou premier panel affiché au démarrage, avec boutons « Se connecter » / « Créer un compte » / « Commencer maintenant » qui ouvrent `LoginFrame`. Sinon, garder le flux actuel : lancement → `LoginFrame`.

9. **Interface admin**  
   - Détecter le rôle après login (ex. champ dans la réponse `LOGIN` : client vs admin).  
   - Si admin : dans la barre du `MainFrame` client, afficher un lien/bouton « Administration » qui ouvre une **nouvelle fenêtre** `AdminFrame`.  
   - `AdminFrame` : même header « ChriOnline - Administration », « Admin », « Déconnexion », lien « Voir le catalogue client ».  
   - Onglets admin : Tableau de bord, Produits, Commandes, Utilisateurs.  
   - **Tableau de bord** : 4 cartes (KPIs) + tableau « Dernières commandes ».  
   - **Produits** : `JTable` (ID, Nom, Description, Prix, Stock, Catégorie, Actions) + bouton « + Ajouter un produit ». Actions = icônes ou boutons Modifier / Supprimer.  
   - **Dialogs produit** : `AjouterProduitDialog` et `ModifierProduitDialog` (JDialog avec Nom, Description, Prix, Stock, Catégorie ; Annuler / Enregistrer).  
   - **Commandes** : `JTable` (ID, Client, Date, Total, Statut, Actions) avec bouton « Changer statut ».  
   - **Dialog statut** : `ChangerStatutDialog` (JDialog) : infos commande, liste des articles, ComboBox « Nouveau statut » (En attente, Validée, Expédiée, Livrée, Annulée), Annuler / Mettre à jour.  
   - **Utilisateurs** : `JTable` (ID, Nom, Email, Rôle, Date d’inscription, Actions) avec action Modifier (ex. changer le rôle).

10. **Protocole côté serveur**  
    Pour admin et « Mes commandes », le serveur doit exposer (si pas déjà fait) :  
    - `GET_ORDERS|userId`  
    - Pour admin : commandes pour modifier le statut, liste des produits (CRUD), liste des utilisateurs, etc. Selon votre `PROTOCOLE.md`, ajouter les commandes nécessaires et les implémenter côté serveur.

---

## 4. Détails utiles pour coller aux maquettes

- **Logo** : utiliser un `JLabel` avec une `ImageIcon` (icône sac / boîte bleue) à côté du texte « ChriOnline » dans chaque header.  
- **Couleurs** : bleu foncé pour les boutons principaux et l’onglet actif ; fond gris très clair pour les cartes ; vert pour « Livrée », jaune/orange pour « En attente ».  
- **Pastilles de statut** : dans la cellule de la table, afficher un `JLabel` avec texte + fond coloré (ou personnaliser le rendu de la cellule avec un `DefaultTableCellRenderer`).  
- **Modales** : fond blanc/gris clair, titre en gras, bouton X en haut à droite (fermer), boutons en bas (Annuler à gauche, Valider/Enregistrer/Mettre à jour à droite en style primaire).  
- **Responsive** : en desktop fixe, prévoir des tailles minimales de fenêtre (ex. 900×600) pour que les deux colonnes catalogue/détail restent lisibles.

---

## 5. Fichiers à créer ou modifier (quand vous donnerez le feu vert)

- **À adapter** : `LoginFrame`, `MainFrame`, `ProductListPanel`, `CartPanel` (et la partie checkout pour utiliser une JDialog).  
- **À créer** :  
  - `OrdersPanel` (Mes commandes),  
  - `PaiementDialog`,  
  - `AdminFrame`,  
  - panels admin (Tableau de bord, Produits, Commandes, Utilisateurs),  
  - `AjouterProduitDialog`, `ModifierProduitDialog`, `ChangerStatutDialog`,  
  - optionnel : écran Landing (ou premier panel au démarrage).

---

## 6. Résumé

- **Maquettes** : vous avez une landing, login, inscription, catalogue (liste + détail), panier, paiement simulé (modal), Mes commandes, et toute l’interface admin (dashboard, produits avec ajout/modif, commandes avec changement de statut, utilisateurs).  
- **Réalisation** : tout se fait en **Swing** (JFrame, JDialog, JTable, JTabbedPane, etc.) dans le module **client**, sans changer la stack.  
- **Aucune modification de code** n’est faite tant que vous n’avez pas donné votre **OK**.  
- Quand vous direz « OK », on pourra détailler écran par écran (ou par lot : client puis admin) et proposer les modifications de code concrètes.

Si vous voulez, on peut commencer par un seul écran (par ex. connexion ou catalogue) et je vous détaillerai les changements ligne par ligne après votre accord.
