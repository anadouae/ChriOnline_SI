package com.chrionline.server.service;

import com.chrionline.common.Protocol;

import java.sql.*;

/**
 * Service panier : ajout, suppression, récupération (total inclus).
 */
public class CartService {

    private final ProductService productService = new ProductService();

    /**
     * ADD_TO_CART|userId|productId|quantity
     * Retourne OK ou ERROR|message
     */
    public String addToCart(String userIdStr, String productIdStr, String quantityStr) {
        int userId, productId, quantity;
        try {
            userId = Integer.parseInt(userIdStr);
            productId = Integer.parseInt(productIdStr);
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) throw new NumberFormatException("qty");
        } catch (NumberFormatException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        int available = productService.getStock(productId);
        if (available < quantity) {
            return Protocol.ERROR + Protocol.SEPARATOR + "OUT_OF_STOCK";
        }

        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                     "ON CONFLICT (user_id, product_id) DO UPDATE SET quantity = cart_items.quantity + EXCLUDED.quantity";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            return Protocol.OK;
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }
    }

    /**
     * REMOVE_FROM_CART|userId|productId
     */
    public String removeFromCart(String userIdStr, String productIdStr) {
        int userId, productId;
        try {
            userId = Integer.parseInt(userIdStr);
            productId = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        String sql = "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
            return Protocol.OK;
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }
    }

    /**
     * GET_CART|userId
     * Réponse : OK|productId1|qty1|prix1||productId2|qty2|prix2||total
     */
    public String getCart(String userIdStr) {
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        String sql = "SELECT c.product_id, c.quantity, p.price FROM cart_items c " +
                     "JOIN products p ON p.id = c.product_id WHERE c.user_id = ?";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            StringBuilder sb = new StringBuilder(Protocol.OK);
            sb.append(Protocol.SEPARATOR);
            double total = 0;
            String sep = "";
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int qty = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    total += price * qty;
                    sb.append(sep).append(productId).append(Protocol.SEPARATOR).append(qty).append(Protocol.SEPARATOR).append(price);
                    sep = Protocol.SEPARATOR + Protocol.SEPARATOR;
                }
            }
            sb.append(Protocol.SEPARATOR).append(Protocol.SEPARATOR).append(String.format("%.2f", total));
            return sb.toString();
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }
    }

    /**
     * Récupère les lignes du panier pour validation commande (product_id, quantity, unit_price).
     */
    public java.util.List<int[]> getCartItemsForOrder(int userId) {
        String sql = "SELECT c.product_id, c.quantity, p.price FROM cart_items c " +
                     "JOIN products p ON p.id = c.product_id WHERE c.user_id = ?";
        java.util.List<int[]> items = new java.util.ArrayList<>();
        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new int[]{
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        (int) Math.round(rs.getDouble("price") * 100)
                    });
                }
            }
        } catch (SQLException ignored) {
        }
        return items;
    }

    /** Vide le panier après validation commande (même connexion pour transaction). */
    public void clearCart(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.executeUpdate();
    }
}
