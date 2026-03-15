package com.chrionline.app.ui.client;

import com.chrionline.app.model.CartItem;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.network.TcpApiService;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panier : tableau Produit, Quantité, Prix unitaire, Total ; retirer ; valider commande (ouvre PaiementDialog).
 */
public class CartPanel extends JPanel {

    private final TcpApiService api;
    private final Runnable onCartChange;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;

    public CartPanel(TcpApiService api, Runnable onCartChange) {
        this.api = api;
        this.onCartChange = onCartChange;
        setLayout(new BorderLayout(12, 12));
        setBackground(UiConstants.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Mon panier"));

        tableModel = new DefaultTableModel(new String[]{"Produit", "Quantité", "Prix unitaire", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        totalLabel = new JLabel("Total : 0.00 €");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        south.add(totalLabel, BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton removeBtn = new JButton("Retirer la ligne sélectionnée");
        removeBtn.addActionListener(e -> removeSelected());
        JButton checkoutBtn = new JButton("Valider la commande (paiement)");
        checkoutBtn.setBackground(UiConstants.BLUE_DARK);
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.addActionListener(e -> doCheckout());
        buttons.add(removeBtn);
        buttons.add(checkoutBtn);
        south.add(buttons, BorderLayout.EAST);
        card.add(south, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (CartItem ci : api.getCart()) {
            tableModel.addRow(new Object[]{
                ci.getProductName(),
                Integer.valueOf(ci.getQuantity()),
                String.format("%.2f €", ci.getUnitPrice()),
                String.format("%.2f €", ci.getTotal())
            });
        }
        totalLabel.setText("Total : " + String.format("%.2f", api.getCartTotal()) + " €");
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0);
        for (CartItem ci : api.getCart()) {
            if (ci.getProductName().equals(name)) {
                api.removeFromCart(ci.getProductId());
                break;
            }
        }
        refresh();
        if (onCartChange != null) onCartChange.run();
    }

    private void doCheckout() {
        if (api.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Panier vide.");
            return;
        }
        double total = api.getCartTotal();
        PaiementDialog dialog = new PaiementDialog((Frame) SwingUtilities.getWindowAncestor(this), total, (cardNumber, expiry, cvv) -> {
            String orderId = api.validateOrder(cardNumber, expiry, cvv);
            if (orderId != null) {
                JOptionPane.showMessageDialog(CartPanel.this, "Commande enregistrée. Numéro : " + orderId);
                refresh();
                if (onCartChange != null) onCartChange.run();
            }
        });
        dialog.setVisible(true);
    }
}
