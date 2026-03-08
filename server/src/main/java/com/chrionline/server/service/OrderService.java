package com.chrionline.server.service;

import com.chrionline.common.Protocol;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service commandes : validation avec paiement simulé, génération ID unique.
 */
public class OrderService {

    private final CartService cartService = new CartService();
    private final ProductService productService = new ProductService();

    private static final AtomicInteger orderSeq = new AtomicInteger(1);

    /**
     * VALIDATE_ORDER|userId|cardNumber|cardExpiry|cvv
     * Retourne OK|orderId ou ERROR|message
     */
    public String validateOrder(String userIdStr, String cardNumber, String cardExpiry, String cvv) {
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        if (cardNumber == null || cardNumber.replace(" ", "").length() < 12) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        List<int[]> items = cartService.getCartItemsForOrder(userId);
        if (items.isEmpty()) {
            return Protocol.ERROR + Protocol.SEPARATOR + "Panier vide";
        }

        // Vérifier les stocks
        for (int[] row : items) {
            int productId = row[0], qty = row[1];
            if (productService.getStock(productId) < qty) {
                return Protocol.ERROR + Protocol.SEPARATOR + "OUT_OF_STOCK";
            }
        }

        String orderId = generateOrderId();
        double total = 0;
        for (int[] row : items) {
            total += row[1] * (row[2] / 100.0);
        }

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert order
                String orderSql = "INSERT INTO orders (id, user_id, total, status, payment_method) VALUES (?, ?, ?, 'EN_ATTENTE', 'CARTE')";
                try (PreparedStatement psOrder = conn.prepareStatement(orderSql)) {
                    psOrder.setString(1, orderId);
                    psOrder.setInt(2, userId);
                    psOrder.setDouble(3, total);
                    psOrder.executeUpdate();
                }

                // Insert order_items et décrémenter stock
                String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
                for (int[] row : items) {
                    int productId = row[0], qty = row[1];
                    double unitPrice = row[2] / 100.0;
                    try (PreparedStatement psItem = conn.prepareStatement(itemSql)) {
                        psItem.setString(1, orderId);
                        psItem.setInt(2, productId);
                        psItem.setInt(3, qty);
                        psItem.setDouble(4, unitPrice);
                        psItem.executeUpdate();
                    }
                    if (!productService.decrementStock(conn, productId, qty)) {
                        throw new SQLException("Stock update failed");
                    }
                }

                cartService.clearCart(conn, userId);
                conn.commit();
                return Protocol.OK + Protocol.SEPARATOR + orderId;
            } catch (Exception e) {
                conn.rollback();
                return Protocol.ERROR + Protocol.SEPARATOR + "OUT_OF_STOCK";
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }
    }

    /**
     * GET_ORDERS|userId
     * Réponse : OK|orderId1|date1|total1||orderId2|...
     */
    public String getOrders(String userIdStr) {
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        String sql = "SELECT id, created_at, total FROM orders WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            StringBuilder sb = new StringBuilder(Protocol.OK);
            String sep = "";
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append(sep);
                    sb.append(rs.getString("id")).append(Protocol.SEPARATOR);
                    sb.append(rs.getTimestamp("created_at").toLocalDateTime().toLocalDate()).append(Protocol.SEPARATOR);
                    sb.append(rs.getDouble("total"));
                    sep = Protocol.SEPARATOR + Protocol.SEPARATOR;
                }
            }
            return sb.toString();
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }
    }

    private static String generateOrderId() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int n = orderSeq.getAndIncrement();
        return "ORD-" + date + "-" + String.format("%03d", n);
    }
}
