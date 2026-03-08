package com.chrionline.app.ui.admin;

import com.chrionline.app.model.Product;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import java.awt.*;

public class EditProductDialog extends JDialog {

    private final Product product;
    private final JTextField nameField;
    private final JTextArea descField;
    private final JTextField priceField;
    private final JTextField stockField;
    private final JTextField categoryField;
    private final java.util.function.Consumer<Product> onSave;

    public EditProductDialog(Frame parent, Product product, java.util.function.Consumer<Product> onSave) {
        super(parent, "Modifier le produit", true);
        this.product = product;
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
        nameField.setText(product.getName());
        p.add(nameField, gbc);
        gbc.gridy = 2;
        p.add(new JLabel("Description"), gbc);
        gbc.gridy = 3;
        descField = new JTextArea(3, 28);
        descField.setText(product.getDescription());
        p.add(new JScrollPane(descField), gbc);
        gbc.gridy = 4;
        p.add(new JLabel("Prix (€)"), gbc);
        gbc.gridy = 5;
        priceField = new JTextField(10);
        priceField.setText(String.valueOf(product.getPrice()));
        p.add(priceField, gbc);
        gbc.gridy = 6;
        p.add(new JLabel("Stock"), gbc);
        gbc.gridy = 7;
        stockField = new JTextField(10);
        stockField.setText(String.valueOf(product.getStock()));
        p.add(stockField, gbc);
        gbc.gridy = 8;
        p.add(new JLabel("Catégorie"), gbc);
        gbc.gridy = 9;
        categoryField = new JTextField(20);
        categoryField.setText(product.getCategory());
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
            product.setName(nameField.getText().trim());
            product.setDescription(descField.getText().trim());
            product.setPrice(Double.parseDouble(priceField.getText().trim().replace(",", ".")));
            product.setStock(Integer.parseInt(stockField.getText().trim()));
            product.setCategory(categoryField.getText().trim());
            onSave.accept(product);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prix et stock doivent être des nombres.");
        }
    }
}
