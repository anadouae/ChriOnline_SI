package com.chrionline.app.ui.admin;

import com.chrionline.app.network.ApiService;
import com.chrionline.app.network.TcpApiService;
import com.chrionline.app.ui.auth.LoginFrame;
import com.chrionline.app.ui.client.ClientMainFrame;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    private final TcpApiService tcpApiService;
    private JTabbedPane tabs;

    public AdminFrame(TcpApiService tcpApiService) {
        this.tcpApiService = tcpApiService;
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
        tabs.addTab("Tableau de bord", new DashboardPanel(tcpApiService));
        tabs.addTab("Produits", new ProductsPanel(tcpApiService));
        tabs.addTab("Commandes", new AdminOrdersPanel(tcpApiService));
        tabs.addTab("Utilisateurs", new UsersPanel(tcpApiService));
        add(tabs, BorderLayout.CENTER);
    }

    private void doLogout() {
        tcpApiService.logout();
        dispose();
        new LoginFrame((TcpApiService) tcpApiService).setVisible(true);
    }

    private void openClientCatalog() {
        if (tcpApiService.getCurrentUser() != null) {
            tcpApiService.login(tcpApiService.getCurrentUser().getEmail(), "");
            dispose();
            new ClientMainFrame(tcpApiService).setVisible(true);
        }
    }
}
