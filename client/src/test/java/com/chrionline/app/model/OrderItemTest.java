package com.chrionline.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe OrderItem.
 */
@DisplayName("OrderItem Model Tests")
class OrderItemTest {

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem("Smartphone", 1, 399.99);
    }

    @Test
    @DisplayName("Devrait créer un article de commande")
    void testOrderItemCreation() {
        assertThat(orderItem).isNotNull();
        assertThat(orderItem.getProductName()).isEqualTo("Smartphone");
        assertThat(orderItem.getQuantity()).isEqualTo(1);
        assertThat(orderItem.getUnitPrice()).isEqualTo(399.99);
    }

    @Test
    @DisplayName("Devrait calculer le sous-total correctement")
    void testGetSubtotal() {
        double subtotal = orderItem.getSubtotal();
        assertThat(subtotal).isEqualTo(399.99);
    }

    @Test
    @DisplayName("Devrait calculer le sous-total avec plusieurs quantités")
    void testGetSubtotalMultipleQuantity() {
        OrderItem item = new OrderItem("T-shirt", 5, 19.99);
        assertThat(item.getSubtotal()).isCloseTo(99.95, offset(0.01));
    }

    @Test
    @DisplayName("Devrait retourner le nom du produit")
    void testGetProductName() {
        assertThat(orderItem.getProductName()).isEqualTo("Smartphone");
    }

    @Test
    @DisplayName("Devrait retourner la quantité")
    void testGetQuantity() {
        assertThat(orderItem.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("Devrait retourner le prix unitaire")
    void testGetUnitPrice() {
        assertThat(orderItem.getUnitPrice()).isEqualTo(399.99);
    }

    @Test
    @DisplayName("Devrait gérer les quantités zéro")
    void testZeroQuantity() {
        OrderItem item = new OrderItem("Produit", 0, 50.0);
        assertThat(item.getSubtotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait gérer les prix zéro")
    void testZeroPrice() {
        OrderItem item = new OrderItem("Gratuit", 5, 0);
        assertThat(item.getSubtotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait gérer les noms de produits longs")
    void testLongProductName() {
        String longName = "Ordinateur Portable avec Processeur Intel Core i9 et 32 Go de RAM DDR5";
        OrderItem item = new OrderItem(longName, 1, 1999.99);
        assertThat(item.getProductName()).isEqualTo(longName);
    }

    @Test
    @DisplayName("Devrait gérer les noms de produits spéciaux")
    void testSpecialCharactersInProductName() {
        OrderItem item = new OrderItem("Café moulu - 500g (Arabica 100%)", 3, 12.99);
        assertThat(item.getProductName()).isEqualTo("Café moulu - 500g (Arabica 100%)");
        assertThat(item.getSubtotal()).isCloseTo(38.97, offset(0.01));
    }
}

