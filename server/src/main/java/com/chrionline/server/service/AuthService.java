package com.chrionline.server.service;

import com.chrionline.common.Protocol;

import java.sql.*;
import java.util.regex.Pattern;

/**
 * Service d'authentification : inscription, connexion, déconnexion.
 */
public class AuthService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.\\w+$");

    /**
     * REGISTER|email|password|nom
     * Retourne OK|Inscription réussie ou ERROR|message
     */
    public String register(String email, String password, String name) {
        if (email == null || email.isBlank() || password == null || password.isBlank() || name == null || name.isBlank()) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return Protocol.ERROR + Protocol.SEPARATOR + "Email invalide";
        }
        if (password.length() < 4) {
            return Protocol.ERROR + Protocol.SEPARATOR + "Mot de passe trop court";
        }

        String hash = hashPassword(password);
        String sql = "INSERT INTO users (email, password_hash, name, role) VALUES (?, ?, ?, 'CLIENT')";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, hash);
            ps.setString(3, name.trim());
            ps.executeUpdate();
            return Protocol.OK + Protocol.SEPARATOR + "Inscription réussie";
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) { // unique violation
                return Protocol.ERROR + Protocol.SEPARATOR + "Email déjà utilisé";
            }
            return Protocol.ERROR + Protocol.SEPARATOR + "AUTH_REGISTER_FAILED";
        }
    }

    /**
     * LOGIN|email|password
     * Retourne OK|userId|email|name|role ou ERROR|message
     */
    public String login(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
        }

        String sql = "SELECT id, email, name, role, password_hash FROM users WHERE email = ?";

        try (Connection conn = com.chrionline.server.db.DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Protocol.ERROR + Protocol.SEPARATOR + "AUTH_LOGIN_FAILED";
                }
                int userId = rs.getInt("id");
                String userEmail = rs.getString("email");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String storedHash = rs.getString("password_hash");
                if (!checkPassword(password, storedHash)) {
                    return Protocol.ERROR + Protocol.SEPARATOR + "AUTH_LOGIN_FAILED";
                }
                return Protocol.OK + Protocol.SEPARATOR + userId + Protocol.SEPARATOR + userEmail + Protocol.SEPARATOR + name + Protocol.SEPARATOR + (role != null ? role : "CLIENT");
            }
        } catch (SQLException e) {
            return Protocol.ERROR + Protocol.SEPARATOR + "AUTH_LOGIN_FAILED";
        }
    }

    /**
     * LOGOUT|userId — simple accusé OK
     */
    public String logout(String userIdStr) {
        return Protocol.OK;
    }

    /** Hash simple (hashCode). Pour production : BCrypt. */
    private static String hashPassword(String plain) {
        return String.valueOf(plain.hashCode());
    }

    private static boolean checkPassword(String plain, String storedHash) {
        return hashPassword(plain).equals(storedHash);
    }
}
