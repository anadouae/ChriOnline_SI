package com.chrionline.app;

import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.home.HomeFrame;

import javax.swing.*;

/**
 * Point d'entrée ChriOnline v1 - Frontend desktop (Client + Admin).
 * Démarrage par la homepage, puis connexion → espace client (avec bouton Administration si admin).
 */
public class ChriOnlineApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            ApiService api = new ApiService();
            HomeFrame home = new HomeFrame(api);
            home.setVisible(true);
        });
    }
}
