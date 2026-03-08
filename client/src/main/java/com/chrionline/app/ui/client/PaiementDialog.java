package com.chrionline.app.ui.client;

import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Modal Paiement simulé (maquette) : montant, numéro carte, expiration, CVV, Annuler / Valider.
 */
public class PaiementDialog extends JDialog {

    private final JTextField cardNumberField;
    private final JTextField expiryField;
    private final JTextField cvvField;
    private final PaiementCallback callback;

    public interface PaiementCallback {
        void onValidate(String cardNumber, String expiry, String cvv);
    }

    public PaiementDialog(Frame parent, double amount, PaiementCallback callback) {
        super(parent, "Paiement simulé", true);
        this.callback = callback;
        setSize(400, 320);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        p.add(new JLabel("Montant à payer : " + String.format("%.2f €", amount)), gbc);
        gbc.gridy = 1;
        p.add(new JLabel(" "), gbc);
        gbc.gridy = 2;
        p.add(new JLabel("Numéro de carte"), gbc);
        gbc.gridy = 3;
        cardNumberField = new JTextField(20);
        cardNumberField.setPreferredSize(new Dimension(280, 28));
        p.add(cardNumberField, gbc);
        gbc.gridy = 4;
        p.add(new JLabel("Expiration (MM/AA)"), gbc);
        gbc.gridy = 5;
        expiryField = new JTextField(6);
        expiryField.setPreferredSize(new Dimension(80, 28));
        p.add(expiryField, gbc);
        gbc.gridy = 6;
        p.add(new JLabel("CVV"), gbc);
        gbc.gridy = 7;
        cvvField = new JTextField(4);
        cvvField.setPreferredSize(new Dimension(60, 28));
        p.add(cvvField, gbc);
        gbc.gridy = 8;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        cancel.addActionListener(e -> dispose());
        JButton validate = new JButton("Valider");
        validate.setBackground(UiConstants.BLUE_DARK);
        validate.setForeground(Color.WHITE);
        validate.setFocusPainted(false);
        validate.addActionListener(e -> {
            callback.onValidate(cardNumberField.getText().trim(), expiryField.getText().trim(), cvvField.getText().trim());
            dispose();
        });
        btns.add(cancel);
        btns.add(validate);
        p.add(btns, gbc);

        add(p);
    }
}
