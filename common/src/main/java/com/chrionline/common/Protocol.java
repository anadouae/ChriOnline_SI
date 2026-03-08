package com.chrionline.common;

/**
 * Constantes du protocole de communication client-serveur.
 * À utiliser par les deux côtés (client et serveur).
 */
public final class Protocol {

    private Protocol() {}

    // Commandes utilisateur
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";

    // Commandes produits
    public static final String GET_PRODUCTS = "GET_PRODUCTS";
    public static final String GET_PRODUCT = "GET_PRODUCT";

    // Commandes panier
    public static final String ADD_TO_CART = "ADD_TO_CART";
    public static final String REMOVE_FROM_CART = "REMOVE_FROM_CART";
    public static final String GET_CART = "GET_CART";

    // Commandes commande
    public static final String VALIDATE_ORDER = "VALIDATE_ORDER";
    public static final String GET_ORDERS = "GET_ORDERS";

    // Réponses
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    // Séparateur des messages
    public static final String SEPARATOR = "|";

    // Port par défaut
    public static final int DEFAULT_PORT = 12345;

    // Configuration réseau
    public static final String DEFAULT_HOST = "localhost";
}
