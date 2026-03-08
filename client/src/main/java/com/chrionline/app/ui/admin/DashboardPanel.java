package com.chrionline.app.ui.admin;

import com.chrionline.app.model.Order;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.components.StatusBadge;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tableau de bord admin : KPI (commandes aujourd'hui, produits, utilisateurs, revenu) + Dernières commandes.
 */
public class DashboardPanel extends JPanel {

    private final ApiService api;

    public DashboardPanel(ApiService api) {
        this.api = api;
        setLayout(new BorderLayout(16, 16));
        setBackground(UiConstants.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new GridLayout(1, 4, 16, 0));
        top.setOpaque(false);
        top.add(buildKpiCard("Commandes aujourd'hui", String.valueOf(api.getOrdersCountToday()), "commandes"));
        top.add(buildKpiCard("Produits", String.valueOf(api.getProducts().size()), "produits au catalogue"));
        top.add(buildKpiCard("Utilisateurs", String.valueOf(api.getAllUsers().size()), "utilisateurs inscrits"));
        top.add(buildKpiCard("Revenu total", String.format("%.2f €", api.getTotalRevenue()), "toutes commandes"));
        add(top, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout(8, 8));
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createTitledBorder("Dernières commandes"));
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Date", "Total", "Statut"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        List<Order> orders = api.getAllOrders();
        for (int i = 0; i < Math.min(10, orders.size()); i++) {
            Order o = orders.get(i);
            model.addRow(new Object[]{o.getId(), o.getFormattedDate(), String.format("%.2f €", o.getTotal()), o.getStatus()});
        }
        table.getColumnModel().getColumn(3).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? tbl.getSelectionBackground() : tbl.getBackground());
            if (value instanceof com.chrionline.app.model.OrderStatus) {
                p.add(new StatusBadge((com.chrionline.app.model.OrderStatus) value));
            } else {
                p.add(new JLabel(value != null ? value.toString() : ""));
            }
            return p;
        });
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel buildKpiCard(String title, String value, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xe0e0e0)),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 22f));
        card.add(valueLabel, BorderLayout.NORTH);
        card.add(new JLabel(subtitle), BorderLayout.CENTER);
        card.add(new JLabel(title), BorderLayout.SOUTH);
        return card;
    }
}
