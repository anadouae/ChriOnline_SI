package com.chrionline.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour la classe CartItem.
 */
@DisplayName("CartItem Model Tests")
class CartItemTest {

    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cartItem = new CartItem(1, "Ordinateur portable", 2, 599.99);
    }

    @Test
    @DisplayName("Devrait créer un article du panier")
    void testCartItemCreation() {
        assertThat(cartItem).isNotNull();
        assertThat(cartItem.getProductId()).isEqualTo(1);
        assertThat(cartItem.getProductName()).isEqualTo("Ordinateur portable");
        assertThat(cartItem.getQuantity()).isEqualTo(2);
        assertThat(cartItem.getUnitPrice()).isEqualTo(599.99);
    }

    @Test
    @DisplayName("Devrait calculer le total correctement")
    void testGetTotal() {
        double total = cartItem.getTotal();
        assertThat(total).isEqualTo(1199.98);
    }

    @Test
    @DisplayName("Devrait calculer le total avec quantité 1")
    void testGetTotalWithQuantityOne() {
        CartItem item = new CartItem(2, "Souris", 1, 25.99);
        assertThat(item.getTotal()).isEqualTo(25.99);
    }

    @Test
    @DisplayName("Devrait calculer le total avec quantité 0")
    void testGetTotalWithZeroQuantity() {
        CartItem item = new CartItem(3, "Clavier", 0, 99.99);
        assertThat(item.getTotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait retourner le productId")
    void testGetProductId() {
        assertThat(cartItem.getProductId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Devrait retourner le productName")
    void testGetProductName() {
        assertThat(cartItem.getProductName()).isEqualTo("Ordinateur portable");
    }

    @Test
    @DisplayName("Devrait retourner la quantité")
    void testGetQuantity() {
        assertThat(cartItem.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Devrait retourner le prix unitaire")
    void testGetUnitPrice() {
        assertThat(cartItem.getUnitPrice()).isEqualTo(599.99);
    }

    @Test
    @DisplayName("Devrait gérer les prix décimaux")
    void testDecimalPrices() {
        CartItem item = new CartItem(4, "Produit", 3, 19.99);
        assertThat(item.getTotal()).isCloseTo(59.97, offset(0.01));
    }

    @Test
    @DisplayName("Devrait gérer les grandes quantités")
    void testLargeQuantity() {
        CartItem item = new CartItem(5, "Produit", 1000, 10.0);
        assertThat(item.getTotal()).isEqualTo(10000.0);
    }

    @Test
    @DisplayName("Devrait gérer les articles avec quantités négatives")
    void testNegativeQuantity() {
        CartItem item = new CartItem(6, "Produit", -5, 50.0);
        assertThat(item.getTotal()).isEqualTo(-250.0);
    }
}

