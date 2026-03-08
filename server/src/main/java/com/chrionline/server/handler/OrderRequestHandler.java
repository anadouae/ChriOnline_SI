package com.chrionline.server.handler;

import com.chrionline.common.Protocol;
import com.chrionline.server.service.OrderService;

/**
 * Handler Commandes – Bloc B (Personne 2).
 * Commandes : VALIDATE_ORDER, GET_ORDERS.
 */
public class OrderRequestHandler implements RequestHandler {

    private final OrderService orderService = new OrderService();

    @Override
    public boolean accepts(String command) {
        return "VALIDATE_ORDER".equals(command) || "GET_ORDERS".equals(command);
    }

    @Override
    public String handle(String command, String[] parts) {
        switch (command) {
            case "VALIDATE_ORDER":
                if (parts.length >= 5) return orderService.validateOrder(parts[1], parts[2], parts[3], parts[4]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            case "GET_ORDERS":
                if (parts.length >= 2) return orderService.getOrders(parts[1]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            default:
                return Protocol.ERROR + Protocol.SEPARATOR + "UNKNOWN_COMMAND";
        }
    }
}
