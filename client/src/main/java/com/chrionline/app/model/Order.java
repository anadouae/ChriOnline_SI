package com.chrionline.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Commande avec lignes et statut.
 */
public class Order {
    private String id;
    private String clientName;
    private String clientEmail;
    private LocalDateTime date;
    private double total;
    private OrderStatus status;
    private List<OrderItem> items = new ArrayList<>();

    public Order(String id, String clientName, String clientEmail, LocalDateTime date, double total, OrderStatus status) {
        this.id = id;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.date = date;
        this.total = total;
        this.status = status;
    }

    public String getId() { return id; }
    public String getClientName() { return clientName; }
    public String getClientEmail() { return clientEmail; }
    public LocalDateTime getDate() { return date; }
    public double getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }

    public void setStatus(OrderStatus status) { this.status = status; }
    public void addItem(OrderItem item) { items.add(item); }

    public String getFormattedDate() {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern("d MMMM yyyy 'à' HH:mm", Locale.FRENCH));
    }
}
