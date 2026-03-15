package com.chrionline.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.chrionline.app.model.CartItem;
import com.chrionline.app.model.Order;
import com.chrionline.app.model.OrderStatus;
import com.chrionline.app.model.Product;
import com.chrionline.app.model.User;
import com.chrionline.app.model.UserRole;
import com.chrionline.common.Protocol;

/**
 * Implémentation TCP de ApiService.
 *
 * Remplace le mock par de vraies communications avec le serveur via Socket TCP.
 * Le protocole utilisé est : COMMANDE|param1|param2|...
 * Les réponses du serveur sont : OK|... ou ERROR|message
 *
 * Format des réponses serveur (voir services côté serveur) :
 *   LOGIN        → OK|userId|email|name|role
 *   REGISTER     → OK|message
 *   GET_PRODUCTS → OK|id|nom|prix|stock|desc||id|nom|prix|stock|desc||...
 *   GET_CART     → OK|prodId|qty|prix||prodId|qty|prix||total
 *   VALIDATE_ORDER → OK|orderId
 *   GET_ORDERS   → OK|orderId|date|total||orderId|date|total||...
 */
public class TcpApiService extends ApiService {

    // ─── Connexion TCP ────────────────────────────────────────────────────────
    private final String host;
    private final int port;

    private Socket socket;
    private BufferedReader inFromServer;
    private PrintWriter outToServer;

    private User currentUser;

    /** Connexion au serveur sur localhost:12345 (valeurs par défaut). */
    public TcpApiService() {
        this(Protocol.DEFAULT_HOST, Protocol.DEFAULT_PORT);
    }

    /** Connexion au serveur sur l'hôte et port indiqués. */
    public TcpApiService(String host, int port) {
        // On appelle super() pour éviter l'initialisation des données mock
        // mais on ne va jamais utiliser les méthodes du parent
        this.host = host;
        this.port = port;
    }

    // ─── Connexion / déconnexion TCP ──────────────────────────────────────────
    /**
     * Ouvre la connexion TCP au serveur.
     * À appeler avant toutToServerusage, ou laisser connect() le faire automatiquement.
     */
    public boolean connect() {
        try {
            disconnect();
            socket = new Socket(host, port);

            // Créer le lecteur : lit les réponses du serveur ligne par ligne
            inFromServer= new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Créer l'écrivain : envoie des lignes au serveur
            //    "true" = auto-flush : chaque println() envoie immédiatement
            outToServer= new PrintWriter(socket.getOutputStream(), true);
            System.out.println("[NetworkClient] Connecté à " + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("[TcpApiService] Impossible de se connecter au serveur " + host + ":" + port);
            System.err.println("[TcpApiService] " + e.getMessage());
            return false;
        }
    }

    /** Ferme proprement la connexion TCP. */
    public void disconnect() {
        try {
            if (inFromServer != null) {
                inFromServer.close();
            }
        } catch (IOException e) {
            System.err.println("[NetworkClient] Error while closing input stream: " + e.getMessage());
        }

        if (outToServer != null) {
            outToServer.close();
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[NetworkClient] Déconnecté.");
            }
        } catch (IOException e) {
            System.err.println("[NetworkClient] Erreur lors de la déconnexion : " + e.getMessage());
        } finally {
            socket = null;
            inFromServer = null;
            outToServer = null;
        }
    }

    /** Vérifie si la connexion TCP est active. */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    // ─── Envoi / réception d'une requête ──────────────────────────────────────
    /**
     * Envoie une requête au serveur et retourne la réponse brute.
     * Se reconnecte automatiquement si la connexion est coupée.
     * Retourne null en cas d'échec réseau.
     */
    private String sendRequest(String request) {
        try {
            // Vérifier qu'on est bien connecté
            if (!isConnected()) {
                if (!connect()) {
                    System.err.println("[TCP][CLIENT] Connection failed. Request not sent: " + request);
                    return null; // reconnexion échouée
                }
            }
            System.out.println("[TCP][CLIENT -> SERVER] " + request);
            outToServer.println(request);
            String response = inFromServer.readLine();
            if (response == null) {
                System.err.println("[TCP][SERVER -> CLIENT] <null> (connection closed by server)");
                disconnect();
                return null;
            }
            System.out.println("[TCP][SERVER -> CLIENT] " + response);
            return response;
        } catch (IOException e) {
            System.err.println("[TcpApiService] Erreur réseau : " + e.getMessage());
            System.err.println("[TCP][ERROR] Network error while sending request: " + request);
            System.err.println("[TCP][ERROR] " + e.getMessage());
            disconnect();
            return null;
        }
    }

    // ─── Authentification ─────────────────────────────────────────────────────
    /**
     * LOGIN|email|password
     * Réponse serveur : OK|userId|email|name|role  ou  ERROR|message
     */
    @Override
    public User login(String email, String password) {
        String response = sendRequest("LOGIN|" + email + "|" + password);
        if (response == null) return null;

        String[] parts = response.split("\\|", -1);
        if (parts.length >= 5 && "OK".equals(parts[0])) {
            // OK|userId|email|name|role
            int userId = TcpApiService.parseInt(parts[1], -1);
            String userEmail = parts[2];
            String name = parts[3];
            UserRole role = "ADMIN".equalsIgnoreCase(parts[4]) ? UserRole.ADMIN : UserRole.CLIENT;
            currentUser = new User(userId, name, userEmail, role);
            return currentUser;
        }
        // Réponse ERROR ou malformée → login échoué
        return null;
    }

    /**
     * REGISTER|email|password|name
     * Réponse serveur : OK|message  ou  ERROR|message
     * Retourne true si inscription réussie, false sinon.
     */
    @Override
    public boolean register(String email, String password, String name) {
        String response = sendRequest("REGISTER|" + email + "|" + password + "|" + name);
        if (response == null) return false;
        return response.startsWith("OK");
    }

    /**
     * LOGOUT|userId
     */
    @Override
    public void logout() {
        if (currentUser != null) {
            sendRequest("LOGOUT|" + currentUser.getId());
        }
        currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    // ─── Produits ─────────────────────────────────────────────────────────────

    /**
     * GET_PRODUCTS
     * Réponse serveur : OK|id|nom|prix|stock|desc||id|nom|prix|stock|desc||...
     * Les produits sont séparés par "||" (double pipe).
     */
    @Override
    public List<Product> getProducts() {
        String response = sendRequest("GET_PRODUCTS");
        List<Product> list = new ArrayList<>();
        if (response == null || !response.startsWith("OK")) return list;

        // Retirer le "OK|" du début
        String data = response.substring(3); // "OK|" = 3 chars
        if (data.isEmpty()) return list;

        // Découper par "||" pour séparer les produits
        String[] productTokens = data.split("\\|\\|");
        for (String token : productTokens) {
            String[] p = token.split("\\|", -1);
            if (p.length >= 5) {
                int id = parseInt(p[0], 0);
                String name = p[1];
                double price = parseDouble(p[2], 0.0);
                int stock = parseInt(p[3], 0);
                String desc = p[4];
                // La catégorie n'est pas renvoyée par le serveur actuel → champ vide
                list.add(new Product(id, name, desc, price, stock, ""));
            }
        }
        return list;
    }

    /**
     * GET_PRODUCT|productId
     * Réponse serveur : OK|id|nom|prix|stock|desc  ou  ERROR|message
     */
    @Override
    public Product getProduct(int productId) {
        String response = sendRequest("GET_PRODUCT|" + productId);
        if (response == null || !response.startsWith("OK")) return null;

        String[] p = response.split("\\|", -1);
        // OK|id|nom|prix|stock|desc → indices 0-5
        if (p.length >= 6) {
            int id = parseInt(p[1], 0);
            String name = p[2];
            double price = parseDouble(p[3], 0.0);
            int stock = parseInt(p[4], 0);
            String desc = p[5];
            return new Product(id, name, desc, price, stock, "");
        }
        return null;
    }

    // ─── Panier ───────────────────────────────────────────────────────────────

    /**
     * GET_CART|userId
     * Réponse serveur : OK|prodId|qty|prix||prodId|qty|prix||total
     * Note : le total est le dernier élément après le dernier "||"
     */
    @Override
    public List<CartItem> getCart() {
        List<CartItem> list = new ArrayList<>();
        if (currentUser == null) return list;

        String response = sendRequest("GET_CART|" + currentUser.getId());
        if (response == null || !response.startsWith("OK")) return list;

        String data = response.substring(3); // "OK|"
        if (data.isEmpty()) return list;

        // Découper par "||"
        String[] parts = data.split("\\|\\|");
        // Le dernier élément est le total — on ne le parse pas ici (voir getCartTotal())
        // Les autres éléments sont les lignes du panier
        int lineCount = parts.length - 1; // dernier = total
        for (int i = 0; i < lineCount; i++) {
            String[] f = parts[i].split("\\|", -1);
            if (f.length >= 3) {
                int productId = parseInt(f[0], 0);
                int qty = parseInt(f[1], 0);
                double price = parseDouble(f[2], 0.0);
                // Le nom du produit n'est pas renvoyé par GET_CART → on l'affiche comme ID
                list.add(new CartItem(productId, "Produit #" + productId, qty, price));
            }
        }
        return list;
    }

    /**
     * ADD_TO_CART|userId|productId|quantity
     * Réponse serveur : OK  ou  ERROR|message
     */
    @Override
    public void addToCart(int productId, int quantity) {
        if (currentUser == null) return;
        sendRequest("ADD_TO_CART|" + currentUser.getId() + "|" + productId + "|" + quantity);
    }

    /**
     * REMOVE_FROM_CART|userId|productId
     * Réponse serveur : OK  ou  ERROR|message
     */
    @Override
    public void removeFromCart(int productId) {
        if (currentUser == null) return;
        sendRequest("REMOVE_FROM_CART|" + currentUser.getId() + "|" + productId);
    }

    /**
     * Calcule le total du panier à partir de la réponse GET_CART.
     * Le total est le dernier élément après le dernier "||".
     */
    @Override
    public double getCartTotal() {
        if (currentUser == null) return 0.0;

        String response = sendRequest("GET_CART|" + currentUser.getId());
        if (response == null || !response.startsWith("OK")) return 0.0;

        String data = response.substring(3);
        if (data.isEmpty()) return 0.0;

        // Le total est après le dernier "||"
        int lastSep = data.lastIndexOf("||");
        if (lastSep < 0) return 0.0;
        String totalStr = data.substring(lastSep + 2);
        return parseDouble(totalStr, 0.0);
    }

    // ─── Commandes ────────────────────────────────────────────────────────────

    /**
     * VALIDATE_ORDER|userId|cardNumber|cardExpiry|cvv
     * Réponse serveur : OK|orderId  ou  ERROR|message
     * Retourne l'orderId si succès, null sinon.
     */
    @Override
    public String validateOrder(String cardNumber, String expiry, String cvv) {
        if (currentUser == null) return null;
        String response = sendRequest(
            "VALIDATE_ORDER|" + currentUser.getId() + "|" + cardNumber + "|" + expiry + "|" + cvv
        );
        if (response == null || !response.startsWith("OK")) return null;

        String[] parts = response.split("\\|", -1);
        return parts.length >= 2 ? parts[1] : null;
    }

    /**
     * GET_ORDERS|userId
     * Réponse serveur : OK|orderId|date|total||orderId|date|total||...
     */
    @Override
    public List<Order> getOrdersForCurrentUser() {
        // ensureConnected();
        // UserDto u = getCurrentUser();
        List<Order> list = new ArrayList<>();
        if (currentUser == null) return list;

        String response = sendRequest("GET_ORDERS|" + currentUser.getId());
        if (response == null || response.isBlank() || !response.startsWith("OK")) return list;

        response = response.trim();

        if(response.equals("OK") || response.equals("OK|")) {
            // Cas où il n'y a aucune commande → OK ou OK| (sans données)
            return list;
        }
        else if(response.startsWith("ERROR|")) {
            // Cas d'erreur → on affiche un message d'erreur (optionnel)
            System.err.println("[TcpApiService] Erreur lors de la récupération des commandes : " + response);
            return list;
        }

        String data = response.substring(3);
        if (data.isEmpty()) return list;

        String[] parts = data.split("\\|\\|");
        for (String part : parts) {
            String[] f = part.split("\\|", -1);
            if (f.length >= 3) {
                String orderId = f[0];
                String dateStr = f[1]; // format "yyyy-MM-dd"
                double total = parseDouble(f[2], 0.0);
                LocalDateTime date = parseDate(dateStr);
                list.add(new Order(orderId, currentUser.getName(), currentUser.getEmail(),
                                   date, total, OrderStatus.EN_ATTENTE));
            }
        }
        return list;
    }

    // ─── Méthodes admin non implémentées côté serveur (restent en mock) ───────
    // Ces fonctionnalités nécessitent des commandes supplémentaires à implémenter
    // côté serveur (GET_ALL_ORDERS, GET_ALL_USERS, ADD_PRODUCT, etc.)

    @Override
    public List<Order> getAllOrders() {
        // TODO: implémenter GET_ALL_ORDERS côté serveur
        return new ArrayList<>();
    }

    @Override
    public List<User> getAllUsers() {
        // TODO: implémenter GET_ALL_USERS côté serveur
        return new ArrayList<>();
    }

    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        // TODO: implémenter UPDATE_ORDER_STATUS|orderId|status côté serveur
    }

    @Override
    public void addProduct(Product p) {
        // TODO: implémenter ADD_PRODUCT|nom|desc|prix|stock|categorie côté serveur
    }

    @Override
    public void updateProduct(Product p) {
        // TODO: implémenter UPDATE_PRODUCT|id|nom|desc|prix|stock|categorie côté serveur
    }

    @Override
    public void deleteProduct(int productId) {
        // TODO: implémenter DELETE_PRODUCT|id côté serveur
    }

    @Override
    public int getOrdersCountToday() {
        return 0; // TODO
    }

    @Override
    public double getTotalRevenue() {
        return 0.0; // TODO
    }


    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return fallback; }
    }

    private static double parseDouble(String s, double fallback) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return fallback; }
    }

    /** Parse une date "yyyy-MM-dd" en LocalDateTime à minuit. */
    private static LocalDateTime parseDate(String s) {
        try {
            String[] d = s.trim().split("-");
            return LocalDateTime.of(
                Integer.parseInt(d[0]),
                Integer.parseInt(d[1]),
                Integer.parseInt(d[2]),
                0, 0
            );
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
