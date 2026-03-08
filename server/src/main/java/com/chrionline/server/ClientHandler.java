package com.chrionline.server;

import com.chrionline.common.Protocol;
import com.chrionline.server.handler.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Gère la communication avec un client. Délègue chaque requête au handler qui l'accepte.
 * Permet le travail en binôme : Bloc A (Auth, Produits) et Bloc B (Panier, Commandes).
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final List<RequestHandler> handlers;

    public ClientHandler(Socket socket, List<RequestHandler> handlers) {
        this.socket = socket;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                if (request.isBlank()) continue;
                String response = processRequest(request.trim());
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Erreur ClientHandler: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
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
}
