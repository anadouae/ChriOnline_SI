# ChriOnline – Descriptions des maquettes pour Figma / Figma AI

Ce document décrit **toutes les pages** de l’application e-commerce ChriOnline. Vous pouvez copier chaque description (ou le « Prompt Figma AI ») dans Figma pour générer des maquettes ou wireframes.

---

## Style global (recommandations)

- **Application :** Desktop (client lourd Java Swing), e-commerce.
- **Ton :** sobre, professionnel, lisible. Couleur d’accent possible : bleu ou vert (confiance, achat).
- **Typographie :** titres clairs, corps lisible (équivalent 14–16px pour le texte principal).
- **Zones :** en-tête (header), zone principale (content), pied de page ou barre d’actions si besoin.

---

## 1. Page de connexion (Login)

**Écran :** Fenêtre de connexion, premier écran au lancement.

**Disposition :**
- Centré, fenêtre modale ou frame d’environ 400×320 px.
- Titre en haut : « ChriOnline » ou « Connexion ».
- Formulaire centré avec champs en colonne.
- En bas : lien ou bouton vers l’inscription.

**Éléments :**
- Titre : « Connexion » (ou « ChriOnline - Connexion »).
- Champ **Email** (texte, une ligne).
- Champ **Mot de passe** (masqué).
- Bouton **« Se connecter »** (principal).
- Lien / bouton secondaire : **« Créer un compte »** (bas de page).

**Prompt Figma AI :**
> Maquette desktop : page de connexion e-commerce « ChriOnline ». Fenêtre centrée 400x320. Titre « Connexion », champ Email, champ Mot de passe, bouton « Se connecter », en bas lien « Créer un compte ». Style sobre et professionnel.

---

## 2. Page d’inscription (Register)

**Écran :** Inscription d’un nouveau client (même fenêtre que la connexion, vue alternée).

**Disposition :**
- Même taille que la connexion (400×320 px), même fenêtre.
- Titre « Inscription » ou « Créer un compte ».
- Formulaire en colonne.
- En bas : lien « Déjà inscrit ? Connexion ».

**Éléments :**
- Titre : « Inscription ».
- Champ **Nom** (ou Prénom/Nom).
- Champ **Email**.
- Champ **Mot de passe** (masqué).
- Bouton **« S’inscrire »** (ou « Créer mon compte »).
- Lien en bas : **« Déjà inscrit ? Connexion »**.

**Prompt Figma AI :**
> Maquette desktop : page d’inscription e-commerce. Fenêtre 400x320. Titre « Inscription », champs Nom, Email, Mot de passe, bouton « S’inscrire », lien « Déjà inscrit ? Connexion » en bas. Style cohérent avec une page de connexion.

---

## 3. Accueil – Catalogue produits

**Écran :** Vue principale après connexion : liste des produits + détail.

**Disposition :**
- **En-tête** (barre haute) : logo ou titre « ChriOnline », à droite « Bonjour, [Nom] » et bouton **« Déconnexion »**.
- **Onglets** sous l’en-tête : « Catalogue » (actif), « Mon panier ».
- **Zone principale :**
  - À gauche (ou en haut) : **tableau des produits** (colonnes : ID, Nom, Prix, Stock, Description ou au moins Nom, Prix, Stock).
  - À droite (ou en dessous) : zone **détail du produit** (visible quand une ligne est sélectionnée) : nom, prix, description, stock, champ **Quantité** (spinner ou nombre), bouton **« Ajouter au panier »**.

**Éléments :**
- Tableau : colonnes **ID**, **Nom**, **Prix**, **Stock** (optionnel : Description, Catégorie).
- Clic sur une ligne = affichage du détail à côté.
- Détail : nom du produit, prix, description, stock disponible, sélecteur de quantité, bouton « Ajouter au panier ».

**Prompt Figma AI :**
> Maquette desktop : interface catalogue e-commerce. Header avec « ChriOnline », « Bonjour, Jean » et « Déconnexion ». Onglets Catalogue et Mon panier. Zone principale : tableau produits (colonnes ID, Nom, Prix, Stock) à gauche ; à droite bloc détail produit (nom, prix, description, stock, quantité, bouton « Ajouter au panier »). Style application desktop sobre.

---

## 4. Mon panier

**Écran :** Contenu du panier (onglet « Mon panier » dans la même fenêtre que le catalogue).

**Disposition :**
- Même en-tête et onglets que l’écran 3 ; onglet **« Mon panier »** actif.
- **Tableau du panier** : colonnes **ID produit** (ou Nom), **Quantité**, **Prix unitaire**.
- **En bas à droite :** texte **« Total : XX,XX € »**, bouton **« Retirer la ligne sélectionnée »**, bouton **« Valider la commande (paiement) »** (ou « Passer commande »).

**Éléments :**
- Lignes = produits dans le panier.
- Sélection d’une ligne pour la retirer.
- Total mis à jour.
- Bouton principal : valider la commande (ouvre l’écran Paiement).

**Prompt Figma AI :**
> Maquette desktop : page panier e-commerce. Même header que le catalogue (ChriOnline, utilisateur, Déconnexion). Onglet « Mon panier » actif. Tableau avec colonnes ID produit, Quantité, Prix unitaire. En bas : « Total : 0,00 € », boutons « Retirer la ligne sélectionnée » et « Valider la commande (paiement) ». Style cohérent avec le catalogue.

---

## 5. Fenêtre Paiement (simulé)

**Écran :** Modal / dialogue qui s’ouvre après « Valider la commande ».

**Disposition :**
- Fenêtre modale centrée (environ 350×220 px).
- Titre : « Paiement simulé » ou « Paiement par carte ».
- Formulaire en grille 2 colonnes : libellé à gauche, champ à droite.

**Éléments :**
- **Numéro de carte** (champ texte).
- **Date d’expiration (MM/AA)** (champ court).
- **CVV** (champ court, masqué ou type mot de passe).
- Boutons : **« Valider »** (ou « Payer ») et **« Annuler »**.

**Prompt Figma AI :**
> Maquette : modal de paiement par carte. Titre « Paiement simulé ». Champs : Numéro carte, Expiration (MM/AA), CVV. Boutons Valider et Annuler. Fenêtre modale centrée, style e-commerce.

---

## 6. Confirmation de commande (succès)

**Écran :** Message affiché après paiement réussi (peut être une simple alerte ou une petite page).

**Disposition :**
- Message centré ou modal.
- Icône succès (optionnel), titre « Commande enregistrée » ou « Paiement accepté ».

**Éléments :**
- Texte : « Votre commande a été enregistrée. Numéro : [ID_COMMANDE] ».
- Bouton **« OK »** pour fermer et revenir au catalogue ou au panier.

**Prompt Figma AI :**
> Maquette : message de confirmation de commande e-commerce. Texte « Votre commande a été enregistrée. Numéro : CMD-12345 ». Bouton OK. Modal ou bandeau centré, style succès.

---

## 7. Historique des commandes (niveau avancé – client)

**Écran :** Liste des commandes passées par le client.

**Disposition :**
- Même en-tête que le catalogue (ChriOnline, utilisateur, Déconnexion).
- Onglet ou menu **« Mes commandes »** (en plus de Catalogue et Mon panier).
- **Tableau** : colonnes **ID commande**, **Date**, **Total**, **Statut** (En attente, Validée, Expédiée, Livrée, Annulée).
- Option : clic sur une ligne pour voir le détail (lignes de la commande).

**Éléments :**
- Liste des commandes avec ID, date, montant total, statut.
- Pas d’édition ; consultation uniquement.

**Prompt Figma AI :**
> Maquette desktop : page « Mes commandes » e-commerce. Header identique au catalogue. Tableau colonnes : ID commande, Date, Total, Statut. Style cohérent avec le reste de l’app. Consultation seule.

---

## 8. Interface administrateur – Tableau de bord

**Écran :** Accueil admin après connexion avec un compte administrateur.

**Disposition :**
- **En-tête** : « ChriOnline – Administration », nom de l’admin, Déconnexion.
- **Menu ou onglets** : Tableau de bord, Produits, Commandes, Utilisateurs.
- **Zone principale :** cartes ou blocs de statistiques (nombre de commandes du jour, nombre de produits, nombre d’utilisateurs, etc.) et éventuellement un résumé des dernières commandes.

**Éléments :**
- Titre « Tableau de bord ».
- 3 à 4 cartes : Chiffre + libellé (ex. « 42 commandes », « 120 produits », « 85 utilisateurs »).
- Option : liste courte « Dernières commandes » (ID, date, statut).

**Prompt Figma AI :**
> Maquette desktop : tableau de bord administrateur e-commerce. Header « ChriOnline – Administration » et Déconnexion. Menu : Tableau de bord, Produits, Commandes, Utilisateurs. Zone centrale : 3 ou 4 cartes statistiques (commandes, produits, utilisateurs). Style admin sobre.

---

## 9. Interface administrateur – Gestion des produits

**Écran :** CRUD produits (liste + ajout / modification).

**Disposition :**
- Même header et menu que le tableau de bord ; **« Produits »** actif.
- **Tableau** : colonnes **ID**, **Nom**, **Description**, **Prix**, **Stock**, **Catégorie** (optionnel). Dernière colonne : **Actions** (Modifier, Supprimer).
- Bouton **« Ajouter un produit »** au-dessus du tableau.
- Clic sur « Ajouter » ou « Modifier » ouvre un **formulaire** (modal ou panneau latéral) : Nom, Description, Prix, Stock, Catégorie (liste déroulante).

**Éléments :**
- Liste des produits avec actions Modifier / Supprimer.
- Formulaire produit : Nom, Description (zone texte), Prix, Stock (nombre), Catégorie. Boutons Enregistrer et Annuler.

**Prompt Figma AI :**
> Maquette desktop : page admin « Gestion des produits » e-commerce. Tableau avec ID, Nom, Description, Prix, Stock, Actions (Modifier, Supprimer). Bouton « Ajouter un produit ». Modal ou panneau formulaire : Nom, Description, Prix, Stock, Catégorie. Style cohérent tableau de bord admin.

---

## 10. Interface administrateur – Gestion des commandes

**Écran :** Liste des commandes et mise à jour du statut.

**Disposition :**
- Même header et menu ; **« Commandes »** actif.
- **Tableau** : **ID commande**, **Client (email ou nom)**, **Date**, **Total**, **Statut** (liste déroulante ou boutons : En attente, Validée, Expédiée, Livrée, Annulée). Option : bouton **« Changer le statut »** ou liste déroulante inline.
- Filtres optionnels : par statut, par date.

**Éléments :**
- Une ligne par commande.
- Statut modifiable (liste déroulante ou bouton « Changer statut » ouvrant un petit formulaire).
- Option : détail de la commande (lignes de produits) en modal ou panneau.

**Prompt Figma AI :**
> Maquette desktop : page admin « Gestion des commandes » e-commerce. Tableau : ID commande, Client, Date, Total, Statut (modifiable). Bouton ou liste pour changer le statut. Style cohérent avec gestion des produits.

---

## 11. Interface administrateur – Gestion des utilisateurs

**Écran :** Liste des utilisateurs (clients et admins).

**Disposition :**
- Même header et menu ; **« Utilisateurs »** actif.
- **Tableau** : **ID**, **Nom**, **Email**, **Rôle** (Client / Admin), **Date d’inscription** (optionnel). Colonne **Actions** : Modifier (ex. rôle), éventuellement Désactiver (selon cahier des charges).

**Éléments :**
- Liste des comptes.
- Modification du rôle (Client ↔ Admin) possible.
- Pas de suppression de compte obligatoire ; à préciser selon le cahier.

**Prompt Figma AI :**
> Maquette desktop : page admin « Gestion des utilisateurs » e-commerce. Tableau : ID, Nom, Email, Rôle (Client/Admin), Actions (Modifier). Style cohérent avec les autres pages admin.

---

## Récapitulatif des pages

| # | Page | Type | Usage |
|---|------|------|--------|
| 1 | Connexion | Client | Premier écran |
| 2 | Inscription | Client | Création de compte |
| 3 | Catalogue | Client | Liste + détail produits |
| 4 | Mon panier | Client | Panier + accès paiement |
| 5 | Paiement (modal) | Client | Formulaire carte simulée |
| 6 | Confirmation commande | Client | Succès + numéro commande |
| 7 | Historique commandes | Client (avancé) | Liste des commandes |
| 8 | Tableau de bord admin | Admin | Statistiques |
| 9 | Gestion produits | Admin | CRUD produits |
| 10 | Gestion commandes | Admin | Liste + statuts |
| 11 | Gestion utilisateurs | Admin | Liste + rôles |

---

## Utilisation avec Figma AI

1. Ouvrir Figma et créer un nouveau fichier (ou un frame par page).
2. Utiliser la fonction **Figma AI** (ou un plugin de génération par texte).
3. Copier-coller le **« Prompt Figma AI »** de la section correspondante.
4. Ajuster les textes, couleurs et espacements selon votre charte.
5. Réutiliser les mêmes composants (header, boutons, champs) pour garder la cohérence entre les maquettes.

Vous pouvez aussi utiliser ces descriptions pour dessiner les maquettes à la main ou pour un autre outil (Sketch, Adobe XD, etc.).
