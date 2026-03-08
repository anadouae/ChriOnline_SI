package com.chrionline.app.ui.components;

import com.chrionline.app.model.OrderStatus;

import javax.swing.*;
import java.awt.*;

/**
 * Pastille de statut de commande (couleur selon maquettes).
 */
public class StatusBadge extends JLabel {

    public StatusBadge(OrderStatus status) {
        super(status.name().replace("_", " "));
        setOpaque(true);
        setBackground(colorFor(status));
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        setFont(getFont().deriveFont(Font.PLAIN, 12));
    }

    private static Color colorFor(OrderStatus s) {
        switch (s) {
            case LIVREE: return UiConstants.STATUS_LIVREE;
            case EN_ATTENTE: return UiConstants.STATUS_ATTENTE;
            case VALIDEE: return UiConstants.STATUS_VALIDEE;
            case EXPEDIEE: return UiConstants.STATUS_EXPEDIEE;
            default: return Color.GRAY;
        }
    }
}
