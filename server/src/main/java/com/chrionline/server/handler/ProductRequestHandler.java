package com.chrionline.server.handler;

import com.chrionline.common.Protocol;
import com.chrionline.server.service.ProductService;

/**
 * Handler Produits – Bloc A (Personne 1).
 * Commandes : GET_PRODUCTS, GET_PRODUCT.
 */
public class ProductRequestHandler implements RequestHandler {

    private final ProductService productService = new ProductService();

    @Override
    public boolean accepts(String command) {
        return "GET_PRODUCTS".equals(command) || "GET_PRODUCT".equals(command);
    }

    @Override
    public String handle(String command, String[] parts) {
        switch (command) {
            case "GET_PRODUCTS":
                return productService.getProducts();
            case "GET_PRODUCT":
                if (parts.length >= 2) return productService.getProduct(parts[1]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            default:
                return Protocol.ERROR + Protocol.SEPARATOR + "UNKNOWN_COMMAND";
        }
    }
}
