package com.chrionline.app.ui.admin;

import com.chrionline.app.model.Order;
import com.chrionline.app.model.OrderStatus;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.components.StatusBadge;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminOrdersPanel extends JPanel {

    private final ApiService api;
    private JTable table;
    private DefaultTableModel tableModel;

    public AdminOrdersPanel(ApiService api) {
        this.api = api;
        setLayout(new BorderLayout(12, 12));
        setBackground(UiConstants.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Gestion des commandes"));
        tableModel = new DefaultTableModel(new String[]{"ID", "Client", "Date", "Total", "Statut", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Order o : api.getAllOrders()) {
            tableModel.addRow(new Object[]{
                o.getId(),
                o.getClientName() + " (" + o.getClientEmail() + ")",
                o.getFormattedDate(),
                String.format("%.2f €", o.getTotal()),
                o.getStatus(),
                o
            });
        }
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionsCellRenderer(this));
    }

    void openChangeStatusDialog(Order order) {
        ChangeStatusDialog.StatusCallback cb = newStatus -> {
            api.updateOrderStatus(order.getId(), newStatus);
            refresh();
        };
        ChangeStatusDialog d = new ChangeStatusDialog((Frame) SwingUtilities.getWindowAncestor(this), order, cb);
        d.setVisible(true);
    }

    private static class StatusCellRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean sel, boolean focus, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            p.setOpaque(true);
            p.setBackground(sel ? tbl.getSelectionBackground() : tbl.getBackground());
            if (value instanceof OrderStatus) p.add(new StatusBadge((OrderStatus) value));
            else p.add(new JLabel(String.valueOf(value)));
            return p;
        }
    }

    private static class ActionsCellRenderer implements javax.swing.table.TableCellRenderer {
        private final AdminOrdersPanel panel;

        ActionsCellRenderer(AdminOrdersPanel panel) { this.panel = panel; }

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean sel, boolean focus, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(true);
            p.setBackground(sel ? tbl.getSelectionBackground() : tbl.getBackground());
            if (value instanceof Order) {
                JButton btn = new JButton("Changer statut");
                btn.addActionListener(e -> panel.openChangeStatusDialog((Order) value));
                p.add(btn);
            }
            return p;
        }
    }
}
