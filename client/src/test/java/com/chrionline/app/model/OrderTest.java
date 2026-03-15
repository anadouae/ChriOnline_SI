package com.chrionline.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe Order.
 */
@DisplayName("Order Model Tests")
class OrderTest {

    private Order order;
    private LocalDateTime orderDate;

    @BeforeEach
    void setUp() {
        orderDate = LocalDateTime.of(2026, Month.MARCH, 11, 14, 30, 0);
        order = new Order("ORD-001", "Jean Dupont", "jean@example.com", orderDate, 1199.98, OrderStatus.EN_ATTENTE);
    }

    @Test
    @DisplayName("Devrait créer une commande")
    void testOrderCreation() {
        assertThat(order).isNotNull();
        assertThat(order.getId()).isEqualTo("ORD-001");
        assertThat(order.getClientName()).isEqualTo("Jean Dupont");
        assertThat(order.getClientEmail()).isEqualTo("jean@example.com");
        assertThat(order.getDate()).isEqualTo(orderDate);
        assertThat(order.getTotal()).isEqualTo(1199.98);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EN_ATTENTE);
    }

    @Test
    @DisplayName("Devrait retourner une liste vide d'articles au départ")
    void testEmptyItemsInitially() {
        assertThat(order.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Devrait ajouter un article à la commande")
    void testAddItem() {
        OrderItem item = new OrderItem("Smartphone", 1, 399.99);
        order.addItem(item);

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0)).isEqualTo(item);
    }

    @Test
    @DisplayName("Devrait ajouter plusieurs articles à la commande")
    void testAddMultipleItems() {
        OrderItem item1 = new OrderItem("Smartphone", 1, 399.99);
        OrderItem item2 = new OrderItem("T-shirt", 2, 19.99);
        OrderItem item3 = new OrderItem("Café moulu", 3, 12.99);

        order.addItem(item1);
        order.addItem(item2);
        order.addItem(item3);

        assertThat(order.getItems()).hasSize(3);
        assertThat(order.getItems())
                .contains(item1, item2, item3);
    }

    @Test
    @DisplayName("Devrait changer le statut d'une commande")
    void testSetStatus() {
        order.setStatus(OrderStatus.VALIDEE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.VALIDEE);

        order.setStatus(OrderStatus.EXPEDIEE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EXPEDIEE);

        order.setStatus(OrderStatus.LIVREE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.LIVREE);
    }

    @Test
    @DisplayName("Devrait retourner l'ID de la commande")
    void testGetId() {
        assertThat(order.getId()).isEqualTo("ORD-001");
    }

    @Test
    @DisplayName("Devrait retourner le nom du client")
    void testGetClientName() {
        assertThat(order.getClientName()).isEqualTo("Jean Dupont");
    }

    @Test
    @DisplayName("Devrait retourner l'email du client")
    void testGetClientEmail() {
        assertThat(order.getClientEmail()).isEqualTo("jean@example.com");
    }

    @Test
    @DisplayName("Devrait retourner la date de la commande")
    void testGetDate() {
        assertThat(order.getDate()).isEqualTo(orderDate);
    }

    @Test
    @DisplayName("Devrait retourner le total de la commande")
    void testGetTotal() {
        assertThat(order.getTotal()).isEqualTo(1199.98);
    }

    @Test
    @DisplayName("Devrait retourner le statut de la commande")
    void testGetStatus() {
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EN_ATTENTE);
    }

    @Test
    @DisplayName("Devrait formater la date correctement")
    void testGetFormattedDate() {
        String formatted = order.getFormattedDate();
        assertThat(formatted).contains("11");
        assertThat(formatted).contains("mars");
        assertThat(formatted).contains("2026");
    }

    @Test
    @DisplayName("Devrait gérer une date null")
    void testGetFormattedDateWithNull() {
        Order orderWithoutDate = new Order("ORD-002", "Test", "test@example.com", null, 100.0, OrderStatus.EN_ATTENTE);
        assertThat(orderWithoutDate.getFormattedDate()).isEmpty();
    }

    @Test
    @DisplayName("Devrait supporter tous les statuts de commande")
    void testAllOrderStatuses() {
        for (OrderStatus status : OrderStatus.values()) {
            order.setStatus(status);
            assertThat(order.getStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Devrait gérer les totaux décimaux")
    void testDecimalTotals() {
        Order decimalOrder = new Order("ORD-003", "Test", "test@example.com", orderDate, 99.99, OrderStatus.EN_ATTENTE);
        assertThat(decimalOrder.getTotal()).isEqualTo(99.99);
    }
}

