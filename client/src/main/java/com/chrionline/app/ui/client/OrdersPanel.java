package com.chrionline.app.ui.client;

import com.chrionline.app.model.Order;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.components.StatusBadge;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Mes commandes (client) : tableau ID, Date, Total, Statut.
 */
public class OrdersPanel extends JPanel {

    private final ApiService api;
    private JTable table;
    private DefaultTableModel tableModel;

    public OrdersPanel(ApiService api) {
        this.api = api;
        setLayout(new BorderLayout(12, 12));
        setBackground(UiConstants.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Mes commandes"));

        tableModel = new DefaultTableModel(new String[]{"ID Commande", "Date", "Total", "Statut"}, 0) {
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
        for (Order o : api.getOrdersForCurrentUser()) {
            tableModel.addRow(new Object[]{
                o.getId(),
                o.getFormattedDate(),
                String.format("%.2f €", o.getTotal()),
                o.getStatus()
            });
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
    }
}
