package com.chrionline.server.handler;

/**
 * Interface des handlers de requêtes (pour travail en binôme sans conflits).
 * Chaque bloc (Auth+Produits / Panier+Commandes) implémente ses handlers.
 */
public interface RequestHandler {

    /**
     * Retourne true si ce handler gère la commande.
     */
    boolean accepts(String command);

    /**
     * Traite la requête et retourne la réponse (OK|... ou ERROR|...).
     */
    String handle(String command, String[] parts);
}
