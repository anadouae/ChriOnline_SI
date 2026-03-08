package com.chrionline.server.handler;

import com.chrionline.common.Protocol;
import com.chrionline.server.service.CartService;

/**
 * Handler Panier – Bloc B (Personne 2).
 * Commandes : ADD_TO_CART, REMOVE_FROM_CART, GET_CART.
 */
public class CartRequestHandler implements RequestHandler {

    private final CartService cartService = new CartService();

    @Override
    public boolean accepts(String command) {
        return "ADD_TO_CART".equals(command) || "REMOVE_FROM_CART".equals(command) || "GET_CART".equals(command);
    }

    @Override
    public String handle(String command, String[] parts) {
        switch (command) {
            case "ADD_TO_CART":
                if (parts.length >= 4) return cartService.addToCart(parts[1], parts[2], parts[3]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            case "REMOVE_FROM_CART":
                if (parts.length >= 3) return cartService.removeFromCart(parts[1], parts[2]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            case "GET_CART":
                if (parts.length >= 2) return cartService.getCart(parts[1]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            default:
                return Protocol.ERROR + Protocol.SEPARATOR + "UNKNOWN_COMMAND";
        }
    }
}
