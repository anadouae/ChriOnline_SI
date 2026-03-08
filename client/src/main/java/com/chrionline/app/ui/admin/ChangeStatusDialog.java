package com.chrionline.app.ui.admin;

import com.chrionline.app.model.Order;
import com.chrionline.app.model.OrderItem;
import com.chrionline.app.model.OrderStatus;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import java.awt.*;

public class ChangeStatusDialog extends JDialog {

    private final Order order;
    private final JComboBox<OrderStatus> statusCombo;
    private final StatusCallback onUpdate;

    public interface StatusCallback {
        void onUpdate(OrderStatus s);
    }

    public ChangeStatusDialog(Frame parent, Order order, StatusCallback onUpdate) {
        super(parent, "Changer le statut de la commande", true);
        this.order = order;
        this.onUpdate = onUpdate;
        setSize(420, 380);
        setLocationRelativeTo(parent);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(new JLabel("Commande " + order.getId()), gbc);
        gbc.gridy = 1;
        p.add(new JLabel("Client : " + order.getClientName() + " (" + order.getClientEmail() + ")"), gbc);
        gbc.gridy = 2;
        p.add(new JLabel("Total : " + String.format("%.2f €", order.getTotal())), gbc);
        gbc.gridy = 3;
        p.add(new JLabel(" "), gbc);
        gbc.gridy = 4;
        p.add(new JLabel("Articles de la commande :"), gbc);
        gbc.gridy = 5;
        JPanel items = new JPanel(new GridLayout(0, 1, 0, 2));
        for (OrderItem it : order.getItems()) {
            items.add(new JLabel("• " + it.getProductName() + " x" + it.getQuantity() + " - " + String.format("%.2f €", it.getUnitPrice())));
        }
        p.add(new JScrollPane(items), gbc);
        gbc.gridy = 6;
        p.add(new JLabel("Nouveau statut"), gbc);
        gbc.gridy = 7;
        statusCombo = new JComboBox<>(OrderStatus.values());
        statusCombo.setSelectedItem(order.getStatus());
        p.add(statusCombo, gbc);
        gbc.gridy = 8;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        cancel.addActionListener(e -> dispose());
        JButton update = new JButton("Mettre à jour");
        update.setBackground(UiConstants.BLUE_DARK);
        update.setForeground(Color.WHITE);
        update.setFocusPainted(false);
        update.addActionListener(e -> {
            onUpdate.onUpdate((OrderStatus) statusCombo.getSelectedItem());
            dispose();
        });
        btns.add(cancel);
        btns.add(update);
        p.add(btns, gbc);
        add(p);
    }
}
