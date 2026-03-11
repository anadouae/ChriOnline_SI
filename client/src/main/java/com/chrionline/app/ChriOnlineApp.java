package com.chrionline.app;

import com.chrionline.app.network.ApiService;
import com.chrionline.app.network.TcpApiService;
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
            TcpApiService tcpApiServicepi = new TcpApiService();

            if(!tcpApiServicepi.connect()){
                JOptionPane.showMessageDialog(
                        null,
                        "Connexion au serveur impossible !",
                        "Connexion échouée",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Connexion au serveur réussie !",
                        "Connexion réussie",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            HomeFrame home = new HomeFrame(tcpApiServicepi);
            home.setVisible(true);
        });
    }
}
