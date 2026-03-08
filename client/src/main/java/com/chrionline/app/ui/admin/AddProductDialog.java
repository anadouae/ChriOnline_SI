package com.chrionline.app.ui.admin;

import com.chrionline.app.model.Product;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import java.awt.*;

public class AddProductDialog extends JDialog {

    private final JTextField nameField;
    private final JTextArea descField;
    private final JTextField priceField;
    private final JTextField stockField;
    private final JTextField categoryField;
    private final SaveCallback onSave;

    public interface SaveCallback {
        void onSave(Product p);
    }

    public AddProductDialog(Frame parent, SaveCallback onSave) {
        super(parent, "Ajouter un produit", true);
        this.onSave = onSave;
        setSize(450, 380);
        setLocationRelativeTo(parent);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(new JLabel("Nom"), gbc);
        gbc.gridy = 1;
        nameField = new JTextField(28);
        p.add(nameField, gbc);
        gbc.gridy = 2;
        p.add(new JLabel("Description"), gbc);
        gbc.gridy = 3;
        descField = new JTextArea(3, 28);
        p.add(new JScrollPane(descField), gbc);
        gbc.gridy = 4;
        p.add(new JLabel("Prix (€)"), gbc);
        gbc.gridy = 5;
        priceField = new JTextField(10);
        p.add(priceField, gbc);
        gbc.gridy = 6;
        p.add(new JLabel("Stock"), gbc);
        gbc.gridy = 7;
        stockField = new JTextField(10);
        p.add(stockField, gbc);
        gbc.gridy = 8;
        p.add(new JLabel("Catégorie"), gbc);
        gbc.gridy = 9;
        categoryField = new JTextField(20);
        p.add(categoryField, gbc);
        gbc.gridy = 10;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        cancel.addActionListener(e -> dispose());
        JButton save = new JButton("Enregistrer");
        save.setBackground(UiConstants.BLUE_DARK);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.addActionListener(e -> doSave());
        btns.add(cancel);
        btns.add(save);
        p.add(btns, gbc);
        add(p);
    }

    private void doSave() {
        try {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim().replace(",", "."));
            int stock = Integer.parseInt(stockField.getText().trim());
            String category = categoryField.getText().trim();
            Product prod = new Product(0, name, desc, price, stock, category);
            onSave.onSave(prod);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prix et stock doivent être des nombres.");
        }
    }
}
