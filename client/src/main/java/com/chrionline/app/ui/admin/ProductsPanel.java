package com.chrionline.app.ui.admin;

import com.chrionline.app.model.Product;
import com.chrionline.app.network.ApiService;
import com.chrionline.app.ui.components.UiConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductsPanel extends JPanel {

    private final ApiService api;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProductsPanel(ApiService api) {
        this.api = api;
        setLayout(new BorderLayout(12, 12));
        setBackground(UiConstants.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(new JLabel("Gestion des produits", SwingConstants.LEFT), BorderLayout.WEST);
        JButton addBtn = new JButton("+ Ajouter un produit");
        addBtn.setBackground(UiConstants.BLUE_DARK);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> openAddDialog());
        top.add(addBtn, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Description", "Prix (€)", "Stock", "Catégorie", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Product p : api.getProducts()) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getDescription(),
                String.format("%.2f", p.getPrice()),
                p.getStock(),
                p.getCategory(),
                p
            });
        }
        if (table.getColumnCount() >= 7) {
            table.getColumnModel().getColumn(6).setCellRenderer((tbl, value, sel, focus, row, col) -> {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
                p.setOpaque(true);
                p.setBackground(sel ? tbl.getSelectionBackground() : tbl.getBackground());
                if (value instanceof Product) {
                    JButton edit = new JButton("Modifier");
                    edit.addActionListener(e -> openEditDialog((Product) value));
                    JButton del = new JButton("Supprimer");
                    del.setForeground(Color.RED);
                    del.addActionListener(e -> deleteProduct((Product) value));
                    p.add(edit);
                    p.add(del);
                }
                return p;
            });
        }
    }

    private void openAddDialog() {
        AddProductDialog d = new AddProductDialog((Frame) SwingUtilities.getWindowAncestor(this), prod -> {
            api.addProduct(prod);
            refresh();
        });
        d.setVisible(true);
    }

    private void openEditDialog(Product p) {
        EditProductDialog d = new EditProductDialog((Frame) SwingUtilities.getWindowAncestor(this), p, prod -> {
            api.updateProduct(prod);
            refresh();
        });
        d.setVisible(true);
    }

    private void deleteProduct(Product p) {
        if (JOptionPane.showConfirmDialog(this, "Supprimer " + p.getName() + " ?") == JOptionPane.YES_OPTION) {
            api.deleteProduct(p.getId());
            refresh();
        }
    }
}
