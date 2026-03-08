package com.chrionline.app.ui.admin;

import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import java.awt.*;

public class AdminHeader extends JPanel {

    public AdminHeader(Runnable onLogout, Runnable onViewClientCatalog) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xdee2e6)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        JLabel title = new JLabel("ChriOnline - Administration");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(UiConstants.BLUE_DARK);
        add(title, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(new JLabel("Admin"));
        JButton clientCatalog = new JButton("Voir le catalogue client");
        clientCatalog.setBorderPainted(false);
        clientCatalog.setContentAreaFilled(false);
        clientCatalog.setForeground(UiConstants.BLUE_DARK);
        clientCatalog.addActionListener(e -> onViewClientCatalog.run());
        right.add(clientCatalog);
        JButton logout = new JButton("Déconnexion");
        logout.setBorder(BorderFactory.createLineBorder(new Color(0xdee2e6)));
        logout.setBackground(Color.WHITE);
        logout.addActionListener(e -> onLogout.run());
        right.add(logout);
        add(right, BorderLayout.EAST);
    }
}
