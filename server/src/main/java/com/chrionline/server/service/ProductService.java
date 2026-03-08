package com.chrionline.server.service;

import com.chrionline.common.Protocol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service produits : liste et détail.
 */
public class ProductService {

    /**
     * GET_PRODUCTS
     * Réponse : OK|id1|nom1|prix1|stock1|description1||id2|nom2|...
     * (description peut être vide, on utilise || entre produits)
     */
    public String getProducts() {
        String sql = "SELECT id, name, price, stock, description FROM products ORDER BY id";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            StringBuilder sb = new StringBuilder(Protocol.OK);
            String sep = "";
            while (rs.next()) {
                sb.append(sep);
                sb.append(rs.getInt("id")).append(Protocol.SEPARATOR);
                sb.append(escape(rs.getString("name"))).append(Protocol.SEPARATOR);
                sb.append(rs.getDouble("price")).append(Protocol.SEPARATOR);
                sb.append(rs.getInt("stock")).append(Protocol.SEPARATOR);
                sb.append(escape(rs.getString("description")));
                sep = Protocol.SEPARATOR + Protocol.SEPARATOR; // || entre produits
            }
            return sb.toString();
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "PRODUCT_NOT_FOUND";
        }
    }

    /**
     * GET_PRODUCT|productId
     * Réponse : OK|id|nom|prix|stock|description ou ERROR|message
     */
    public String getProduct(String productIdStr) {
        int id;
        try {
            id = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        String sql = "SELECT id, name, price, stock, description FROM products WHERE id = ?";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Protocol.ERROR + Protocol.SEPARATOR + "PRODUCT_NOT_FOUND";
                }
                return Protocol.OK + Protocol.SEPARATOR
                    + rs.getInt("id") + Protocol.SEPARATOR
                    + escape(rs.getString("name")) + Protocol.SEPARATOR
                    + rs.getDouble("price") + Protocol.SEPARATOR
                    + rs.getInt("stock") + Protocol.SEPARATOR
                    + escape(rs.getString("description"));
            }
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "PRODUCT_NOT_FOUND";
        }
    }

    /** Vérifie le stock disponible pour un produit. */
    public int getStock(int productId) {
        String sql = "SELECT stock FROM products WHERE id = ?";
        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("stock") : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    /** Décrémente le stock (après validation commande). Utilise la connexion passée pour la même transaction. */
    public boolean decrementStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, quantity);
        ps.setInt(2, productId);
        ps.setInt(3, quantity);
        return ps.executeUpdate() == 1;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("|", "/").replace("\n", " ");
    }
}
