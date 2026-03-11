package com.chrionline.app.ui.client;

import com.chrionline.app.network.ApiService;
import com.chrionline.app.network.TcpApiService;
import com.chrionline.app.ui.admin.AdminFrame;
import com.chrionline.app.ui.auth.LoginFrame;
import com.chrionline.app.ui.components.ClientHeader;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale client : onglets Catalogue, Mon panier, Mes commandes.
 * Si l'utilisateur est admin, le header affiche le bouton "Administration" vers l'espace admin.
 */
public class ClientMainFrame extends JFrame {

    private final TcpApiService tcpApiService;
    private JTabbedPane tabs;
    private CataloguePanel cataloguePanel;
    private CartPanel cartPanel;
    private OrdersPanel ordersPanel;

    public ClientMainFrame(TcpApiService tcpApiService) {
        this.tcpApiService = tcpApiService;
        setTitle("ChriOnline - Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UiConstants.BACKGROUND_LIGHT);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        String userName = tcpApiService.getCurrentUser() != null ? tcpApiService.getCurrentUser().getName() : "Utilisateur";
        boolean isAdmin = tcpApiService.getCurrentUser() != null && tcpApiService.getCurrentUser().isAdmin();
        add(new ClientHeader(userName, this::doLogout, isAdmin ? this::openAdmin : null), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        cataloguePanel = new CataloguePanel(tcpApiService, this::refreshCart);
        cartPanel = new CartPanel(tcpApiService, this::refreshCart);
        ordersPanel = new OrdersPanel(tcpApiService);

        tabs.addTab("Catalogue", cataloguePanel);
        tabs.addTab("Mon panier", cartPanel);
        tabs.addTab("Mes commandes", ordersPanel);
        add(tabs, BorderLayout.CENTER);
    }

    private void refreshCart() {
        if (cartPanel != null) cartPanel.refresh();
        if (ordersPanel != null) ordersPanel.refresh();
        updateCartTabTitle();
    }

    private void updateCartTabTitle() {
        int count = tcpApiService.getCart().stream().mapToInt(c -> c.getQuantity()).sum();
        if (count > 0)
            tabs.setTitleAt(1, "Mon panier (" + count + ")");
        else
            tabs.setTitleAt(1, "Mon panier");
    }

    public void openAdmin() {
        dispose();
        AdminFrame adminFrame = new AdminFrame(tcpApiService);
        adminFrame.setVisible(true);
    }

    public void showProductsPage() {
        if (tabs != null) {
            tabs.setSelectedIndex(0); // Catalogue = page produits }
        }
    }

    private void doLogout() {
        tcpApiService.logout();
        dispose();
        LoginFrame login = new LoginFrame((TcpApiService) tcpApiService);
        login.setVisible(true);
    }
}
