package com.chrionline.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * NetworkClient — couche réseau brute.
 *
 * Responsabilité UNIQUE : ouvrir un socket TCP, envoyer une ligne,
 * recevoir une ligne. Il ne comprend pas le protocole, il transporte.
 *
 *  Utilisation :
 *      NetworkClient client = new NetworkClient("localhost", 12345);
 *      client.connect();
 *      String reponse = client.send("LOGIN|jean@example.com|1234");
 *      client.disconnect();
 */
public class NetworkClient {

    // Configuration
    private final String host;  // adresse du serveur, ex: "localhost"
    private final int port;     // port du serveur, ex: 12345

    // Ressources réseau
    private Socket socket;          // le socket TCP lui-même
    private BufferedReader readerFromServer;  // pour LIRE les réponses du serveur
    private PrintWriter writeToServer;     // pour ÉCRIRE les requêtes vers le serveur

    // Constructeur
    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Connexion / déconnexion

    /**
     * Ouvre la connexion TCP vers le serveur.
     * À appeler UNE SEULE FOIS au démarrage de l'application.
     *
     * @return true si la connexion a réussi, false sinon
     */
    public boolean connect() {
        try {
            // Créer le socket : établit la connexion TCP avec le serveur
            socket = new Socket(host, port);

            // Créer le lecteur : lit les réponses du serveur ligne par ligne
            readerFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Créer l'écrivain : envoie des lignes au serveur
            //    "true" = auto-flush : chaque println() envoie immédiatement
            writeToServer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("[NetworkClient] Connecté à " + host + ":" + port);
            return true;

        } catch (IOException e) {
            System.err.println("[NetworkClient] Connexion impossible : " + e.getMessage());
            return false;
        }
    }

    /**
     * Ferme proprement le socket et les flux.
     * À appeler quand l'utilisateur ferme l'application.
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[NetworkClient] Déconnecté.");
            }
        } catch (IOException e) {
            System.err.println("[NetworkClient] Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    /**
     * Indique si le socket est actuellement connecté.
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Envoie une requête au serveur et attend sa réponse.
     *
     * C'est la SEULE méthode publique pour communiquer.
     * Elle est synchronisée (synchronized) pour éviter que deux appels
     * simultanés mélangent leurs réponses.
     *
     * @param request  la requête à envoyer, ex: "LOGIN|jean@example.com|1234"
     * @return         la réponse du serveur, ex: "OK|1|jean@example.com|Jean|CLIENT"
     *                 ou null en cas d'erreur réseau
     */
    public synchronized String send(String request) {
        // Vérifier qu'on est bien connecté
        if (!isConnected()) {
            System.err.println("[NetworkClient] Pas connecté. Tentative de reconnexion...");
            if (!connect()) {
                return null; // reconnexion échouée
            }
        }

        try {
            // Envoyer la requête (println ajoute \n automatiquement)
            writeToServer.println(request);

            // Lire la réponse (bloquant : attend que le serveur réponde)
            String response = readerFromServer.readLine();

            // Log pour le débogage (à retirer en production)
            System.out.println("[NetworkClient] → " + request);
            System.out.println("[NetworkClient] ← " + response);

            return response;

        } catch (IOException e) {
            System.err.println("[NetworkClient] Erreur réseau : " + e.getMessage());
            disconnect();
            return null;
        }
    }
}
