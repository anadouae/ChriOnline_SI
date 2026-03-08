package com.chrionline.app.ui.admin;

import com.chrionline.app.model.User;
import com.chrionline.app.model.UserRole;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UsersPanel extends JPanel {

    private final ApiService api;
    private JTable table;
    private DefaultTableModel tableModel;

    public UsersPanel(ApiService api) {
        this.api = api;
        setLayout(new BorderLayout(12, 12));
        setBackground(UiConstants.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Gestion des utilisateurs"));
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Email", "Rôle", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (User u : api.getAllUsers()) {
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole(), u});
        }
        if (table.getColumnCount() >= 5) {
            table.getColumnModel().getColumn(3).setCellRenderer((tbl, value, sel, focus, row, col) -> {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                p.setOpaque(true);
                p.setBackground(sel ? tbl.getSelectionBackground() : tbl.getBackground());
                if (value instanceof UserRole) {
                    JLabel badge = new JLabel(((UserRole) value).name());
                    badge.setOpaque(true);
                    badge.setBackground(((UserRole) value) == UserRole.ADMIN ? UiConstants.ROLE_ADMIN : UiConstants.ROLE_CLIENT);
                    badge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                    p.add(badge);
                } else p.add(new JLabel(String.valueOf(value)));
                return p;
            });
        }
    }
}
