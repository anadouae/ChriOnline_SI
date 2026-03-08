package com.chrionline.server.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestionnaire de connexion à la base PostgreSQL.
 * Charge la config depuis database.properties.
 */
public class DatabaseManager {

    private static final String PROP_FILE = "database.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream(PROP_FILE)) {
            if (is == null) {
                throw new IllegalStateException("Fichier " + PROP_FILE + " introuvable dans les resources.");
            }
            Properties p = new Properties();
            p.load(is);
            url = p.getProperty("db.url");
            user = p.getProperty("db.user");
            password = p.getProperty("db.password", "");
        } catch (IOException e) {
            throw new RuntimeException("Erreur chargement " + PROP_FILE, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
