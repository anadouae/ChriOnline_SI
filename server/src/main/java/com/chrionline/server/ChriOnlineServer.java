package com.chrionline.server;

import com.chrionline.common.Protocol;
import com.chrionline.server.handler.AuthRequestHandler;
import com.chrionline.server.handler.CartRequestHandler;
import com.chrionline.server.handler.OrderRequestHandler;
import com.chrionline.server.handler.ProductRequestHandler;
import com.chrionline.server.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Serveur principal ChriOnline.
 * Enregistre les handlers (Bloc A : Auth + Produits, Bloc B : Panier + Commandes).
 */
public class ChriOnlineServer {

    private static final int PORT = Protocol.DEFAULT_PORT;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        List<RequestHandler> handlers = new ArrayList<>();
        handlers.add(new AuthRequestHandler());
        handlers.add(new ProductRequestHandler());
        handlers.add(new CartRequestHandler());
        handlers.add(new OrderRequestHandler());

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("ChriOnline Server démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté: " + clientSocket.getInetAddress());
                executor.submit(new ClientHandler(clientSocket, handlers));
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
