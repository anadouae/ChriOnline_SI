package com.chrionline.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests d'intégration pour vérifier que tous les modèles travaillent ensemble.
 */
@DisplayName("Model Integration Tests")
class ModelsIntegrationTest {

    @Test
    @DisplayName("Un utilisateur peut avoir un panier avec plusieurs articles")
    void testUserWithCart() {
        User user = new User(1, "John", "john@example.com", UserRole.CLIENT);
        CartItem item1 = new CartItem(1, "Product1", 2, 50.0);
        CartItem item2 = new CartItem(2, "Product2", 1, 100.0);

        assertThat(user).isNotNull();
        assertThat(item1.getTotal()).isEqualTo(100.0);
        assertThat(item2.getTotal()).isEqualTo(100.0);
        assertThat(item1.getTotal() + item2.getTotal()).isEqualTo(200.0);
    }

    @Test
    @DisplayName("Une commande peut contenir plusieurs articles avec un total valide")
    void testOrderWithMultipleItems() {
        Order order = new Order("ORD-001", "John", "john@example.com",
                java.time.LocalDateTime.now(), 0, OrderStatus.EN_ATTENTE);

        OrderItem item1 = new OrderItem("Product1", 2, 50.0);
        OrderItem item2 = new OrderItem("Product2", 1, 100.0);

        order.addItem(item1);
        order.addItem(item2);

        double totalItems = item1.getSubtotal() + item2.getSubtotal();
        assertThat(totalItems).isEqualTo(200.0);
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    @DisplayName("Un produit peut avoir un stock valide")
    void testProductStock() {
        Product product = new Product(1, "Laptop", "High-end laptop", 999.99, 10, "Electronics");

        assertThat(product.getStock()).isEqualTo(10);
        product.setStock(5);
        assertThat(product.getStock()).isEqualTo(5);
        assertThat(product.getPrice()).isPositive();
    }

    @Test
    @DisplayName("Une commande complète avec tous les détails")
    void testCompleteOrder() {
        // Créer un utilisateur
        User user = new User(1, "Alice", "alice@example.com", UserRole.CLIENT);
        assertThat(user.isAdmin()).isFalse();

        // Créer une commande
        Order order = new Order("ORD-2026-001", user.getName(), user.getEmail(),
                java.time.LocalDateTime.now(), 299.97, OrderStatus.EN_ATTENTE);

        // Ajouter des articles
        OrderItem item1 = new OrderItem("Laptop", 1, 199.99);
        OrderItem item2 = new OrderItem("Mouse", 1, 99.98);

        order.addItem(item1);
        order.addItem(item2);

        // Vérifications
        assertThat(order.getId()).isEqualTo("ORD-2026-001");
        assertThat(order.getClientName()).isEqualTo("Alice");
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotal()).isEqualTo(299.97);
    }

    @Test
    @DisplayName("Tous les statuts de commande sont disponibles")
    void testAllOrderStatusesAvailable() {
        Order order = new Order("ORD-001", "Test", "test@example.com",
                java.time.LocalDateTime.now(), 100.0, OrderStatus.EN_ATTENTE);

        // Tester la transition entre les statuts
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EN_ATTENTE);

        order.setStatus(OrderStatus.VALIDEE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.VALIDEE);

        order.setStatus(OrderStatus.EXPEDIEE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EXPEDIEE);

        order.setStatus(OrderStatus.LIVREE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.LIVREE);
    }
}

