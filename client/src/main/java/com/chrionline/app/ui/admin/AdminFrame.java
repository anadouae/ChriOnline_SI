package com.chrionline.app.ui.admin;

import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.auth.LoginFrame;
import com.chrionline.app.ui.client.ClientMainFrame;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    private final ApiService api;
    private JTabbedPane tabs;

    public AdminFrame(ApiService api) {
        this.api = api;
        setTitle("ChriOnline - Administration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xf5f5f5));
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(new AdminHeader(this::doLogout, this::openClientCatalog), BorderLayout.NORTH);
        tabs = new JTabbedPane();
        tabs.addTab("Tableau de bord", new DashboardPanel(api));
        tabs.addTab("Produits", new ProductsPanel(api));
        tabs.addTab("Commandes", new AdminOrdersPanel(api));
        tabs.addTab("Utilisateurs", new UsersPanel(api));
        add(tabs, BorderLayout.CENTER);
    }

    private void doLogout() {
        api.logout();
        dispose();
        new LoginFrame(api).setVisible(true);
    }

    private void openClientCatalog() {
        if (api.getCurrentUser() != null) {
            api.login(api.getCurrentUser().getEmail(), "");
            dispose();
            new ClientMainFrame(api).setVisible(true);
        }
    }
}
