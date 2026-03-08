package com.chrionline.app.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * En-tête client : logo ChriOnline, "Bonjour, [Nom]", Déconnexion.
 */
public class ClientHeader extends JPanel {

    public ClientHeader(String userName, Runnable onLogout, Runnable onAdminLink) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xdee2e6)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        JLabel logo = new JLabel("ChriOnline");
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 18f));
        logo.setForeground(UiConstants.BLUE_DARK);
        add(logo, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(new JLabel("Bonjour, " + userName));
        if (onAdminLink != null) {
            JButton adminLink = new JButton("Administration");
            adminLink.setBorderPainted(false);
            adminLink.setContentAreaFilled(false);
            adminLink.setForeground(UiConstants.BLUE_DARK);
            adminLink.addActionListener(e -> onAdminLink.run());
            right.add(adminLink);
        }
        JButton logout = new JButton("Déconnexion");
        logout.setBorder(BorderFactory.createLineBorder(new Color(0xdee2e6)));
        logout.setBackground(Color.WHITE);
        logout.addActionListener(e -> onLogout.run());
        right.add(logout);
        add(right, BorderLayout.EAST);
    }

    public ClientHeader(String userName, Runnable onLogout) {
        this(userName, onLogout, null);
    }
}
