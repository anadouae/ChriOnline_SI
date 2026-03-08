package com.chrionline.app.ui.client;

import com.chrionline.app.model.Product;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Catalogue : tableau produits (gauche) + détail produit (droite) + Ajouter au panier.
 */
public class CataloguePanel extends JPanel {

    private final ApiService api;
    private final Runnable onCartChange;
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel detailPanel;
    private JSpinner quantitySpinner;
    private Product selectedProduct;

    public CataloguePanel(ApiService api, Runnable onCartChange) {
        this.api = api;
        this.onCartChange = onCartChange;
        setLayout(new BorderLayout(12, 12));
        setBackground(UiConstants.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
        loadProducts();
    }

    private void buildUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.55);
        split.setDividerSize(4);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createTitledBorder("Catalogue des produits"));
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prix", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                showProductDetail();
        });
        left.add(new JScrollPane(table), BorderLayout.CENTER);
        split.setLeftComponent(left);

        detailPanel = new JPanel(new BorderLayout(12, 12));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder("Détail du produit"));
        JLabel placeholder = new JLabel("Sélectionnez un produit dans la liste pour voir les détails");
        placeholder.setForeground(Color.GRAY);
        detailPanel.add(placeholder, BorderLayout.CENTER);
        split.setRightComponent(detailPanel);
        add(split, BorderLayout.CENTER);
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        for (Product p : api.getProducts()) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), String.format("%.2f €", p.getPrice()), p.getStock()});
        }
    }

    private void showProductDetail() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) tableModel.getValueAt(row, 0);
        selectedProduct = api.getProduct(id);
        if (selectedProduct == null) return;

        detailPanel.removeAll();
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel name = new JLabel(selectedProduct.getName());
        name.setFont(name.getFont().deriveFont(Font.BOLD, 14f));
        content.add(name, gbc);
        gbc.gridy = 1;
        content.add(new JLabel(String.format("%.2f €", selectedProduct.getPrice())), gbc);
        gbc.gridy = 2;
        content.add(new JLabel(selectedProduct.getDescription()), gbc);
        gbc.gridy = 3;
        content.add(new JLabel("Catégorie : " + selectedProduct.getCategory()), gbc);
        gbc.gridy = 4;
        content.add(new JLabel("Stock disponible: " + selectedProduct.getStock() + " unités"), gbc);
        gbc.gridy = 5;
        content.add(new JLabel("Quantité"), gbc);
        gbc.gridy = 6;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        content.add(quantitySpinner, gbc);
        gbc.gridy = 7;
        JButton addBtn = new JButton("Ajouter au panier");
        addBtn.setBackground(UiConstants.BLUE_DARK);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addToCart());
        content.add(addBtn, gbc);

        detailPanel.add(content, BorderLayout.NORTH);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private void addToCart() {
        if (selectedProduct == null) return;
        int qty = (Integer) quantitySpinner.getValue();
        if (qty > selectedProduct.getStock()) {
            JOptionPane.showMessageDialog(this, "Stock insuffisant.");
            return;
        }
        api.addToCart(selectedProduct.getId(), qty);
        JOptionPane.showMessageDialog(this, "Ajouté au panier.");
        if (onCartChange != null) onCartChange.run();
    }
}
